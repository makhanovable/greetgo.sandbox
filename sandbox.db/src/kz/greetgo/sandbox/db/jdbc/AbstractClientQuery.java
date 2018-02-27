package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.sandbox.db.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractClientQuery {

  protected StringBuilder sql = new StringBuilder();
  protected List<Object> params = new ArrayList<>();

  protected String filter;
  protected String orderBy;
  protected int order;
  protected int limit;
  protected int offset;


  public AbstractClientQuery(String filter) {
    this.filter = ClientUtils.getFormattedFilter(filter);
  }

  public AbstractClientQuery(String filter, String orderBy, int order) {
    this(filter);
    this.orderBy = orderBy;
    this.order = order;
  }

  public AbstractClientQuery(String filter, String orderBy, int order, int limit, int offset) {
    this(filter, orderBy, order);
    this.limit = limit;
    this.offset = offset;
  }

  protected void select() {
    sql.append("select *\n");
  }

  void from() {
    sql.append("from Client c\n");
  }

  abstract void join();

  void where() {
    sql.append("where c.actual=true\n");
    if (filter != null) {
      sql.append("and lower(concat(c.name, c.surname, c.patronymic)) SIMILAR TO ?\n");
      params.add(filter);
    }
  }

  abstract void groupBy();

  abstract void orderBy();

  abstract void limit();

  public void generateSql() {
    select();
    from();
    join();
    where();
    groupBy();
    orderBy();
    limit();
  }
}


