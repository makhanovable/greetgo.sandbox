package kz.greetgo.sandbox.db.register_impl.migration.report;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class MigrateOneFileCommon {

  public InputStream inputStream;
  public ErrorFile outputErrorFile;
  public MigrationSimpleReport migrationSimpleReport;
  public int maxBatchSize = 100;
  public Connection connection;

  protected abstract void prepareTmpTables() throws Exception;

  protected abstract void uploadData() throws Exception;

  protected abstract void prepareIndexes() throws Exception;

  protected abstract void migrateData() throws Exception;

  protected abstract void downloadErrors() throws Exception;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    prepareIndexes();
    migrateData();
    downloadErrors();
  }

  protected void exec(String sqlQuery) throws SQLException {
    long init = System.currentTimeMillis();
    try (Statement statement = connection.createStatement()) {
      statement.execute(sqlQuery);
    }
    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.addAction(Util.getSecondsFromMilliseconds(init, post), -1, sqlQuery);
  }

  protected void execUpdate(String sqlQuery) throws SQLException {
    int totalUpdatedRecordsCount;
    long init = System.currentTimeMillis();
    try (Statement statement = connection.createStatement()) {
      totalUpdatedRecordsCount = statement.executeUpdate(sqlQuery);
    }
    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.addAction(Util.getSecondsFromMilliseconds(init, post), totalUpdatedRecordsCount, sqlQuery);
  }

  /*
  private void execUpdateByParts(String sqlQuery, long limitCount) throws Exception {
    sqlQuery = replaceTableNames(sqlQuery);
    sqlQuery = sqlQuery.replaceAll("limit_to_replace", "LIMIT " + limitCount);

    int totalUpdatedRecordsCount = 0;
    int updatedRecordsCount;
    long init = System.currentTimeMillis();
    do {
      try (Statement statement = connection.createStatement()) {
        updatedRecordsCount = statement.executeUpdate(sqlQuery);
        totalUpdatedRecordsCount += updatedRecordsCount;
        System.out.println(updatedRecordsCount + " " + totalUpdatedRecordsCount);
      }
    } while (updatedRecordsCount > 0);
    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.addAction(Util.getSecondsFromMilliseconds(init, post), totalUpdatedRecordsCount, sqlQuery);
  }*/
}
