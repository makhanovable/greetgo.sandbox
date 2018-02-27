package kz.greetgo.sandbox.db.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.db.util.ClientUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientListReportViewQuery extends AbstractClientListCommonQuery implements ConnectionCallback<Void> {

  private final ClientReportView view;

  public ClientListReportViewQuery(String filter, String orderBy, int order, ClientReportView view) {
    super(filter, orderBy, order);
    this.view = view;
  }

  @Override
  void limit() {
    //no limit
  }

  @Override
  protected void select() {
    super.select();
    sql.append(", ch.name as charm\n");
  }

  @Override
  void join() {
    super.join();
    sql.append("left join Charm ch on c.charm=ch.id\n");
  }

  @Override
  protected void groupBy() {
    super.groupBy();
    sql.append(", ch.name\n");
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {

    generateSql();
    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
      int argIndex = 1;
      for (Object arg : params)
        ps.setObject(argIndex++, arg);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ClientRecord cr = ClientUtils.rsToClientRecord(rs);
          view.appendRow(cr);
        }

      }
    }
    return null;
  }
}
