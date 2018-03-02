package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.util.ClientUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class AbstractClientListCommonQuery extends AbstractClientQuery {

  AbstractClientListCommonQuery(String filter, String orderBy, int order) {
    super(filter, orderBy, order);
  }

  AbstractClientListCommonQuery(String filter, String orderBy, int desc, int limit, int offset) {
    super(filter, orderBy, desc, limit, offset);
  }

  @Override
  protected void select() {
    sql.append("select c.id, c.name, c.surname, c.patronymic, ch.name as charm, date_part('year', age(c.birthDate)) as age,\n");
    sql.append("coalesce(max(ca.money), 0) AS maximumBalance, coalesce(min(ca.money),0) AS minimumBalance, coalesce(sum(ca.money),0) AS totalAccountBalance\n");
  }

  protected ClientRecord rsToClientRecord(ResultSet rs) throws SQLException {
    ClientRecord record = new ClientRecord();
    record.id = rs.getString("id");
    record.name = rs.getString("name");
    record.surname = rs.getString("surname");
    record.patronymic = rs.getString("patronymic");
    record.age = rs.getInt("age");
    record.charm = rs.getString("charm");
    record.totalAccountBalance = rs.getFloat("totalAccountBalance");
    record.maximumBalance = rs.getFloat("maximumBalance");
    record.minimumBalance = rs.getFloat("minimumBalance");
    return record;
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

    if (orderByIsMatch) {
      sql.append("order by ");

      switch (orderBy) {
        case "fio":
          sql.append("concat(c.surname, c.name, c.patronymic)");
          break;
        case "age":
          sql.append("age");
          break;
        case "total":
          sql.append("totalAccountBalance");
          break;
        case "max":
          sql.append("maximumBalance");
          break;
        case "min":
          sql.append("minimumBalance");
          break;
      }

      if (desc == 1)
        sql.append(" desc");
      sql.append("\n");
    }
  }
}
