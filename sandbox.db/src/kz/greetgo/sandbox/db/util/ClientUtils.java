package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientUtils {
  public static String[] sortableColumns = {"age", "totalAccountBalance", "maximumBalance", "minimumBalance"};
  public static String[] reportHeaders = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};

  public static ClientRecord rsToClientRecord(ResultSet rs) throws SQLException {
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

  public static String getFormattedFilter(String filter) {
    if (filter == null || filter.isEmpty())
      return null;
    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%(" + filter.toLowerCase() + ")%";
    return filter;
  }
}
