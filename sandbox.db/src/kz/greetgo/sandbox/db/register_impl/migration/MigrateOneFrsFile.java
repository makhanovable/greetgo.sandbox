package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;
import kz.greetgo.sandbox.db.register_impl.migration.report.MigrationSimpleReport;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneFrsFile {
  public InputStream inputStream;
  public ErrorFile outputErrorFile;
  public MigrationSimpleReport migrationSimpleReport;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTableName;
  String tmpClientAccountTableName;
  String tmpClientAccountTransactionTableName;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws Exception {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientTableName = "tmp_migration_client_" + additionalId;
    tmpClientAccountTableName = "tmp_migration_client_account_" + additionalId;
    tmpClientAccountTransactionTableName = "tmp_migration_client_account_transaction_" + additionalId;

    exec("CREATE TABLE client_to_replace (" +
      "  id bigint, " +
      "  cia_id varchar(64) UNIQUE, " +
      "  PRIMARY KEY(id)" +
      ")");

    exec("CREATE TABLE client_account_to_replace (" +
      "  record_no bigint, " +
      "  cia_id varchar(64), " +
      "  id bigint, " +
      "  client_id bigint, " +
      "  money numeric(19, 2) NOT NULL DEFAULT 0, " +
      "  account_number varchar(64), " +
      "  registered_at timestamp, " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_account_transaction_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  id bigint, " +
      "  account_id bigint, " +
      "  money numeric(19, 2), " +
      "  finished_at timestamp, " +
      "  transaction_type varchar(256), " +
      "  account_number varchar(64), " +
      "  status int DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");
  }

  void uploadData() throws Exception {
    connection.setAutoCommit(false);

    FrsUploader frsUploader = new FrsUploader();
    frsUploader.connection = connection;
    frsUploader.maxBatchSize = maxBatchSize;
    frsUploader.errorFileWriter = outputErrorFile;
    frsUploader.clientAccountTable = tmpClientAccountTableName;
    frsUploader.clientAccountTransactionTable = tmpClientAccountTransactionTableName;

    long init = System.currentTimeMillis();

    frsUploader.parse(inputStream);

    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.appendParseInfo((post - init) / 1000f);

    connection.setAutoCommit(true);
  }

  void migrateData() throws Exception {
    migrateData_checkForDuplicatesOfTmpClientAccountTransaction();
    migrateData_checkForDuplicatesOfTmpClientAccount();

    migrateData_finalOfTmpTransactionTypeTable();

    migrateData_checkForExistingRecordsOfTmpClientAccountTable();
    migrateData_finalOfTmpClientAccountTable();

    migrateData_checkForExistingRecordsOfTmpClientAccountTransaction();
    migrateData_processBusinessLogicErrorsOfTmpClientAccountTransaction();
    migrateData_fillMoneyOfTmpClientAccount();
    migrateData_finalOfTmpClientAccountTransaction();

    migrateData_close();
  }

  /**
   * Статус = 1, для отсеивания дубликатов клиентских аккаунтов с приоритетом на последнюю запись
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClientAccount() throws Exception {
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
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClientAccountTransaction() throws Exception {
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
   * Статус = 2, если account_number присутствует в постоянной таблице client_account (игнорировать)
   * Статус = 3, если cia_id присутствует в постоянной таблице client (игнорировать)
   * Статус = 4, если отсутствует (insert client_account & client)
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForExistingRecordsOfTmpClientAccountTable() throws Exception {
    exec("UPDATE client_account_to_replace " +
      "SET client_id = ca.client, status = 2 " +
      "FROM client_account as ca " +
      "WHERE status = 1 AND ca.number = account_number"
    );

    exec("UPDATE client_account_to_replace " +
      "SET id = nextval('client_account_id_seq'), client_id = c.id, status = 3 " +
      "FROM client as c " +
      "WHERE status = 1 AND c.migration_cia_id = cia_id"
    );

    exec("INSERT INTO client_to_replace(id, cia_id) " +
      "SELECT nextval('client_id_seq'), ca_r.cia_id " +
      "FROM client_account_to_replace AS ca_r " +
      "WHERE status = 1 " +
      "ON CONFLICT(cia_id) DO NOTHING ");

    exec("UPDATE client_account_to_replace AS ca_r " +
      "SET id = nextval('client_account_id_seq'), client_id = c_r.id, status = 4 " +
      "FROM client_to_replace AS c_r " +
      "WHERE ca_r.status = 1 AND c_r.cia_id = ca_r.cia_id"
    );
  }

  void migrateData_finalOfTmpTransactionTypeTable() throws Exception {
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

  /**
   * Заполнение постоянных client и client_account таблиц
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfTmpClientAccountTable() throws Exception {
    /*exec("INSERT INTO client(id, charm, migration_cia_id, actual) " +
      "SELECT ca_r.client_id, 0, ca_r.cia_id, 0 " +
      "FROM client_to_replace AS ca_r " +
      "WHERE status = 4 " +
      "ON CONFLICT(migration_cia_id) DO NOTHING"
    );*/

    exec("INSERT INTO client(id, charm, migration_cia_id, actual) " +
      "SELECT c_r.id, 0, c_r.cia_id, 0 " +
      "FROM client_to_replace AS c_r "
    );

//TODO: migration_cia_id UNIQUE?
    // ускорить запросы sql
    exec("INSERT INTO client_account(id, client, money, number, registered_at) " +
      "SELECT ca_r.id, ca_r.client_id, 0, ca_r.account_number, ca_r.registered_at " +
      "FROM client_account_to_replace AS ca_r " +
      "WHERE ca_r.status IN (3, 4)"
    );

    /*exec("INSERT INTO client_account(id, client, money, number, registered_at) " +
      "SELECT x.id, x.client_id, x.msum, x.account_number, x.registered_at " +
      "FROM ( " +
      "  SELECT ca_r.id, ca_r.client_id, SUM(cat_r.money) AS msum, cat_r.finished_at " +
      "  FROM client_account_to_replace AS ca_r " +
      "  JOIN client_account_transaction AS cat_r ON account_number = cat_r.account_number " +
      "  WHERE ca_r.status = 3 AND cat_r.status IN (2, 3) " +
      ") AS x"
    );*/
  }

  /**
   * Статус = 2, если такая запись уже существует в постоянной таблице client_account_transaction (игнорировать)
   * Статус = 3, если такой номер аккаунта существует в постоянной таблице client_account (update)
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForExistingRecordsOfTmpClientAccountTransaction() throws Exception {
    exec("UPDATE client_account_transaction_to_replace AS cat_r " +
      "SET status = 2 " +
      "FROM client_account_transaction AS cat " +
      "JOIN client_account AS ca ON cat.account = ca.id " +
      "WHERE cat_r.status = 1 AND cat_r.account_number = ca.number AND cat_r.money = cat.money AND " +
      "cat_r.finished_at = cat.finished_at"
    );

    exec("UPDATE client_account_transaction_to_replace " +
      "SET id = nextval('client_account_transaction_id_seq'), account_id = ca.id, status = 3 " +
      "FROM client_account AS ca " +
      "WHERE status = 1 AND account_number = ca.number"
    );
  }

  /**
   * Заполнение ошибок несуществующего клиентского аккаунта у записей со статусом = 1
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_processBusinessLogicErrorsOfTmpClientAccountTransaction() throws Exception {
    exec("UPDATE client_account_transaction_to_replace " +
      "SET error = 'Аккаунт '||account_number||' не существует " +
      "во временной таблице client_account_transaction у записи = '||record_no " +
      "WHERE status = 1"
    );
  }

  /**
   * Заполнение поля money у постоянной таблицы client_account таблицы после миграции
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_fillMoneyOfTmpClientAccount() throws Exception {
    exec("UPDATE client_account AS ca " +
      "SET money = money + x.msum " +
      "FROM ( " +
      "  SELECT SUM(money) AS msum, account_id " +
      "  FROM client_account_transaction_to_replace " +
      "  WHERE status IN (3, 4) " +
      "  GROUP BY account_id " +
      ") AS x " +
      "WHERE x.account_id = id "
    );
  }

  /**
   * Заполнение постоянной client_account_transaction таблицы
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfTmpClientAccountTransaction() throws Exception {
    exec("INSERT INTO client_account_transaction(id, account, money, finished_at, type) " +
      "SELECT cat_r.id, cat_r.account_id, cat_r.money, cat_r.finished_at, tt.id " +
      "FROM client_account_transaction_to_replace AS cat_r " +
      "JOIN transaction_type AS tt ON tt.name = cat_r.transaction_type " +
      "WHERE cat_r.status = 3"
    );
  }

  /**
   * Статусы для пройденной миграции
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_close() throws Exception {
    exec("UPDATE client_account_transaction_to_replace " +
      "SET status = 4 " +
      "WHERE status IN (2, 3)"
    );

    exec("UPDATE client_account_to_replace " +
      "SET status = 5 " +
      "WHERE status IN (3, 4)"
    );
  }

  private void exec(String sql) throws Exception {
    sql = sql.replaceAll("client_to_replace", tmpClientTableName);
    sql = sql.replaceAll("client_account_to_replace", tmpClientAccountTableName);
    sql = sql.replaceAll("client_account_transaction_to_replace", tmpClientAccountTransactionTableName);

    long init = System.currentTimeMillis();

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }

    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.append((post - init) / 1000f, sql);
  }

  void downloadErrors() throws Exception {
    long init = System.currentTimeMillis();

    String sqlQuery = "SELECT error FROM " + tmpClientAccountTableName + " WHERE status = 1 AND error IS NOT NULL";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next())
          outputErrorFile.appendErrorLine(resultSet.getString("error"));
      }
    }

    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.append((post - init) / 1000f, sqlQuery);
  }
}
