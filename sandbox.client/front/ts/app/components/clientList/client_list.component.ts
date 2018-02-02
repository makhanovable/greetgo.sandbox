import { ClientInfo } from './../../../model/ClientInfo';

import { Component, ViewChild, OnInit } from '@angular/core';
import { MatTableDataSource, MatDialog, MatSort } from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { ClientFormComponent } from '../clientForm/client_form.component';


@Component({
    selector: 'client-list-component',
    template: require('./client_list.component.html'),
    styles: [require('./client_list.component.css')],
})
export class ClientListComponent implements OnInit {

    displayedColumns = ['select', 'fio', 'charm', 'age', 'totalAccountBalance', 'maximumBalance', 'minimumBalance'];
    dataSource: MatTableDataSource<ClientInfo>;

    tableElemets: ClientInfo[];
    selection = new SelectionModel<ClientInfo>(true, []);

    @ViewChild(MatSort) sort: MatSort;

    constructor(public dialog: MatDialog) {

    }

    ngOnInit() {

        this.initTableData();
        this.initMatTable();
    }

    initTableData() {
        this.tableElemets = [
            {
                id: 1, birthDay: new Date("September 4, 1996"), patronymic: ".",
                name: "Harry", surname: "Potter", charm: { name: "ленивый" },
                totalAccountBalance: 1, maximumBalance: 2, minimumBalance: 3
            },
            {
                id: 2, birthDay: new Date("September 4, 1996"), patronymic: "Minata",
                name: "Naruto", surname: "Uzum", charm: { name: "ленивый" },
                totalAccountBalance: 2, maximumBalance: 1, minimumBalance: 3
            },
            {
                id: 3, birthDay: new Date("September 4, 1996"), patronymic: "D.",
                name: "Dinara", surname: "Amze", charm: { name: "ленивый" },
                totalAccountBalance: 3, maximumBalance: 0, minimumBalance: 3
            },
            {
                id: 4, birthDay: new Date("September 13, 2000"), patronymic: "D.",
                name: "Dauren", surname: "Amze", charm: { name: "ленивый" },
                totalAccountBalance: 4, maximumBalance: -1, minimumBalance: 3
            },

        ] as ClientInfo[];
    }

    initMatTable() {
        this.dataSource = new MatTableDataSource<ClientInfo>(this.tableElemets);
        this.dataSource.filterPredicate = this.filterPredicate;
        this.dataSource.sort = this.sort
    }

    filterPredicate(data, filter) {
        if (data.name.toLowerCase().indexOf(filter) >= 0
            || data.surname.toLowerCase().indexOf(filter) >= 0
            || data.patronymic.toLowerCase().indexOf(filter) >= 0)
            return true;
        return false
    };

    calculateAge(birthday) {
        var ageDifMs = Date.now() - birthday.getTime();
        var ageDate = new Date(ageDifMs);
        return Math.abs(ageDate.getUTCFullYear() - 1970);
    }

    add() {
        console.log('add button clicked')
        this.openClientModalForm(undefined);
    }

    editSelectedItem() {
        this.openClientModalForm(this.selection.selected[0]);
    }

    removeSelectedItems() {
        // TODO send removed items to server
        var diff = this.ClientInfoArrayDiff(this.tableElemets, this.selection.selected);
        this.tableElemets = diff;
        this.updateMatTable()
    }

    updateMatTable() {
        this.selection.clear();
        this.initMatTable();
    }

    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
    }

    masterToggle() {
        this.isAllSelected() ?
            this.selection.clear() :
            this.dataSource.data.forEach(row => this.selection.select(row));
    }

    openClientModalForm(selected): void {
        let dialogRef = this.dialog.open(ClientFormComponent, {
            // minWidth: '250px',
            data: {
                item: selected,
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            let tmp: ClientInfo = result as ClientInfo;
            console.log('The dialog was closed');
            console.log(result);
            
            if (result) {
                for (let i = 0; i < this.tableElemets.length; i++) {
                    if (this.tableElemets[i].id == tmp.id) {
                        this.tableElemets[i] = tmp;
                        this.updateMatTable();
                        return;
                    }
                }
                this.tableElemets.push(tmp);
                this.updateMatTable();
            }
        });
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim();
        filterValue = filterValue.toLowerCase();
        this.dataSource.filter = filterValue;
    }

    notImpl() {
        alert("not Implemented")
    }
    ping() {
        console.log('pong')
    }

    //a-b
    ClientInfoArrayDiff(a: ClientInfo[], b: ClientInfo[]): ClientInfo[] {

        var tmp = [];
        var diff: ClientInfo[] = [];

        b.forEach(element => {
            tmp[element.id] = true;
        });

        for (let i = 0; i < a.length; i++)
            if (!tmp[a[i].id])
                diff.push(a[i])

        return diff;
    }

}