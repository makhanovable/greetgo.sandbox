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
  String tmpTransactionTypeTableName;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    //processValidationErrors();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientAccountTableName = "tmp_migration_client_account_" + additionalId;
    tmpClientAccountTransactionTableName = "tmp_migration_client_account_transaction_" + additionalId;
    tmpTransactionTypeTableName = "tmp_migration_transaction_type_" + additionalId;

    exec("CREATE TABLE client_account_to_replace (" +
      "  record_no bigint, " +
      "  cia_id varchar(64), " +
      "  id bigint, " +
      "  money numeric(19, 2) NOT NULL DEFAULT 0, " +
      "  account_number varchar(64), " +
      "  registered_at timestamptz, " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_account_transaction_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  id bigint, " +
      "  money numeric(19, 2) DEFAULT 0, " +
      "  finished_at timestamptz, " +
      "  transaction_type varchar(256), " +
      "  type bigint, " +
      "  account_number varchar(64), " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE transaction_type_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  id bigint, " +
      "  code varchar(128), " +
      "  name varchar(256), " +
      "  status int DEFAULT 0, " +
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

  void migrateData() throws SQLException {
    //migrateData_checkForDuplicatesAndFillIdOfTmpTransactionType();
    migrateData_checkForDuplicatesAndOfTmpClientAccountTransaction();
    migrateData_checkForDuplicatesOfTmpClientAccount();

    migrateData_finalOfTransactionTypeTable();
    migrateData_finalOfClientAccountTransaction();
  }

  /**
   * Статус = 1, для отсеивания дубликатов клиентских аккаунтов с приоритетом на последнюю запись
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClientAccount() throws SQLException {
    exec("UPDATE client_account_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY account_number ORDER BY record_no DESC ) AS rnum " +
      "  FROM client_account_to_replace " +
      "  WHERE status = 0 AND error IS NULL " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }

  /**
   * Статус = 1, для отсеивания дубликатов клиентских транзакций с приоритетом на последнюю запись
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesAndOfTmpClientAccountTransaction() throws SQLException {
    exec("UPDATE client_account_transaction_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY money, finished_at, account_number " +
      "    ORDER BY record_no DESC ) AS rnum " +
      "  FROM client_account_transaction_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }

  /**
   * Статус = 1, для отсеивания дубликатов типов транзакций с приоритетом на последнюю запись
   *
   * @throws SQLException проброс для удобства
   */
  /*void migrateData_checkForDuplicatesAndFillIdOfTmpTransactionType() throws SQLException {
    exec("UPDATE transaction_type_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY name ORDER BY record_no DESC ) AS rnum " +
      "  FROM transaction_type_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }*/

  void migrateData_finalOfTransactionTypeTable() throws SQLException {
    exec("INSERT INTO transaction_type(id, name) " +
      "SELECT nextval('transaction_type_id_seq'), trans_dictionary.type " +
      "FROM ( " +
      "  SELECT DISTINCT transaction_type AS type " +
      "  FROM client_account_transaction_to_replace " +
      "  WHERE status = 1 " +
      "  EXCEPT SELECT name FROM transaction_type " +
      ") AS trans_dictionary"
    );
  }

  void migrateData_finalOfClientAccountTransaction() throws SQLException {
    exec("INSERT INTO client_account_transaction(id, account, finished_at, type) " +
      "SELECT nextval('client_account_transaction'), ca.id, cat_r.finished_at, tt.id " +
      "FROM client_account_transaction_to_replace AS cat_r " +
      "JOIN transaction_type AS tt ON cat_r.transaction_type = tt.name " +
      "JOIN client_account AS ca ON cat_r.account_number = ca.number " +
      "WHERE cat_r.status = 1 " +
      "ON CONFLICT(account_number, money, finished_at) DO UPDATE " +
      "SET type = excluded.type"
    );
  }

  /**
   * Статус = 2, присвоение идентификатора типа транзакции временной таблице client_account_transaction
   *
   * @throws SQLException проброс для удобства
   */
  /*void migrateData_fillTypeofClientAccountTransaction() throws SQLException {
    exec("UPDATE client_account_transaction_to_replace AS ca " +
      "SET type = tt.id, status = 2 " +
      "FROM transaction_type_to_replace AS tt " +
      "WHERE ca.status = 1 AND tt.status = 1 AND ca.transaction_type = tt.name"
    );
  }
*/
  private void exec(String sql) throws SQLException {
    sql = sql.replaceAll("client_account_to_replace", tmpClientAccountTableName);
    sql = sql.replaceAll("client_account_transaction_to_replace", tmpClientAccountTransactionTableName);
    sql = sql.replaceAll("transaction_type_to_replace", tmpTransactionTypeTableName);

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  void downloadErrors() {

  }
}
