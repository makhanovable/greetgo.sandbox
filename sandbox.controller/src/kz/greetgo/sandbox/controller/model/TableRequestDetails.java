package kz.greetgo.sandbox.controller.model;

public class TableRequestDetails {
  public int pageIndex =  1;
  public int pageSize =  3;
  public SortColumn sortBy =  SortColumn.NONE;
  public SortDirection sortDirection =  SortDirection.ASC;
  public String filter =  "";
}
