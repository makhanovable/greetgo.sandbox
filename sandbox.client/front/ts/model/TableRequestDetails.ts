import {SortDirection} from "./SortDirection";

export class TableRequestDetails {
  public pageIndex: number = 1;
  public pageSize: number = 3;
  public sortBy: string = '';
  public sortDirection: SortDirection = SortDirection.ASC;
  public filter: string = '';


  constructor(pageIndex: number, pageSize: number, sortBy: string, sortDirection: SortDirection, filter: string) {
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.filter = filter;
  }

  toString() {
    return "PageIndex:" + this.pageIndex + ", PageSize:" + this.pageSize
      + ", SortBy:" + this.sortBy + ", Sort:" + this.sortDirection + ", Filter:" + this.filter;
  }
}