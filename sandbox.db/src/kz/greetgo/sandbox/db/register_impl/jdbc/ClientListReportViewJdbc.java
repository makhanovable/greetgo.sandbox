package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientListReportViewJdbc extends AbstractClientListCommonJdbc implements ConnectionCallback<Void> {

  private final ClientReportView view;

  public ClientListReportViewJdbc(String filter, String orderBy, int order, ClientReportView view) {
    super(filter, orderBy, order);
    this.view = view;
  }

  @Override
  void limit() {
    //no limit
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
          ClientRecord cr = rsToClientRecord(rs);
          view.appendRow(cr);
        }

      }
    }
    return null;
  }
}