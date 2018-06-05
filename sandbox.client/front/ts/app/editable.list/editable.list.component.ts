import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog, MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../HttpService";
import {Client} from '../models/client.record';
import {SelectionModel} from '@angular/cdk/collections';
import {ErrorDialogComponent} from "../error.dialog/error.dialog.component";
import {AreYouSureDialogComponent} from "../are.you.sure.dialog/are.you.sure.dialog.component";
import {fromEvent} from 'rxjs/observable/fromEvent';
import {merge, of as observableOf} from 'rxjs';
import {Observable} from "rxjs/index";
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {ClientDataSource} from "../services/client.data.source";
import {CharmService} from "../services/charm.service";
import {ClientWrapper} from "../models/client.wrapper";

@Component({
    selector: 'editable-list',
    template: require('./editable.list.component.html'),
    styles: [require('./editable.list.component.css')],
})
export class EditableListComponent implements OnInit {

    displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];
    exampleDatabase: ClientDataSource | null;

    data: Client[] = [];
    temp: Client[] = [];
    resultsLength = 0;
    isLoadingResults = true;
    isRateLimitReached = false;
    clientId: number;
    selection = new SelectionModel<Client>(false, null);

    mustLoadFromNet: boolean = true;

    displayedList: Observable<ClientWrapper>;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('input') input: ElementRef;

    constructor(private dialog: MatDialog,
                private http: HttpService,
                private charmService: CharmService) {
    }

    ngOnInit() {
        this.charmService.getCharms();
        this.exampleDatabase = new ClientDataSource(this.http, this.charmService);
        this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

        fromEvent(this.input.nativeElement, 'keyup')
            .pipe(
                startWith({}),
                switchMap(() => {
                    this.isLoadingResults = true;
                    this.paginator.pageIndex = 0;
                    return this.displayedList = this.exampleDatabase!.getClients(this.input.nativeElement.value,
                        this.sort.active, this.sort.direction, this.paginator.pageIndex,
                        this.paginator.pageSize);
                }),
                map(data => {
                    this.isLoadingResults = false;
                    this.isRateLimitReached = false;
                    this.resultsLength = data.total_count;

                    return data.items;
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
                        this.displayedList = this.exampleDatabase!.getClients(this.input.nativeElement.value,
                            this.sort.active, this.sort.direction, this.paginator.pageIndex,
                            this.paginator.pageSize);
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
                    this.resultsLength = data.total_count;
                    console.log(data.items.length + " data length");

                    return data.items;
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
            let dialogRef = this.dialog.open(AreYouSureDialogComponent, {
                data: {clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                this.loadData();
                this.clientId = null;
            });

        } else if (whichDialogNeeded == 1) {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '550px',
                height: '500px',
                data: {whichDialogNeeded: whichDialogNeeded, clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                this.addRowToList(result);
            });
        } else {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '550px',
                height: '500px',
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
            let ClientWrapper = {
                items: this.temp,
                total_count: this.resultsLength + 1
            };
            this.temp = [];
            this.displayedList = observableOf(ClientWrapper);
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
            let ClientWrapper = {
                items: this.temp,
                total_count: this.resultsLength
            };
            this.temp = [];
            this.displayedList = observableOf(ClientWrapper);
            this.mustLoadFromNet = false;
            this.loadData();
            this.clientId = null;
        }
    }

}