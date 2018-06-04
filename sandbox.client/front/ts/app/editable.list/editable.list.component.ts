import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog, MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../HttpService";
import {Client} from '../models/client';
import {SelectionModel} from '@angular/cdk/collections';
import {ErrorDialogComponent} from "../error.dialog/error.dialog.component";
import {AreYouSureDialogComponent} from "../are.you.sure.dialog/are.you.sure.dialog.component";
import {debounceTime, distinctUntilChanged, tap} from 'rxjs/operators';
import {fromEvent} from 'rxjs/observable/fromEvent';
import {merge, of as observableOf} from 'rxjs';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {ClientDataSource} from "../client.data.source";
import {CharmService} from "../services/charm.service";
import {ClientsInfoService} from "../services/clients.info.service";

@Component({
    selector: 'editable-list',
    template: require('./editable.list.component.html'),
    styles: [require('./editable.list.component.css')],
})
export class EditableListComponent implements OnInit {

    displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];
    exampleDatabase: ClientDataSource | null;

    data: Client[] = [];
    resultsLength = 0;
    isLoadingResults = true;
    isRateLimitReached = false;
    clientId: number;
    selection = new SelectionModel<Client>(false, null);

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('input') input: ElementRef;

    constructor(private dialog: MatDialog,
                private http: HttpService,
                private charmService: CharmService,
                private clientInfo: ClientsInfoService) {
    }

    ngOnInit() {
        this.charmService.getCharms();
        this.selection.onChange.subscribe((a) => {
            if (a.added[0]) {
                this.clientId = a.added[0].id;
                console.log('clicked client id = ' + this.clientId);
            }
        });
        this.exampleDatabase = new ClientDataSource(this.http, this.charmService, this.clientInfo);

        fromEvent(this.input.nativeElement, 'keyup')
            .pipe(
                startWith({}),
                switchMap(() => {
                    this.isLoadingResults = true;
                    this.paginator.pageIndex = 0;
                    return this.exampleDatabase!.getClients(this.input.nativeElement.value,
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

        this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

        merge(this.sort.sortChange, this.paginator.page)
            .pipe(
                startWith({}),
                switchMap(() => {
                    this.isLoadingResults = true;
                    return this.exampleDatabase!.getClients(this.input.nativeElement.value,
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
                this.ngOnInit();
                this.clientId = null;
            });

        } else {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '550px',
                height: '500px',
                data: {whichDialogNeeded: whichDialogNeeded, clientId: this.clientId}
            });
            dialogRef.afterClosed().subscribe(result => {
                this.ngOnInit();
                this.clientId = null;
            });
        }
    }

    openErrorDialog(): void {
        let dialogRef = this.dialog.open(ErrorDialogComponent, {});
    }

}