import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {DialogComponent} from "../edit.dialog/edit.dialog.component";
import {MatDialog, MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../HttpService";
import {SelectionModel} from '@angular/cdk/collections';
import {ErrorDialogComponent} from "../error.dialog/error.dialog.component";
import {DeleteDialogComponent} from "../delete.dialog/delete.dialog.component";
import {fromEvent} from 'rxjs/observable/fromEvent';
import {merge, of as observableOf} from 'rxjs';
import {Observable} from "rxjs/index";
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {ClientRecord} from "../../model/client.record";
import {Options} from "../../model/options";
import {DataSourceService} from "../services/data.source.service";
import {saveAs} from 'file-saver/FileSaver';
import {SortBy} from "../../model/sort.by";

@Component({
    selector: 'client-list',
    template: require('./client.list.component.html'),
    styles: [require('./client.list.component.css')],
})
export class ClientListComponent implements OnInit {

    //@Output() exit = new EventEmitter<void>();
    GET_CLIENTS_URL: string = "/client/get-list";
    GET_CLIENTS_COUNT_URL: string = "/client/get-list-count";

    displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];

    data: ClientRecord[] = [];
    temp: ClientRecord[] = [];

    resultsLength = 0;
    isLoadingResults = true;
    isRateLimitReached = false;
    clientId: number;
    selection = new SelectionModel<ClientRecord>(false, null);

    mustLoadFromNet: boolean = true;
    options: Options;

    displayedList: Observable<ClientRecord[]>;
    selected: string;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('input') input: ElementRef;

    constructor(private dialog: MatDialog,
                private http: HttpService, private dataSource: DataSourceService) {
    }

    ngOnInit() {
        this.options = new Options();
        this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

        fromEvent(this.input.nativeElement, 'keyup')
            .pipe(
                startWith({}),
                switchMap(() => {
                    this.isLoadingResults = true;
                    this.paginator.pageIndex = 0;
                    this.options.filter = this.input.nativeElement.value;
                    this.options.sort = SortBy[this.sort.active];
                    this.options.order = this.sort.direction;
                    this.options.page = this.paginator.pageIndex;
                    if (this.paginator.pageSize == null)
                        this.options.size = 5;
                    else
                        this.options.size = this.paginator.pageSize;
                    return this.displayedList = this.dataSource!.getClients(this.GET_CLIENTS_URL, this.options, 0);
                }),
                map(data => {
                    this.isLoadingResults = false;
                    this.isRateLimitReached = false;
                    this.getClientsCount(this.options.filter);
                    console.log("get count");
                    return data;
                }),
                catchError(() => {
                    this.isLoadingResults = false;
                    this.isRateLimitReached = true;
                    return observableOf([]);
                })
            ).subscribe(data => this.data = data);

        this.loadData();
        this.selection.onChange.subscribe((a) => {
            if (a.added[0]) {
                this.clientId = a.added[0].id;
                console.log('clicked client id = ' + this.clientId);
            }
        });
    }

    loadData(): void {
        merge(this.sort.sortChange, this.paginator.page)
            .pipe(
                startWith({}),
                switchMap(() => {
                    this.isLoadingResults = true;
                    if (this.mustLoadFromNet) {
                        console.log('loading data from net');
                        this.options = new Options();
                        this.options.filter = this.input.nativeElement.value;
                        this.options.sort = SortBy[this.sort.active];
                        this.options.order = this.sort.direction;
                        this.options.page = this.paginator.pageIndex;
                        if (this.paginator.pageSize == null)
                            this.options.size = 5;
                        else
                            this.options.size = this.paginator.pageSize;
                        this.displayedList = this.dataSource!.getClients(this.GET_CLIENTS_URL, this.options, 0);
                        return this.displayedList;
                    } else {
                        console.log('loading data NOT from net');
                        this.mustLoadFromNet = true;
                        return this.displayedList;
                    }
                }),
                map(data => {
                    this.isLoadingResults = false;
                    this.isRateLimitReached = false;
                    return data;
                }),
                catchError(() => {
                    this.isLoadingResults = false;
                    this.isRateLimitReached = true;
                    return observableOf([]);
                })
            ).subscribe(data => this.data = data);
    }

    openDialog(whichDialogNeeded): void {
        console.log(this.clientId + " clientId");
        if (whichDialogNeeded != 1) {
            if (this.clientId == null) {
                this.openErrorDialog();
            } else {
                this.formDialog(whichDialogNeeded);
            }
        } else {
            this.formDialog(whichDialogNeeded);
        }
    }

    formDialog(whichDialogNeeded): void {
        if (whichDialogNeeded == 0) {
            let dialogRef = this.dialog.open(DeleteDialogComponent, {
                data: {clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.http.post("/client/delete", {
                        clientId: this.clientId
                    }).toPromise().then(res => {
                        this.loadData();
                        this.resultsLength = this.resultsLength - 1;
                        this.clientId = null;
                    }, error => {
                        alert("Error " + error);
                    });
                }
            });

        } else if (whichDialogNeeded == 1) {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '600px',
                height: '600px',
                data: {whichDialogNeeded: whichDialogNeeded, clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                this.addRowToList(result);
            });
        } else {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '600px',
                height: '600px',
                data: {whichDialogNeeded: whichDialogNeeded, clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                this.editRowOnList(result);
            });
        }
    }

    openErrorDialog(): void {
        let dialogRef = this.dialog.open(ErrorDialogComponent, {});
    }

    addRowToList(result) {
        if (result != null && result != '') {
            console.log(this.data.length);
            for (let i = 0; i < this.data.length; i++) {
                this.temp.push(this.data[i]);
            }
            this.temp.push(result);
            this.resultsLength = this.resultsLength + 1;
            this.displayedList = observableOf(this.temp);
            this.temp = [];
            this.mustLoadFromNet = false;
            this.loadData();
        }
    }

    editRowOnList(result) {
        if (result != null && result != '') {
            let row: number = 0;
            for (let i = 0; i < this.data.length; i++) {
                if (this.data[i].id == result.id) {
                    row = i;
                    break;
                }
            }
            for (let i = 0; i < this.data.length; i++) {
                if (i == row) {
                    this.temp.push(result);
                }
                else {
                    this.temp.push(this.data[i])
                }
            }
            this.displayedList = observableOf(this.temp);
            this.mustLoadFromNet = false;
            this.loadData();
            this.temp = [];
            this.clientId = null;
        }
    }

    loadReport(res) {
        this.options.page = 0;
        this.options.size = this.resultsLength;

        if (res.value == 'pdf') {
            this.pdf();
        } else if (res.value == 'xlsx')
            this.xlsx()
    }

    pdf() {
        this.http.download("/client/get-report/pdf", {
            options: JSON.stringify(this.options)
        }).toPromise().then(res => {
            const file = new Blob([res], {type: 'application/pdf'});
            saveAs(file, new Date().toLocaleString() + ".pdf");
            this.selected = null;
        }, error => {
            alert(error);
        });
    }

    xlsx() {
        this.http.download("/client/render-list/xlsx", {
            options: JSON.stringify(this.options)
        }).toPromise().then(res => {
            const file = new Blob([res], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
            saveAs(file, new Date().toLocaleString() + ".xlsx");
            this.selected = null;
        }, error => {
            alert(error);
        });
    }

    getClientsCount(filter) {
        this.http.get(this.GET_CLIENTS_COUNT_URL, {
            filter: filter
        }).toPromise().then(res => {
            this.resultsLength = res.json();
        })
    }

}