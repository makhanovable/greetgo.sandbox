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
    sql.append("select c.id, c.name, c.surname, c.patronymic, ch.name as charm, date_part('year', age(c.birthDate)) as age,\n");
    sql.append("coalesce(max(ca.money), 0) AS maximumBalance, coalesce(min(ca.money),0) AS minimumBalance, coalesce(sum(ca.money),0) AS totalAccountBalance\n");
  }

  @Override
  void from() {
    sql.append("from Client c\n");
  }

  @Override
  void join() {
    sql.append("left join ClientAccount ca on ca.client=c.id\n");
    sql.append("left join Charm ch on c.charm=ch.id\n");

  }

  @Override
  void groupBy() {
    sql.append("group by c.id, ch.name\n");
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
