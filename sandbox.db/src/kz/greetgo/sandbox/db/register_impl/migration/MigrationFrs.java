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
import java.util.stream.Stream;

public class MigrationFrs extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationFrs(MigrationConfig config, Connection connection) {
    super(config, connection);
  }

  @Override
  protected void createTempTablesImpl() throws SQLException {
    String date = DateUtils.getDateWithTimeString(new Date());

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

    execSql(String.format("CREATE INDEX account_idx_%s ON TMP_ACCOUNT (mig_status);", config.id));
    execSql(String.format("CREATE INDEX transaction_idx_%s ON TMP_TRANSACTION (mig_status);", config.id));

  }

  @Override
  protected void parseFileAndUploadToTempTablesImpl() throws Exception {

    try (FrsParser parser = new FrsParser(config.idGenerator, getMaxBatchSize(), connection, tableNames);
         BufferedReader br = new BufferedReader(new FileReader(config.toMigrate));
         Stream<String> stream = br.lines()) {

      stream.forEach(parser::parse);
    }
  }

  @Override
  protected void markErrorsAndUpsertIntoDbValidRowsImpl() throws SQLException {

    //////ACCOUNTS
    execSql("update TMP_ACCOUNT tmp\n" +
      "  SET error='account number must to be not null', " +
      "      mig_status='INVALID'\n" +
      "  WHERE tmp.account_number ISNULL");

    execSql("update TMP_ACCOUNT tmp\n" +
      "  SET error='client must to be not null', " +
      "      mig_status='INVALID'\n" +
      "  WHERE tmp.client_id ISNULL");

    //if client exist and no error then ready to insert
    execSql("update TMP_ACCOUNT tmp\n" +
      "  SET mig_status = 'TO_INSERT'\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = tmp.client_id and tmp.error is null;");

//    execSql("update TMP_ACCOUNT tmp\n" +
//      "  SET mig_status = 'TO_CREATE_CLIENT'\n" +
//      "  WHERE tmp.mig_status='NOT_READY'");

    execSql(String.format("insert into client (id, cia_id, actual, mig_id)\n" +
      "  SELECT DISTINCT on(tmp.client_id) tmp.id, tmp.client_id, false as actual, '%s'\n" +
      "  from TMP_ACCOUNT tmp\n" +
      "  WHERE tmp.mig_status='NOT_READY'", config.id));

    execSql("update TMP_ACCOUNT tmp\n" +
      "  set mig_status = 'TO_INSERT'\n" +
      "  WHERE tmp.mig_status='NOT_READY'");

    execSql(String.format("insert into clientaccount (id, client, number, registeredat, actual, mig_id)\n" +
      "  SELECT tmp.id, tmp.client_id, tmp.account_number,\n" +
      "    to_timestamp(tmp.registeredat, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as registeredat,\n" +
      "    false as actual,\n" +
      "    '%s'\n" +
      "  FROM TMP_ACCOUNT tmp\n" +
      "  WHERE tmp.mig_status='TO_INSERT'", config.id));

    //////TRANSACTIONS
    //transaction types
//    execSql("update TMP_TRANSACTION\n" +
//      "  SET mig_status='TO_CREATE_TR._TYPE'\n" +
//      "  FROM (\n" +
//      "    SELECT tmp.type FROM TMP_TRANSACTION tmp\n" +
//      "    EXCEPT SELECT name from transactiontype) types\n" +
//      "  WHERE types.type = account.type;");
//
//    execSql("insert into transactiontype (id, code, name)\n" +
//      "  SELECT DISTINCT on(type) id, id, type\n" +
//      "  FROM TMP_TRANSACTION tmp\n" +
//      "  WHERE tmp.mig_status='TO_CREATE_TR._TYPE'");


    execSql("insert into transactiontype (id, code, name)" +
      "  SELECT distinct on(type) id, id, type\n" +
      "  FROM TMP_TRANSACTION tmp" +
      "  WHERE tmp.type NOTNULL\n" +
      "  ON CONFLICT (name) do NOTHING\n");

    execSql("UPDATE TMP_TRANSACTION tmp\n" +
      "  SET mig_status = 'TO_INSERT'\n" +
      "  FROM clientaccount ca\n" +
      "  WHERE ca.number=tmp.account_number\n");

    execSql("update TMP_TRANSACTION tmp\n" +
      "  SET error='transaction must have account'\n" +
      "  WHERE tmp.mig_status='NOT_READY';");


    execSql("insert into clientaccounttransaction (id, account, money, finishedat, type)\n" +
      "  SELECT tmp.id, tmp.account_number,\n" +
      "    cast(replace(tmp.money,'_','') AS REAL),\n" +
      "    to_timestamp(tmp.finished_at, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as finished_at,\n" +
      "    type.id\n" +
      "  FROM TMP_TRANSACTION tmp\n" +
      "    left JOIN transactiontype type on type.name=tmp.type\n" +
      "  WHERE tmp.mig_status='TO_INSERT'");

    //actualize
    execSql(String.format("UPDATE clientaccount c " +
      "  SET actual=true\n" +
      "  WHERE c.mig_id='%s' ;\n", config.id));

    execSql(String.format("DROP INDEX account_idx_%s;", config.id));
    execSql(String.format("DROP INDEX transaction_idx_%s;", config.id));

  }

  @Override
  protected void loadErrorsAndWriteImpl() throws SQLException, IOException {
    String[] accountColumns = {"client_id", "account_number", "error"};
    String[] TrColumns = {"account_number", "error"};

    try (FileWriter writer = new FileWriter(config.error, true);
         BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
      writeErrors(accountColumns, tableNames.get("TMP_ACCOUNT"), bufferedWriter);
      writeErrors(TrColumns, tableNames.get("TMP_TRANSACTION"), bufferedWriter);
    }
  }
}
