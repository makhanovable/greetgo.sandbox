package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientListWebViewJdbc extends AbstractClientListCommonJdbc implements ConnectionCallback<List<ClientRecord>> {

  public ClientListWebViewJdbc(String filter, String orderBy, int desc, int limit, int offset) {
    super(filter, orderBy, desc, limit, offset);
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
          ClientRecord cr = rsToClientRecord(rs);
          result.add(cr);
        }

      }
    }
    return result;
  }
}
