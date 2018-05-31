import {Component, OnInit, ViewChild} from '@angular/core';
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog, MatTableDataSource, MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../HttpService";
import {Client} from '../models/Client';
import {SelectionModel} from '@angular/cdk/collections';
import {ErrorDialogComponent} from "../error_dialog/error.dialog.component";
import {AreYouSureDialogComponent} from "../are_you_sure_dialog/are.you.sure.dialog.component";

@Component({
    selector: 'editable-list',
    template: require('./editable-list-component.html'),
    styles: [require('./editable-list-component.css')],
})
export class EditableListComponent implements OnInit {

    dataSource;
    displayedColumns = ['name', 'charm', 'age', 'total', 'max', 'min'];
    selection = new SelectionModel<Client>(false, null);
    clientId: string;
    ELEMENTS: Client[] = [];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    constructor(private dialog: MatDialog, private http: HttpService) {
    }

    ngOnInit() {
        this.get();
        this.selection.onChange.subscribe((a) => {
            if (a.added[0]) {
                this.clientId = a.added[0].id;
            }
        });
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    openDialog(whichDialogNeeded): void {
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
        } else {
            let dialogRef = this.dialog.open(DialogComponent, {
                width: '550px',
                height: '500px',
                data: {whichDialogNeeded: whichDialogNeeded, clientId: this.clientId}
            });
        }
    }

    openErrorDialog(): void {
        let dialogRef = this.dialog.open(ErrorDialogComponent, {});
    }

    get() {
        this.http.get("/client/get_all_clients").toPromise().then(result => {

            // alert(JSON.stringify(result.json()));
            for (let i = 0; i < Number(JSON.stringify(result.json().length)); i++) {
                let Client = {
                    id: JSON.stringify(result.json()[i].id).replace(/["]+/g, ''),
                    name: JSON.stringify(result.json()[i].name).replace(/["]+/g, ''),
                    charm: JSON.stringify(result.json()[i].charm).replace(/["]+/g, ''),
                    age: JSON.stringify(result.json()[i].age).replace(/["]+/g, ''),
                    total_account_balance: JSON.stringify(result.json()[i].total).replace(/["]+/g, ''),
                    max_balance: JSON.stringify(result.json()[i].max).replace(/["]+/g, ''),
                    min_balance: JSON.stringify(result.json()[i].min).replace(/["]+/g, '')
                };
                this.ELEMENTS.push(Client);
            }
            this.dataSource = new MatTableDataSource(this.ELEMENTS);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
        }).catch(error => {
        })
    }


    addElement() { // TODO test
        this.ELEMENTS.push({
            id: 'fewds', name: 'rdwf', charm: 'vwdfcw', age: 'vwdfcw', total_account_balance: 'vtef',
            max_balance: 'fewdewd', min_balance: 'vrdfcwd'
        });
        this.dataSource = new MatTableDataSource(this.ELEMENTS);
    }

}
