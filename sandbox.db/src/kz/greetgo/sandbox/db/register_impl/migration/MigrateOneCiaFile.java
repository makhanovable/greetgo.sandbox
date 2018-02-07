package kz.greetgo.sandbox.db.register_impl.migration;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneCiaFile {
  public File inputFile;
  public File outputErrorFile;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTable;
  String tmpAddressTable;

  private void exec(String sql) throws SQLException {
    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_ADDRESS", tmpAddressTable);

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    tmpClientTable = "tmp_migration_client_" + sdf.format(now);
    tmpAddressTable = "tmp_migration_address_" + sdf.format(now);

    exec("create table TMP_CLIENT (" +
      "  no int not null primary key," +
      "  cia_id varchar(50)," +
      "  surname varchar(300)," +
      "  status int not null default 0," +
      "  error varchar(500)" +
      ")");

    exec("create table TMP_ADDRESS (" +
      "  no int not null," +
      "  type varchar(30) not null," +
      "  street varchar(300)," +
      "  primary key(no, type)" +
      ")");
  }

  void uploadData() throws Exception {

    connection.setAutoCommit(false);

    XMLReader reader = XMLReaderFactory.createXMLReader();

    CiaUploader ciaUploader = new CiaUploader();
    ciaUploader.connection = connection;
    ciaUploader.maxBatchSize = maxBatchSize;
    ciaUploader.clientTable = tmpClientTable;
    reader.setContentHandler(ciaUploader);

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      reader.parse(new InputSource(fileInputStream));
    }

    connection.setAutoCommit(true);
  }

  void migrateData() throws SQLException {
    exec("insert into client select * from TMP_CLIENT, TMP_ADDRESS");
  }

  void downloadErrors() {

  }

}
