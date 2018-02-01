package kz.greetgo.sandbox.controller.model;

import java.io.Serializable;

public class ClientRecordRequest implements Serializable {
  public long clientRecordCountToSkip;
  public long clientRecordCount;
  public ColumnSortType columnSortType;
  public boolean sortAscend;
  public String nameFilter;

  @Override
  public String toString() {
    return "ClientRecordRequest{" +
      "clientRecordCountToSkip=" + clientRecordCountToSkip +
      ", clientRecordCount=" + clientRecordCount +
      ", columnSortType=" + columnSortType +
      ", sortAscend=" + sortAscend +
      ", nameFilter='" + nameFilter + '\'' +
      '}';
  }
}
