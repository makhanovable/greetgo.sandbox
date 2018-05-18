import {Component, ElementRef, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {AccountInfo} from "../../../../model/AccountInfo";
import {HttpService} from "../../../HttpService";
import {AccountInfoDataSource} from "./AccountInfoDataSource";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/operators";
import {fromEvent} from "rxjs/observable/fromEvent";


@Component({
  selector: 'table-basic-example',
  styles: [require('./account-table.css')],
  template: require('./account-table.html'),
})
export class AccountTableComponent {

  dataSource: AccountInfoDataSource;
  displayedColumns = ['select', 'fio', 'charm', 'age', 'total', 'max', 'min'];
  selection = new SelectionModel<AccountInfo>(false);

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('filter') filter: ElementRef;

  constructor(private httpService: HttpService) {
  }

  ngOnInit() {
    this.dataSource = new AccountInfoDataSource(this.httpService);
    this.dataSource.loadAccountInfoList();
  }

  ngAfterViewInit() {

    fromEvent(this.filter.nativeElement, 'keyup')
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        tap(() => {
          this.paginator.pageIndex = 0;
          this.loadAccountInfoPage();
        })
      ).subscribe();

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
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.active,
      this.sort.direction,
      this.filter.nativeElement.value);
  }

  onRowClicked(accountInfo) {
    this.selection.isSelected(accountInfo) ?
      this.selection.deselect(accountInfo) : this.selection.select(accountInfo)
  }

}
