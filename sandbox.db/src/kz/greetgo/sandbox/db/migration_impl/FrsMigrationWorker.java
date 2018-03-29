package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.util.RND;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public class FrsMigrationWorker extends AbstractMigrationWorker {

  String tmpAccountTable;
  String tmpTransactionTable;

  public FrsMigrationWorker(Connection connection) {
    super(connection);
  }

//  @Override
//  protected List<String> prepareInFiles() throws IOException, SftpException {
//    List<String> fileDirsToLoad = renameFiles(".json_row.txt.tar.bz2");
//    fileDirsToLoad.sort(String::compareTo);
//    return fileDirsToLoad;
//  }

  @Override
  protected void validateErrors() {

  }

  @Override
  protected void uploadErrors() {

  }

  @Override
  protected String r(String sql) {
    sql = sql.replaceAll("TMP_ACCOUNT", tmpAccountTable);
    sql = sql.replaceAll("TMP_TRANSACTION", tmpTransactionTable);
    return sql;
  }

  @Override
  protected void createTmpTables() throws SQLException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    String processId = "_" + RND.intStr(5);
    tmpAccountTable = "cia_migration_account_" + sdf.format(nowDate) + processId;
    tmpTransactionTable = "cia_migration_transaction_" + sdf.format(nowDate) + processId;

    //language=PostgreSQL
    exec("CREATE TABLE TMP_ACCOUNT (\n" +
      "  type           VARCHAR(32),\n" +
      "  client_id      VARCHAR(32),\n" +
      "  account_number VARCHAR(64),\n" +
      "  registered_at  TIMESTAMP WITH TIME ZONE,\n" +
      "\n" +
      "  status         INT NOT NULL DEFAULT 0,\n" +
      "  error          VARCHAR(255),\n" +
      "  number         BIGSERIAL PRIMARY KEY\n" +
      ")");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_TRANSACTION (\n" +
      "  type             VARCHAR(32),\n" +
      "  money            DECIMAL,\n" +
      "  finished_at      TIMESTAMP WITH TIME ZONE,\n" +
      "  transaction_type VARCHAR(255),\n" +
      "  account_number   VARCHAR(100),\n" +
      "\n" +
      "  status           INT NOT NULL DEFAULT 0,\n" +
      "  error            VARCHAR(255),\n" +
      "  number           BIGSERIAL PRIMARY KEY\n" +
      ")");
  }

  @Override
  protected void migrateFromTmp() throws SQLException {

    createIdleClientsIfNotExist();

    insertClientAccounts();

    insertAccountTransactions();
  }

  void createIdleClientsIfNotExist() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client (id, cia_id)\n" +
      "SELECT nextval('s_client'), client_id\n" +
      "FROM TMP_ACCOUNT ta ON CONFLICT (cia_id) DO NOTHING");
  }

  void insertClientAccounts() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client_account (id, client, money, number, registered_at)\n" +
      "SELECT nextval('s_client'), c.id, tt.money, ta.account_number, ta.registered_at\n" +
      "FROM TMP_ACCOUNT ta LEFT JOIN (SELECT account_number, sum(money) money FROM TMP_TRANSACTION GROUP BY account_number) tt\n" +
      "ON tt.account_number = ta.account_number LEFT JOIN client c ON c.cia_id = ta.client_id");
  }

  void insertAccountTransactions() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client_account_transaction (id, account, money, finished_at, type)\n" +
      "SELECT nextval('s_client'), ca.id, tt.money, tt.finished_at, tt.transaction_type\n" +
      "FROM TMP_TRANSACTION tt LEFT JOIN client_account ca ON tt.account_number = ca.number");
  }

  @Override
  protected int parseDataAndSaveInTmpDb() throws IOException, SQLException, SftpException {

    int recordsCount = 0;

    long startedAt = System.nanoTime();

    connection.setAutoCommit(false);

    try (FrsTableWorker frsTableWorker = new FrsTableWorker(connection, maxBatchSize, tmpAccountTable, tmpTransactionTable)) {
      FrsParser frsParser = new FrsParser(inputStream, frsTableWorker);
      frsParser.outError = outError;
      recordsCount += frsParser.parseAndSave();
    } finally {
      connection.setAutoCommit(true);
    }

    {
      long now = System.nanoTime();
      info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
        + " : " + recordsPerSecond(recordsCount, now - startedAt));
    }

    return recordsCount;
  }
}
