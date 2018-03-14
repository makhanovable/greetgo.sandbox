package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.handler.FrsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

public class MigrationFrs extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationFrs(MigrationConfig config, Connection connection) {
    super(config, connection);
  }

  @Override
  protected void createTempTables() throws SQLException {
    String date = getCurrentTimeInMillsString();

    String account = "TMP_ACCOUNT_" + date + "_" + config.id;
    String transaction = "TMP_TRANSACTION_" + date + "_" + config.id;

    tableNames.put("TMP_ACCOUNT", account);
    tableNames.put("TMP_TRANSACTION", transaction);

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder accountTable = new StringBuilder();
    accountTable.append("create table TMP_ACCOUNT (\n")
      .append("  no bigserial,\n")
      .append("  id varchar(32),\n")
      .append("  client_id varchar(32),\n")
      .append("  money varchar(100),\n")
      .append("  account_number varchar(100),\n")
      .append("  registeredAt varchar(100),\n")
      .append("  error varchar(100),\n")
      .append("  mig_status varchar(100) default 'NOT_READY',\n")
      .append("  PRIMARY KEY(no)\n")
      .append(")");

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder transactionTable = new StringBuilder();
    transactionTable.append("create table TMP_TRANSACTION (\n")
      .append("  no bigserial,\n")
      .append("  id varchar(35),\n")
      .append("  account_number varchar(35),\n")
      .append("  money varchar(100),\n")
      .append("  finished_at varchar(100),\n")
      .append("  type varchar(100),\n")
      .append("  error varchar(100),\n")
      .append("  mig_status varchar(100) default 'NOT_READY',\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")");

    execSql(accountTable.toString());
    execSql(transactionTable.toString());
  }

  @Override
  protected void parseFileAndUploadToTempTables() throws Exception {

    try (FrsParser parser = new FrsParser(config.idGenerator, getMaxBatchSize(), connection, tableNames);
         BufferedReader br = new BufferedReader(new FileReader(config.toMigrate));
         Stream<String> stream = br.lines()) {

      stream.forEach(parser::parse);
    }
  }

  @Override
  protected void markErrorsAndUpsertIntoDbValidRows() throws SQLException {

    //////ACCOUNTS
    execSql("update TMP_ACCOUNT tmp\n" +
      "set error='account number must to be not null'\n" +
      "WHERE tmp.account_number ISNULL");

    execSql("update TMP_ACCOUNT tmp\n" +
      "set error='client must to be not null'\n" +
      "WHERE tmp.client_id ISNULL");

    //if client exist and no error then ready to insert
    execSql("update TMP_ACCOUNT tmp\n" +
      "set mig_status = 'TO_INSERT'\n" +
      "FROM client c\n" +
      "WHERE c.cia_id = tmp.client_id and tmp.error is null;");

    execSql("update TMP_ACCOUNT tmp\n" +
      "set mig_status = 'TO_CREATE_CLIENT'\n" +
      "WHERE tmp.mig_status='NOT_READY' and tmp.error is null;");

    execSql("insert into client (id, cia_id, actual, mig_status)\n" +
      "  SELECT DISTINCT on(tmp.client_id) tmp.id, tmp.client_id, false as actual, 'created_for_account'\n" +
      "  from TMP_ACCOUNT tmp\n" +
      "  WHERE tmp.error is null and tmp.mig_status='TO_CREATE_CLIENT'");

    execSql("update TMP_ACCOUNT tmp\n" +
      "set mig_status = 'TO_INSERT'\n" +
      "WHERE tmp.mig_status='TO_CREATE_CLIENT' and tmp.error is null;");

    execSql("insert into clientaccount (id, client, number, registeredat, actual, mig_status)\n" +
      "  SELECT tmp.id, tmp.client_id, tmp.account_number,\n" +
      "    to_timestamp(tmp.registeredat, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as registeredat,\n" +
      "    false as actual,\n" +
      "    'just_migrated'\n" +
      "  from TMP_ACCOUNT tmp\n" +
      "WHERE tmp.mig_status='TO_INSERT'");

    //////TRANSACTIONS
    //transaction types
    execSql("update TMP_TRANSACTION account\n" +
      "set mig_status='TO_CREATE_TR._TYPE'\n" +
      "FROM (\n" +
      "  SELECT tmp.type from TMP_TRANSACTION tmp\n" +
      "  EXCEPT SELECT name from transactiontype) types\n" +
      "WHERE types.type = account.type;");

    execSql("insert into transactiontype (id, code, name)\n" +
      "  SELECT DISTINCT on(type) id, id, type\n" +
      "  from TMP_TRANSACTION tmp\n" +
      "  WHERE tmp.mig_status='TO_CREATE_TR._TYPE'");
// FIXME: 3/14/18 раскомментить
    //???нужно узнать
//    execSql("UPDATE TMP_TRANSACTION tmp\n" +
//      "SET error='transaction must have account'\n" +
//      "FROM clientaccount ca\n" +
//      "WHERE ca.number=tmp.account\n");

    execSql("update TMP_TRANSACTION tmp\n" +
      "set mig_status = 'TO_INSERT'\n" +
      "WHERE tmp.error is null;");

    execSql("insert into clientaccounttransaction (id, account, money, finishedat, type, actual)\n" +
      "  SELECT tmp.id, tmp.account_number,\n" +
      "    cast(replace(tmp.money,'_','') AS REAL),\n" +
      "    to_timestamp(tmp.finished_at, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as finished_at,\n" +
      "    type.id, false as actual\n" +
      "  from TMP_TRANSACTION tmp\n" +
      "    left JOIN transactiontype type on type.name=tmp.type\n" +
      "  WHERE tmp.mig_status='TO_INSERT'");

    //actualize
    execSql("update clientaccount c set mig_status='actualized', actual=true\n" +
      "where c.mig_status='just_migrated' or c.mig_status='just_updated';\n");

  }

  @Override
  protected void loadErrorsAndWrite() throws SQLException, IOException {
    String[] accountColumns = {"client_id", "account_number", "error"};
    String[] TrColumns = {"account_number", "error"};

    try (FileWriter writer = new FileWriter(config.error, true)) {
      writeErrors(accountColumns, tableNames.get("TMP_ACCOUNT"), writer);
      writeErrors(TrColumns, tableNames.get("TMP_TRANSACTION"), writer);
    }
  }
}