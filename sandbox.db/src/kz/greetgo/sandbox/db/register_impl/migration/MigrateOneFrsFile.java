package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneFrsFile {
  public File inputFile;
  public ErrorFile outputErrorFile;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientAccountTableName;
  String tmpClientAccountTransactionTableName;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    processValidationErrors();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientAccountTableName = "tmp_migration_client_account_" + additionalId;
    tmpClientAccountTransactionTableName = "tmp_migration_client_account_transaction_" + additionalId;

    exec("CREATE TABLE client_account_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  client_id varchar(64), " +
      "  account_number varchar(64), " +
      "  registered_at timestamptz, " +
      "  status int, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_account_transaction_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  money numeric(19, 2), " +
      "  finished_at timestamptz, " +
      "  transaction_type varchar(256), " +
      "  account_number varchar(64), " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");
  }


  void uploadData() throws Exception {
    connection.setAutoCommit(false);

    FrsUploader frsUploader = new FrsUploader();
    frsUploader.connection = connection;
    frsUploader.maxBatchSize = maxBatchSize;
    frsUploader.inputFileName = inputFile.getName();
    frsUploader.errorFileWriter = outputErrorFile;
    frsUploader.clientAccountTable = tmpClientAccountTableName;
    frsUploader.clientAccountTransactionTable = tmpClientAccountTransactionTableName;

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      frsUploader.parse(fileInputStream);
    }

    connection.setAutoCommit(true);
  }

  void processValidationErrors() throws SQLException {

  }

  void migrateData() throws SQLException {
  }

  private void exec(String sql) throws SQLException {
    sql = sql.replaceAll("client_account_to_replace", tmpClientAccountTableName);
    sql = sql.replaceAll("client_account_transaction_to_replace", tmpClientAccountTransactionTableName);

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  void downloadErrors() {

  }
}
