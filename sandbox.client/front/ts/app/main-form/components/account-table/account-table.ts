import {Component, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {Observable} from "rxjs/Observable";
import {AccountService} from "../../../services/AccountService";
import {AccountInfo} from "../../../../model/AccountInfo";
import {HttpService} from "../../../HttpService";
import {AccountInfoDataSource} from "./AccountInfoDataSource";
import {tap} from "rxjs/operators";


@Component({
  selector: 'table-basic-example',
  styles: [require('./account-table.css')],
  template: require('./account-table.html'),
})
export class AccountTableComponent {

  dataSource: AccountInfoDataSource;
  displayedColumns = ['fio', 'charm', 'age', 'total', 'max', 'min'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private httpService: HttpService) { }

  ngOnInit() {
    this.dataSource = new AccountInfoDataSource(this.httpService);
    this.dataSource.loadAccountInfoList();
  }

  ngAfterViewInit() {

    this.sort.sortChange.subscribe(() => {
      this.paginator.pageIndex = 0;
      this.loadAccountInfoPage();
    });

    this.paginator.page
      .pipe(
        tap(() => this.loadAccountInfoPage())
      ).subscribe();
  }

  loadAccountInfoPage() {
    this.dataSource.loadAccountInfoList(
      this.paginator.pageIndex, this.paginator.pageSize,
      this.sort.active, this.sort.direction);
  }

  onRowClicked(row) {
    // this.selection.toggle(row);
    console.log(row.position);
  }

}
