package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.ConnectionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientNumberQuery extends AbstractClientQuery implements ConnectionCallback<Integer> {

  public ClientNumberQuery(String filter) {
    super(filter);
  }

  @Override
  protected void select() {
    sql.append("select count(1)\n");
  }

  @Override
  public Integer doInConnection(Connection connection) throws Exception {
    generateSql();
    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

      int argIndex = 1;
      for (Object arg : params)
        ps.setObject(argIndex++, arg);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
        return null;
      }
    }

  }

  @Override
  void join() {
  }

  @Override
  void groupBy() {

  }

  @Override
  void orderBy() {
  }

  @Override
  void limit() {
  }
}
