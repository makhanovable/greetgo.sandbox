package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.util.ClientUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AbstractClientRecordWebViewQuery extends AbstractClientRecordCommonViewQuery implements ConnectionCallback<List<ClientRecord>> {

  public AbstractClientRecordWebViewQuery(String filter, String orderBy, int order, int limit, int offset) {
    super(filter, orderBy, order, limit, offset);
  }

  @Override
  protected void select() {
    super.select();
    sql.append(", c.charm\n");
  }

  @Override
  void limit() {
    sql.append("limit ? offset ?\n");
    params.add(limit);
    params.add(offset);
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    List<ClientRecord> result = new ArrayList<>();
    generateSql();
    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
      int argIndex = 1;
      for (Object arg : params)
        ps.setObject(argIndex++, arg);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ClientRecord cr = ClientUtils.rsToClientRecord(rs);
          result.add(cr);
        }

      }
    }
    return result;
  }
}
