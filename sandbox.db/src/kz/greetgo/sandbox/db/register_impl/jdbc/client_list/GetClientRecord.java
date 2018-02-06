package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetClientRecord extends GetClientList implements ConnectionCallback<List<ClientRecord>> {

  private long clientId;

  public GetClientRecord(long clientId) {
    super(null);

    this.clientId = clientId;
  }

  @Override
  protected void prepareSql() {
    select();
    sqlQuery.append("FROM client AS cl ");
    from();

    sqlQuery.append("WHERE cl.id = ? ");
    sqlParamList.add(clientId);

    sqlQuery.append("GROUP BY cl.id, ch.name");
  }

  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();

    try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
      int index = 1;
      for (Object sqlParam : sqlParamList)
        ps.setObject(index++, sqlParam);

      List<ClientRecord> ret = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) throw new RuntimeException("No rows of result set");
        ret.add(rsToRecord(rs));

        return ret;
      }
    }
  }
}
