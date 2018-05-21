import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {AccountInfo} from "../../../../model/AccountInfo";
import {HttpService} from "../../../HttpService";
import {GenericDataSource} from "./GenericDataSource";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/operators";
import {fromEvent} from "rxjs/observable/fromEvent";
import {AccountService} from "../../../services/AccountService";

@Component({
  selector: 'table-basic-example',
  styles: [require('./account-table.css')],
  template: require('./account-table.html'),
})
export class AccountTableComponent {

  dataSource: GenericDataSource;
  displayedColumns = ['select', 'fio', 'charm', 'age', 'total', 'max', 'min'];
  selection = new SelectionModel<AccountInfo>(false);

  responseLength = 0;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('filter') filter: ElementRef;

  @Output() onAddAccount: EventEmitter<null> = new EventEmitter();
  @Output() onEditAccount: EventEmitter<AccountInfo> = new EventEmitter();

  constructor(private httpService: HttpService, private accountService: AccountService) {
    this.accountService.accountAdded.subscribe(accountInfo => {
      this.dataSource.addNewItem(accountInfo);
    });

    this.accountService.accountDeleted.subscribe(accountInfo => {
      this.dataSource.removeItem(accountInfo);
    });

    this.accountService.accountUpdated.subscribe(accountInfo => {
      this.dataSource.updateItem(accountInfo);
    });
  }

  ngOnInit() {
    this.dataSource = new GenericDataSource();
    this.loadAccountPage();
  }

  ngAfterViewInit() {
    fromEvent(this.filter.nativeElement, 'keyup')
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        tap(() => {
          this.paginator.pageIndex = 0;
          this.loadAccountPage();
        })
      ).subscribe();

    this.sort.sortChange.subscribe(() => {
      this.paginator.pageIndex = 0;
      this.loadAccountPage();
    });

    this.paginator.page
      .pipe(
        tap(() => this.loadAccountPage()
        )
      ).subscribe();
  }

  onRowClicked(accountInfo) {
    this.selection.isSelected(accountInfo) ?
      this.selection.deselect(accountInfo) : this.selection.select(accountInfo);
  }

  loadAccountPage() {
    this.loadAccountInfoList(
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.active,
      this.sort.direction,
      this.filter.nativeElement.value)
  }

  loadAccountInfoList(pageIndex = 0, pageSize = 3, sortBy = '', sortDirection = 'asc', filter = '') {

    console.log("PageIndex:" + pageIndex + ", PageSize:" + pageSize + ", SortBy:" + sortBy + ", Sort:" + sortDirection + ", Filter:" + filter);

    this.dataSource.startLoading();
    this.httpService.get("/accounts/").toPromise().then(response => {
      const result = response.json();
      this.responseLength = Object.keys(result).length;

      this.dataSource.updateDateSource(result);
      this.dataSource.stopLoading();
    }, error => {
      console.log(error);
      this.dataSource.stopLoading()
    });
  }

  onAddClicked() {
    this.onAddAccount.emit();
  }

  onEditClicked() {
    this.onEditAccount.emit(this.selection.selected[0]);
  }

  onDeleteClicked() {
    const clientId = this.selection.selected[0].id;
    this.httpService.post("/client/delete", {clientId: clientId}).toPromise().then(response => {
      this.accountService.deleteAccount(response.json());
      this.selection.deselect(this.selection.selected[0]);
    }, error => {
      console.log(error)
    });
  }

}
