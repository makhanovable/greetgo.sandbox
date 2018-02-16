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
      "  client_id varchar(64), " +
      "  money numeric(19, 2) NOT NULL DEFAULT 0, " +
      "  account_number varchar(64), " +
      "  registered_at timestamptz, " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_account_transaction_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  money numeric(19, 2) DEFAULT 0, " +
      "  finished_at timestamptz, " +
      "  transaction_type varchar(256), " +
      "  account_number varchar(64), " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE transaction_type_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  code varchar(128), " +
      "  name varchar(256), " +
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
    frsUploader.transactionTypeTable = tmpTransactionTypeTableName;

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      frsUploader.parse(fileInputStream);
    }

    connection.setAutoCommit(true);
  }

  void processValidationErrors() throws SQLException {

  }


  void migrateData() throws SQLException {
    migrateData_checkForDuplicatesOfClientAccount();
    migrateData_checkForDuplicatesOfClientAccountTransaction();

    migrateData_fillMoneyOfClientAccount();
    migrateData_status2_clientAccountTransaction();
  }

  /**
   * Статус = 1, для отсеивания дубликатов клиентских аккаунтов с приоритетом на последнюю запись
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfClientAccount() throws SQLException {
    exec("UPDATE client_account_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY account_number ORDER BY record_no DESC ) AS rnum " +
      "  FROM client_account_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }

  /**
   * Статус = 1, для отсеивания дубликатов клиентских транзакций с приоритетом на последнюю запись
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfClientAccountTransaction() throws SQLException {
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
/*
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
  }*/

  /**
   * Заполнение суммы money от уникальных транзакций для счета account_number
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_fillMoneyOfClientAccount() throws SQLException {
    exec("UPDATE client_account_to_replace " +
      "SET money = x.money_sum " +
      "FROM ( " +
      "  SELECT sum(at.money) AS money_sum, at.account_number AS account " +
      "  FROM client_account_transaction_to_replace AS at " +
      "  WHERE at.status = 1 " +
      "  GROUP BY at.account_number " +
      ") AS x " +
      "WHERE status = 1 AND x.account = account_number");
  }

  /**
   * Статус = 2, присвоение идентификатора типа транзакции временной таблице client_account_transaction
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_status2_clientAccountTransaction() throws SQLException {
    exec("UPDATE client_account_transaction_to_replace AS ca " +
      "SET transaction_type = tt.id, status = 2 " +
      "FROM transaction_type AS tt " +
      "WHERE ca.transaction_type = tt.name AND ca.status = 1"
    );
  }

  /**
   * Статус = 2, присвоение типа временной таблице client_account_transaction
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_insertNonexistentClientAccount() throws SQLException {
    exec("INSERT INTO client_account_to_replace(client_id, money, account_number, status) " +
      "SELECT client_id, t.surname, t.name, t.patronymic, t.gender, birth_date, t.charm_id, t.cia_id " +
      "FROM client_to_replace AS t " +
      "WHERE status = 3 " +
      "ON CONFLICT(migration_cia_id) DO UPDATE " +
      "SET id = excluded.id, surname = excluded.surname, name = excluded.name, " +
      "  patronymic = excluded.patronymic, gender = excluded.gender, birth_date = excluded.birth_date, " +
      "  charm = excluded.charm");
  }

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
