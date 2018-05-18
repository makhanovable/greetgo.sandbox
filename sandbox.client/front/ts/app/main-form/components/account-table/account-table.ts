import {Component, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {Observable} from "rxjs/Observable";
import {AccountService} from "../../../services/AccountService";
import {AccountInfo} from "../../../../model/AccountInfo";
import {HttpService} from "../../../HttpService";
import {AccountInfoDataSource} from "./AccountInfoDataSource";


@Component({
  selector: 'table-basic-example',
  styles: [require('./account-table.css')],
  template: require('./account-table.html'),
})
export class AccountTableComponent {

  dataSource: AccountInfoDataSource;
  displayedColumns = ['select', 'fio', 'charm', 'age', 'total', 'max', 'min'];

  constructor(private httpService: HttpService) { }

  ngOnInit() {
    this.dataSource = new AccountInfoDataSource(this.httpService);
    this.dataSource.loadAccountInfoList();
  }

  onRowClicked(row) {
    // this.selection.toggle(row);
    console.log(row.position);
  }

}
