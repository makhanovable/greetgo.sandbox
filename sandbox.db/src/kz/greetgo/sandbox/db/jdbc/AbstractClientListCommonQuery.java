package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.sandbox.db.util.ClientUtils;

import java.util.Arrays;

public abstract class AbstractClientListCommonQuery extends AbstractClientQuery {

  protected AbstractClientListCommonQuery(String filter, String orderBy, int order) {
    super(filter, orderBy, order);
  }

  protected AbstractClientListCommonQuery(String filter, String orderBy, int order, int limit, int offset) {
    super(filter, orderBy, order, limit, offset);
  }

  @Override
  protected void select() {
    sql.append("select c.id, c.name, c.surname, c.patronymic, date_part('year', age(c.birthDate)) as age, max(ca.money) AS maximumBalance, min(ca.money) AS minimumBalance, sum(ca.money) AS totalAccountBalance\n");
  }

  @Override
  void from() {
    sql.append("from Client c\n");
  }

  @Override
  void join() {
    sql.append("left join ClientAccount ca on ca.client=c.id\n");
  }

  @Override
  void groupBy() {
    sql.append("group by c.id\n");
  }

  @Override
  public void orderBy() {
    boolean orderByIsMatch = orderBy != null && Arrays.stream(ClientUtils.sortableColumns).anyMatch(o -> o.equals(orderBy));

    if (orderByIsMatch)
      sql.append("order by ").append(orderBy);
    else
      sql.append("order by concat(c.name, c.surname, c.patronymic)");
    if (order == 1)
      sql.append(" desc");
    sql.append("\n");
  }


}
