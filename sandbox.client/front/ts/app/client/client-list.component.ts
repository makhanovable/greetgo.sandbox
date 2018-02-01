import {Component} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientRecordRequest} from "../../model/ClientRecordRequest";
import {ColumnSortType} from "../../model/ColumnSortType";
import {ClientDetailsComponent} from "./client-details.component";
import {FileContentType} from "../../model/FileContentType";

@Component({
  selector: 'client-list-component',
  template: require('./client-list-component.html'),
  styles: [require('./client-list-component.css')],
  host: {'(document:click)': 'onClick($event)'},
})

export class ClientListComponent {
  curPageNum: number = 0;
  pageCount: number;
  pageNums: number[];
  records: ClientRecord[] | null = [];
  selectedRecordId: number | null = null;
  request: ClientRecordRequest = new ClientRecordRequest();
  filterSuccessState: boolean | null = null;
  isModalFormActive: boolean = false;
  fileContentTypeEnum = FileContentType;
  downloadContentType: FileContentType;

  constructor(private httpService: HttpService) {
    this.request.columnSortType = ColumnSortType.NONE;
    this.request.sortAscend = false;
    this.request.nameFilter = "";

    this.downloadContentType = FileContentType.PDF;

    this.refreshClientRecordList();
  }

  private refreshClientRecordList() {
    this.selectedRecordId = null;
    this.updatePageNumeration();
    this.getClientRecordList();
  }

  private updatePageNumeration() {
    this.httpService.get("/client/count", {
      'clientRecordRequest': JSON.stringify(this.request)
    }).toPromise().then(result => {
      this.pageCount = Math.floor(result.json() as number / this.httpService.pageSize);
      if (result.json() as number % this.httpService.pageSize > 0)
        this.pageCount++;

      this.pageNums = [];

      if (this.pageCount > 0) {
        this.filterSuccessState = true;

        for (let i = 0; i < this.pageCount; i++)
          this.pageNums[i] = i + 1;
      } else
        this.filterSuccessState = false;
    }, error => {
      console.log(error);
    });
  }

  private getClientRecordList() {
    this.request.clientRecordCountToSkip = this.curPageNum * this.httpService.pageSize;
    this.request.clientRecordCount = this.httpService.pageSize;

    this.httpService.get("/client/list", {
      'clientRecordRequest': JSON.stringify(this.request)
    }).toPromise().then(result => {
      this.records = (result.json() as ClientRecord[]).map(ClientRecord.copy);

      if (this.records.length == 0) {
        this.curPageNum--;
        if (this.curPageNum < 0)
          this.curPageNum = 0;
        else
          this.refreshClientRecordList();
      }
    }, error => {
      console.log(error);
    });
  }

  private onClick(event: any) {
  }

  protected onFilterTextChange(event) {
    if (this.filterSuccessState || this.filterSuccessState == false)
      this.filterSuccessState = null;
  }

  onClientRecordListDownloadButtonClick() {
    this.httpService.get("/client/list/token").toPromise().then(result => {
      window.open(this.httpService.url("/client/list/report" +
        "?clientRecordRequest=" + JSON.stringify(this.request) +
        "&fileContentType=" + JSON.stringify(this.downloadContentType) +
        "&token=" + JSON.stringify(result.json())
      ));
    }, error => {
      console.log(error);
    });
  }

  onFilterButtonClick(filterValue: any) {
    let json = JSON.stringify(filterValue).trim();
    let filter = JSON.parse(json).filter;

    if (filter == null || filter.length == 0)
      filter = "";

    this.request.nameFilter = filter;
    this.refreshClientRecordList();
  }

  onClientRecordClick(recordId: number) {
    this.selectedRecordId = recordId;
  }

  onSortingButtonClick(columnSortTypeName: string, sortAscend: boolean) {
    this.request.columnSortType = columnSortTypeName as ColumnSortType;
    this.request.sortAscend = sortAscend;
    this.curPageNum = 0;

    this.refreshClientRecordList();
  }

  isSortingButtonActive(columnSortTypeName: string, sortAscend: boolean): boolean {
    if (columnSortTypeName == this.request.columnSortType) {
      if (columnSortTypeName == "NONE")
        return true;

      if (sortAscend == this.request.sortAscend)
        return true;
    }

    return false;
  }

  onPageNumberButtonClick(pageNum: number) {
    this.curPageNum = pageNum;
    this.refreshClientRecordList();
  }

  onClientRecordEditButtonClick(clientRecordComponent: ClientDetailsComponent, isEditOperation: boolean) {
    isEditOperation ? clientRecordComponent.show(this.selectedRecordId) : clientRecordComponent.show(null);
  }

  onClientRecordRemoveButtonClick() {
    this.httpService.delete("/client/remove", {
      'clientRecordId': this.selectedRecordId
    }).toPromise().then(result => {
      this.refreshClientRecordList();
    }, error => {
      alert("Операция удаления не удалась");
      console.log(error);
    });
  }

  onModalClose(event: any) {
    this.refreshClientRecordList();
  }
}
