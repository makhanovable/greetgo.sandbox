package kz.greetgo.sandbox.db.register_impl.migration;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;

public class MigrationFrs extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationFrs(MigrationConfig config) {
    super(config);
  }

  @Override
  protected void createTempTables() throws SQLException {
    String date = getCurrentDateString();

    String account = "TMP_ACCOUNT_" + date + "_" + config.id;
    String transaction = "TMP_TRANSACTION_" + date + "_" + config.id;

    tableNames.put("ClientAccount", account);
    tableNames.put("ClientAccountTransaction", transaction);

    StringBuilder accountTable = new StringBuilder();
    accountTable.append("create table ClientAccount (\n")
      .append("  id varchar(32),\n")
      .append("  client varchar(32),\n")
      .append("  money real,\n")
      .append("  number varchar(100),\n")
      .append("  registeredAt timestamp with time zone,\n")
      .append("  actual boolean default false,\n")
      .append("  error varchar(100),\n")
      .append("  cia_id varchar(100),\n")
      .append("  PRIMARY KEY(id)\n")
      .append(")");

    StringBuilder transactionTable = new StringBuilder();
    transactionTable.append("create table ClientAccountTransaction (\n")
      .append("  id varchar(35),\n")
      .append("  account varchar(35),\n")
      .append("  money real,\n")
      .append("  registeredAt timestamp with time zone,\n")
      .append("  type varchar(35),\n")
      .append("  PRIMARY KEY (id)\n")
      .append(")");

    execSql(accountTable);
    execSql(transactionTable);

    throw new NotImplementedException();
  }

  @Override
  protected void parseFileAndUploadToTempTables() {
    throw new NotImplementedException();
  }

  @Override
  protected void updateErrorRows() {
    throw new NotImplementedException();
  }

  @Override
  protected void loadErrorsAndWrite() {
    throw new NotImplementedException();
  }
}
