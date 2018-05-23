import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {AccountInfo} from "../../../../model/AccountInfo";
import {HttpService} from "../../../HttpService";
import {GenericDataSource} from "./GenericDataSource";
import {debounceTime, distinctUntilChanged, tap} from "rxjs/operators";
import {fromEvent} from "rxjs/observable/fromEvent";
import {AccountService} from "../../../services/AccountService";
import {SortDirection} from "../../../../model/SortDirection";
import {TableRequestDetails} from "../../../../model/TableRequestDetails";
import {SortColumn} from "../../../../model/SortColumn";

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
    this.paginator.pageSize = 3;
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
    let sortDirection = SortDirection.ASC;
    let sortColumn = SortColumn.NONE;

    if (this.sort.direction == "desc") sortDirection = SortDirection.DESC;
    if (this.sort.active == "fio") sortColumn = SortColumn.FIO;
    else if (this.sort.active == "age") sortColumn = SortColumn.AGE;
    else if (this.sort.active == "total") sortColumn = SortColumn.TOTAL;
    else if (this.sort.active == "max") sortColumn = SortColumn.MAX;
    else if (this.sort.active == "min") sortColumn = SortColumn.MIN;


    const requestDetails = new TableRequestDetails(this.paginator.pageIndex,
      this.paginator.pageSize,
      sortColumn,
      sortDirection,
      this.filter.nativeElement.value);


    console.log(requestDetails.toString());
    this.requestAccountInfoList(requestDetails);
  }

  requestAccountInfoList(requestDetails: TableRequestDetails) {
    this.dataSource.startLoading();

    this.httpService.get("/accounts/", {requestDetails: JSON.stringify(requestDetails)}).toPromise().then(response => {
      this.onAccountInfoListRequestSuccess(response);
    }, error => {
      console.log(error);
      this.dataSource.stopLoading()
    });
  }

  private onAccountInfoListRequestSuccess(response) {
    const result = response.json();
    this.responseLength = result.totalAccountInfo;

    this.dataSource.updateDateSource(result.accountInfoList);
    this.dataSource.stopLoading();
  }

  onAddClicked() {
    this.onAddAccount.emit();
  }

  onEditClicked() {
    this.onEditAccount.emit(this.selection.selected[0]);
  }

  onDeleteClicked() {
    const clientId = this.selection.selected[0].id;

    this.requestClientDelete(clientId);
  }

  private requestClientDelete(clientId: number) {
    this.httpService.post("/client/delete", {clientId: clientId}).toPromise().then(response => {
      this.onClientDeleteSuccess(response);
    }, error => {
      console.log(error)
    });
  }

  private onClientDeleteSuccess(response) {
    this.accountService.deleteAccount(response.json());
    this.selection.deselect(this.selection.selected[0]);
  }
}
