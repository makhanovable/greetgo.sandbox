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
    this.updatePageNumeration();

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

        this.curPageNum = this.boundNum(this.curPageNum, 0, this.pageCount - 1);
        this.boundPageNumArray();
      } else
        this.filterSuccessState = false;

      this.getClientRecordList();
    }, error => {
      console.log(error);
    });
  }

  private boundPageNumArray() {
    let pageRange = 2;
    let startPageNum, lastPageNum, remainingPageNum;

    if (this.curPageNum - pageRange < 0) {
      startPageNum = this.boundNum(this.curPageNum - pageRange, 0, this.pageCount - 1);
      remainingPageNum = this.curPageNum - startPageNum;

      lastPageNum = this.curPageNum + pageRange + (pageRange - remainingPageNum);
      lastPageNum = this.boundNum(lastPageNum, 0, this.pageCount - 1);
    }
    else if (this.curPageNum + pageRange > this.pageCount - 1) {
      lastPageNum = this.boundNum(this.curPageNum + pageRange, 0, this.pageCount - 1);
      remainingPageNum = lastPageNum - this.curPageNum;

      startPageNum = this.curPageNum - pageRange - (pageRange - remainingPageNum);
      startPageNum = this.boundNum(startPageNum, 0, this.pageCount - 1);
    } else {
      startPageNum = this.curPageNum - pageRange;
      lastPageNum = this.curPageNum + pageRange;
    }

    this.pageNums = [];
    for (let i = startPageNum, j = 0; i <= lastPageNum; i++, j++)
      this.pageNums[j] = i + 1;
  }

  private boundNum(curNum: number, minNum: number, maxNum: number): number {
    let finalNum = curNum;

    if (curNum < minNum)
      finalNum = minNum;
    else if (curNum > maxNum)
      finalNum = maxNum;

    return finalNum;
  }

  private getClientRecordList() {
    this.request.clientRecordCountToSkip = this.curPageNum * this.httpService.pageSize;
    this.request.clientRecordCount = this.httpService.pageSize;

    this.httpService.get("/client/list", {
      'clientRecordRequest': JSON.stringify(this.request)
    }).toPromise().then(result => {
      this.records = (result.json() as ClientRecord[]).map(ClientRecord.copy);
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
    this.httpService.get("/client/list/report_instance_id", {
      'clientRecordRequest': JSON.stringify(this.request),
      'fileContentType': JSON.stringify(this.downloadContentType)
    }).toPromise().then(result => {
      window.open(this.httpService.url("/client/list/report" + "?report_instance_id=" + JSON.stringify(result.json())
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

  static PAGE_NAVIGATION_FIRST: number = 0;
  static PAGE_NAVIGATION_PREVIOUS: number = 1;
  static PAGE_NAVIGATION_NEXT: number = 2;
  static PAGE_NAVIGATION_LAST: number = 3;

  onPageNavigationButtonClick(pageNavigation: number) {
    switch (pageNavigation) {
      case ClientListComponent.PAGE_NAVIGATION_FIRST: {
        this.curPageNum = 0;
        break;
      }
      case ClientListComponent.PAGE_NAVIGATION_PREVIOUS: {
        this.curPageNum--;
        this.curPageNum = this.boundNum(this.curPageNum, 0, this.pageCount - 1);
        break;
      }
      case ClientListComponent.PAGE_NAVIGATION_NEXT: {
        this.curPageNum++;
        this.curPageNum = this.boundNum(this.curPageNum, 0, this.pageCount - 1);
        break;
      }
      case ClientListComponent.PAGE_NAVIGATION_LAST: {
        this.curPageNum = this.pageCount - 1;
        break;
      }
    }

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
    let clientRecord: ClientRecord = event.clientRecord;
    let isAddOperation: boolean = event.isAddOperation;

    if (clientRecord == null)
      return;

    if (isAddOperation) {
      this.records.unshift(new ClientRecord().assign(clientRecord));
      this.selectedRecordId = clientRecord.id;
    } else {
      for (let record of  this.records)
        if (record.id == clientRecord.id) {
          record.assign(clientRecord);
          break;
        }
    }
  }
}
