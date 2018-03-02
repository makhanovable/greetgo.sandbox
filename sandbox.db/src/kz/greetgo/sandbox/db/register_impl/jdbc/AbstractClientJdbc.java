package kz.greetgo.sandbox.db.register_impl.jdbc;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClientJdbc {

  protected StringBuilder sql = new StringBuilder();
  List<Object> params = new ArrayList<>();

  private String filter;
  String orderBy;
  int desc;
  int limit;
  int offset;


  public AbstractClientJdbc(String filter) {
    this.filter = getFormattedFilter(filter);
  }

  public AbstractClientJdbc(String filter, String orderBy, int desc) {
    this(filter);
    this.orderBy = orderBy;
    this.desc = desc;
  }

  public AbstractClientJdbc(String filter, String orderBy, int order, int limit, int offset) {
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

  private void where() {
    sql.append("where c.actual=true\n");
    if (filter != null) {
      sql.append("and lower(concat(c.surname, c.name, c.patronymic)) SIMILAR TO ?\n");
      params.add(filter);
    }
  }

  abstract void groupBy();

  abstract void orderBy();

  abstract void limit();

  void generateSql() {
    select();
    from();
    join();
    where();
    groupBy();
    orderBy();
    limit();
  }

  protected String getFormattedFilter(String filter) {
    if (filter == null || filter.isEmpty())
      return null;
    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%(" + filter.toLowerCase() + ")%";
    return filter;
  }


}


