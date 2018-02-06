package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.sandbox.controller.model.ClientRecordRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class GetClientCommon {

  protected final ClientRecordRequest request;

  protected final StringBuilder sqlQuery = new StringBuilder();
  protected final List<Object> sqlParamList = new ArrayList<>();

  protected GetClientCommon(ClientRecordRequest request) {
    this.request = request;
  }

  protected void prepareSql() {
    select();
    sqlQuery.append("FROM client AS cl ");
    from();
    sqlQuery.append("WHERE cl.actual=1 ");

    if (!request.nameFilter.isEmpty()) {
      sqlQuery.append("AND (LOWER(cl.surname) LIKE ? OR " +
        "LOWER(cl.name) LIKE ? OR " +
        "LOWER(cl.patronymic) LIKE ?) ");
      sqlParamList.add("%" + request.nameFilter.toLowerCase() + "%");
      sqlParamList.add("%" + request.nameFilter.toLowerCase() + "%");
      sqlParamList.add("%" + request.nameFilter.toLowerCase() + "%");
    }

    group();
    sort();
    limit();
  }

  protected abstract void select();

  protected abstract void from();

  protected abstract void group();

  protected abstract void sort();

  protected abstract void limit();
}
