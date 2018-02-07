package kz.greetgo.sandbox.db.register_impl.migration;

import org.xml.sax.Attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CiaUploader extends CommonSaxHandler {
  public Connection connection;
  public int maxBatchSize;
  public String clientTable;

  PreparedStatement clientPS;

  private void prepare() throws SQLException {
    clientPS = connection.prepareStatement("insert into " + clientTable + " (no, cia_id, surname) values (?, ?, ?)");
  }

  ClientData client;

  private void setPS() throws SQLException {
    clientPS.setInt(1, no);
    clientPS.setString(2, client.id);
    clientPS.setString(3, client.surname);
  }

  int no = 0;

  @Override
  protected void startTag(Attributes attributes) throws Exception {
    String path = path();
    if ("/cia".equals(path)) {
      prepare();
      return;
    }
    if ("/cia/client".equals(path)) {
      client = new ClientData();
      client.id = attributes.getValue("id");
      no++;
      return;
    }
    if ("/cia/client/surname".equals(path)) {
      client.surname = attributes.getValue("value");
      return;
    }
  }

  int totalCount = 0;
  int batchCount = 0;

  @Override
  protected void endTag() throws Exception {
    String path = path();
    if ("/cia/client".equals(path)) {

      setPS();
      clientPS.addBatch();
      batchCount++;
      totalCount++;

      if (batchCount > maxBatchSize) {
        clientPS.executeBatch();
        connection.commit();
        batchCount = 0;
      }

      return;
    }

    if ("/cia".equals(path)) {

      if (batchCount > 0) {
        clientPS.executeBatch();
        connection.commit();
        batchCount = 0;
      }

      return;
    }
  }
}
