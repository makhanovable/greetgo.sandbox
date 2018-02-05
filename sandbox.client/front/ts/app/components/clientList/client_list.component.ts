import { HttpService } from './../../HttpService';
import { Charm } from './../../../model/Charm';
import { ClientInfo } from './../../../model/ClientInfo';

import { Component, ViewChild, OnInit } from '@angular/core';
import { MatTableDataSource, MatDialog, MatSort } from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { ClientFormComponent } from '../clientForm/client_form.component';


@Component({
    template: require('./client_list.component.html'),
    selector: 'client-list-component',
    styles: [require('./client_list.component.css')],
})
export class ClientListComponent implements OnInit {

    displayedColumns = ['select', 'fio', 'charm', 'age', 'totalAccountBalance', 'maximumBalance', 'minimumBalance'];
    dataSource: MatTableDataSource<ClientInfo>;

    charmsMap: any[] = [];
    charmsArray: Charm[];

    tableElemets: ClientInfo[];
    selection = new SelectionModel<ClientInfo>(true, []);

    filter: string = ''
    orderByColumns: string[] = ['Age', 'Total account balance', 'Maximum balance', 'Minimum balance'];
    selectedOrder = 0;
    curPage: number = 0;
    rowsPerPage: number = 10;

    @ViewChild(MatSort) sort: MatSort;

    constructor(public dialog: MatDialog, private httpService: HttpService) {

    }

    ngOnInit() {
        this.loadTableData(undefined, this.curPage, this.rowsPerPage);
        this.loadChamrs();
    }

    loadChamrs() {
        this.charmsArray = [{ id: "0", name: "ленивый" }, { id: "3", name: "пунктуальный" }] as Charm[];
        this.charmsArray.forEach(element => {
            this.charmsMap[element.id] = element;
        });

    }

    loadTableData(filter: string, pageNumber: number, rowsPerPage: number) {

        this.httpService.get("/client/get", {

        }).toPromise().then(res => {
            this.tableElemets = JSON.parse(res.text()) as ClientInfo[];
            
            this.initMatTable();
            console.log(res);
            console.log(this.tableElemets);
        }).catch(error => {
            console.log(error);
        
        })

        // this.tableElemets = [
        //     {
        //         id: 1, age: 17, patronymic: ".",
        //         name: "Harry", surname: "Potter", charmId:"0",
        //         totalAccountBalance: 1, maximumBalance: 2, minimumBalance: 3
        //     },
        //     {
        //         id: 2, age: 17, patronymic: "Minata",
        //         name: "Naruto", surname: "Uzum", charmId:"0",
        //         totalAccountBalance: 2, maximumBalance: 1, minimumBalance: 3
        //     },
        //     {
        //         id: 3, age: 17, patronymic: "D.",
        //         name: "Dinara", surname: "Amze", charmId:"0",
        //         totalAccountBalance: 3, maximumBalance: 0, minimumBalance: 3
        //     },
        //     {
        //         id: 4, age: 17, patronymic: "D.",
        //         name: "Dauren", surname: "Amze", charmId:"0",
        //         totalAccountBalance: 4, maximumBalance: -1, minimumBalance: 3
        //     },

        // ] as ClientInfo[];

        // this.initMatTable();
    }

    initMatTable() {
        this.selection.clear();
        this.dataSource = new MatTableDataSource<ClientInfo>(this.tableElemets);
        this.dataSource.sort = this.sort
    }

    applyFilter() {
        this.loadTableData(this.filter, this.curPage, this.rowsPerPage);
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
            maxWidth: "1000px",
            // position: {top:"100px"} ,

            data: {
                charms: this.charmsArray,
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

    ping() {
        console.log('pong')
    }

    //set a- set b
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

    // calculateAge(birthday) {
    //     var ageDifMs = Date.now() - birthday.getTime();
    //     var ageDate = new Date(ageDifMs);
    //     return Math.abs(ageDate.getUTCFullYear() - 1970);
    // }

}