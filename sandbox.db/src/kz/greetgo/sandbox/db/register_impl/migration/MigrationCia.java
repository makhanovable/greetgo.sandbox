package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.util.RND;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrationCia extends Migration {


  public MigrationCia(MigrationConfig config) {
    super(config);
  }

  @Override
  protected void createTempTables() throws SQLException {

    String date = getCurrentDateString();

    String client = "TMP_CLIENT_" + date + "_" + config.id;
    String address = "TMP_CLIENT_ADDRESS_" + date + "_" + config.id;
    String phoneNumber = "TMP_CLIENT_PHONE_" + date + "_" + config.id;
    tableNames.put("Client", client);
    tableNames.put("ClientAddr", address);
    tableNames.put("ClientPhone", phoneNumber);

    StringBuilder clientTable = new StringBuilder();
    clientTable.append("create table Client (\n");
    clientTable.append("id varchar(32),\n");
    clientTable.append("name varchar(255),\n");
    clientTable.append("surname varchar(255),\n");
    clientTable.append("patronymic varchar(255),\n");
    clientTable.append("gender varchar(10),\n");
    clientTable.append("birthDate date,\n");
    clientTable.append("charm varchar(32),\n");
    clientTable.append("actual boolean default false,\n");
    clientTable.append("error varchar(100),\n");
    clientTable.append("cia_id varchar(100),\n");
    clientTable.append("PRIMARY KEY (id)\n");
    clientTable.append(")");

    StringBuilder addrTable = new StringBuilder();
    addrTable.append("create table ClientAddr (\n");
    addrTable.append("client character varying(32),\n");
    addrTable.append("type character varying(100),\n");
    addrTable.append("street character varying(100),\n");
    addrTable.append("house character varying(100),\n");
    addrTable.append("flat character varying(100),\n");
    addrTable.append("error varchar(100),\n");
    addrTable.append("PRIMARY KEY (client, type)\n");
    addrTable.append(")\n");

    StringBuilder phoneTable = new StringBuilder();
    phoneTable.append("create table ClientPhone (\n");
    phoneTable.append(" client character varying(32),\n");
    phoneTable.append(" number character varying(100),\n");
    phoneTable.append(" type character varying(100),\n");
    phoneTable.append(" error varchar(100),\n");
    phoneTable.append(" PRIMARY KEY (client, number)\n");
    phoneTable.append(")\n");

    execSql(clientTable);
    execSql(addrTable);
    execSql(phoneTable);

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
