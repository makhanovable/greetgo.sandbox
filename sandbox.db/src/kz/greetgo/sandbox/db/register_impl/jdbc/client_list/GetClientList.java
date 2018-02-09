package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetClientList extends GetClientCommon implements ConnectionCallback<List<ClientRecord>> {

  public GetClientList(ClientRecordRequest request) {
    super(request);
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
      int index = 1;

      for (Object sqlParam : sqlParamList)
        ps.setObject(index++, sqlParam);

      try (ResultSet rs = ps.executeQuery()) {
        List<ClientRecord> ret = new ArrayList<>();
        while (rs.next()) {
          ret.add(rsToRecord(rs));
        }
        return ret;
      }
    }
  }

  @Override
  protected void select() {
    sqlQuery.append("SELECT " +
      "cl.id, " +
      "cl.surname, " +
      "cl.name, " +
      "cl.patronymic, " +
      "EXTRACT(YEAR FROM age(cl.birth_date)) AS age, " +
      "ch.name AS charmName, " +
      "SUM(COALESCE(cl_ac.money, 0)) AS totalAccountBalance, " +
      "MAX(COALESCE(cl_ac.money, 0)) AS maxAccountBalance, " +
      "MIN(COALESCE(cl_ac.money, 0)) AS minAccountBalance ");
  }

  @Override
  protected void from() {
    sqlQuery.append("LEFT JOIN charm AS ch ON cl.charm=ch.id " +
      "LEFT JOIN client_account AS cl_ac ON cl.id=cl_ac.client ");
  }

  @Override
  protected void group() {
    sqlQuery.append("GROUP BY cl.id, ch.name ");
  }

  @Override
  protected void sort() {
    sqlQuery.append("ORDER BY ");

    switch (request.columnSortType) {
      case AGE:
        sqlQuery.append("age ");
        break;
      case TOTALACCOUNTBALANCE:
        sqlQuery.append("totalAccountBalance ");
        break;
      case MAXACCOUNTBALANCE:
        sqlQuery.append("maxAccountBalance ");
        break;
      case MINACCOUNTBALANCE:
        sqlQuery.append("minAccountBalance ");
        break;
    }

    if (request.columnSortType != ColumnSortType.NONE) {
      if (request.sortAscend)
        sqlQuery.append("ASC ");
      else
        sqlQuery.append("DESC ");
    } else
      sqlQuery.append("cl.id ");
  }

  @Override
  protected void limit() {
    sqlQuery.append("LIMIT ? ");
    sqlParamList.add(request.clientRecordCount);
    sqlQuery.append("OFFSET ? ");
    sqlParamList.add(request.clientRecordCountToSkip);
  }

  protected ClientRecord rsToRecord(ResultSet rs) throws Exception {
    ClientRecord ret = new ClientRecord();

    ret.id = rs.getLong("id");
    ret.fullName = Util.getFullname(rs.getString("surname"), rs.getString("name"), rs.getString("patronymic"));
    ret.charmName = rs.getString("charmName");
    ret.age = rs.getInt("age");
    ret.totalAccountBalance = Util.floatToString(rs.getFloat("totalAccountBalance"));
    ret.maxAccountBalance = Util.floatToString(rs.getFloat("maxAccountBalance"));
    ret.minAccountBalance = Util.floatToString(rs.getFloat("minAccountBalance"));

    return ret;
  }
}
