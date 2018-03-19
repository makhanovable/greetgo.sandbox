package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.handler.FrsParser;
import kz.greetgo.sandbox.db.util.DateUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationStatus.NOT_READY;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationStatus.TO_INSERT;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_ACCOUNT;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_TRANSACTION;

public class MigrationFrs extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationFrs(MigrationConfig config, Connection connection) {
    super(config, connection);
  }

  @Override
  protected void createTempTables() throws SQLException {
    String date = DateUtils.getDateWithTimeString(new Date());

    String account = TMP_ACCOUNT.name() + date + "_" + config.id;
    String transaction = TMP_TRANSACTION.name() + date + "_" + config.id;

    tableNames.put(TMP_ACCOUNT, account);
    tableNames.put(TMP_TRANSACTION, transaction);

    String accountTable = "create table " + TMP_ACCOUNT + " (\n" +
      "  no bigserial,\n" +
      "  id varchar(32),\n" +
      "  client_id varchar(32),\n" +
      "  money varchar(100),\n" +
      "  account_number varchar(100),\n" +
      "  registeredAt varchar(100),\n" +
      "  error varchar(100),\n" +
      "  mig_status smallint default " + NOT_READY + ",\n" +
      "  PRIMARY KEY(no)\n" +
      ")";


    String transactionTable = "create table " + TMP_TRANSACTION + " (\n" +
      "  no bigserial,\n" +
      "  id varchar(35),\n" +
      "  account_number varchar(35),\n" +
      "  money varchar(100),\n" +
      "  finished_at varchar(100),\n" +
      "  type varchar(100),\n" +
      "  error varchar(100),\n" +
      "  mig_status smallint default " + NOT_READY + ",\n" +
      "  PRIMARY KEY (no)\n" +
      ")";


    execSql(accountTable);
    execSql(transactionTable);

    execSql(String.format("CREATE INDEX transaction_idx_%s ON " + TMP_TRANSACTION + " (mig_status);", config.id));


  }

  @Override
  protected void parseFileAndUploadToTempTables() throws Exception {

    try (FrsParser parser = new FrsParser(config.idGenerator, getMaxBatchSize(), connection, tableNames);
         BufferedReader br = new BufferedReader(new FileReader(config.toMigrate), 102400)) {
      String line;
      while ((line = br.readLine()) != null) {
        parser.parse(line);
      }
    }


  }

  @Override
  protected void markErrorRows() throws SQLException {
    execSql("update " + TMP_ACCOUNT + " tmp\n" +
      "  SET error='account number must to be not null'\n" +
      "  WHERE tmp.account_number ISNULL");

    execSql("update " + TMP_ACCOUNT + " tmp\n" +
      "  SET error='client must to be not null'\n" +
      "  WHERE tmp.client_id ISNULL");

    execSql("UPDATE " + TMP_TRANSACTION + " tmp\n" +
      "  SET mig_status =" + TO_INSERT + "\n" +
      "  FROM " + TMP_ACCOUNT + " ca\n" +
      "  WHERE ca.error ISNULL AND ca.account_number=tmp.account_number\n");

    execSql("UPDATE " + TMP_TRANSACTION + " tmp\n" +
      "  SET mig_status =" + TO_INSERT + "\n" +
      "  FROM clientaccount ca\n" +
      "  WHERE tmp.mig_status=" + NOT_READY + " AND ca.number=tmp.account_number\n");

    execSql("update " + TMP_TRANSACTION + " tmp\n" +
      "  SET error='transaction must have account'\n" +
      "  WHERE tmp.mig_status=" + NOT_READY);
  }


  private void insertValidAccountRows() throws SQLException {
    //if client exist and no error then ready to insert
//    execSql("update TMP_ACCOUNT tmp\n" +
//      "  SET mig_status =" + TO_INSERT + "\n" +
//      "  FROM client c\n" +
//      "  WHERE c.cia_id = tmp.client_id");

//    execSql("update TMP_ACCOUNT tmp\n" +
//      "  SET mig_status = 'TO_CREATE_CLIENT'\n" +
//      "  WHERE tmp.mig_status='NOT_READY'");

//    execSql(String.format("insert into client (id, cia_id, actual, mig_id)\n" +
//      "  SELECT DISTINCT on(tmp.client_id) tmp.id, tmp.client_id, false as actual, '%s'\n" +
//      "  from TMP_ACCOUNT tmp\n" +
//      "  WHERE tmp.mig_status=" + NOT_READY, config.id));

//    execSql("update TMP_ACCOUNT tmp\n" +
//      "  set mig_status =" + TO_INSERT + "\n" +
//      "  WHERE tmp.mig_status=" + NOT_READY);


  }

  private void insertValidTransactionRows() throws SQLException {


  }


  @Override
  protected void upsertIntoDbValidRows() throws SQLException {

    execSql(String.format("insert into clientaccount (id, client, number, registeredat, actual, mig_id)\n" +
      "  SELECT tmp.id, tmp.client_id, tmp.account_number,\n" +
      "    to_timestamp(tmp.registeredat, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as registeredat,\n" +
      "    false as actual,\n" +
      "    '%s'\n" +
      "  FROM " + TMP_ACCOUNT + " tmp", config.id));


    execSql("insert into transactiontype (id, code, name)" +
      "  SELECT distinct on(type) id, id, type\n" +
      "  FROM " + TMP_TRANSACTION + " tmp" +
      "  WHERE tmp.type NOTNULL\n" +
      "  ON CONFLICT (name) do NOTHING\n");


    execSql("insert into clientaccounttransaction (id, account, money, finishedat, type)\n" +
      "  SELECT tmp.id, tmp.account_number,\n" +
      "    cast(replace(tmp.money,'_','') AS REAL),\n" +
      "    to_timestamp(tmp.finished_at, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as finished_at,\n" +
      "    type.id\n" +
      "  FROM " + TMP_TRANSACTION + " tmp\n" +
      "    left JOIN transactiontype type on type.name=tmp.type\n" +
      "  WHERE tmp.mig_status=" + TO_INSERT);


    //actualize
    execSql(String.format("UPDATE clientaccount c " +
      "  SET actual=true\n" +
      "  WHERE c.mig_id='%s' ;\n", config.id));


    execSql(String.format("DROP INDEX transaction_idx_%s;", config.id));


  }

  @Override
  protected void loadErrorsAndWrite() throws SQLException, IOException {
    String[] accountColumns = {"client_id", "account_number", "error"};
    String[] TrColumns = {"account_number", "error"};

    try (FileWriter writer = new FileWriter(config.error, true);
         BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
      writeErrors(accountColumns, tableNames.get(TMP_ACCOUNT), bufferedWriter);
      writeErrors(TrColumns, tableNames.get(TMP_TRANSACTION), bufferedWriter);
    }
  }
}
