package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.handler.CiaHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.SQLException;

public class MigrationCia extends Migration {


  @SuppressWarnings("WeakerAccess")
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
    clientTable.append("create table Client (\n")
      .append("  id varchar(32),\n")
      .append("  cia_id varchar(100),\n")
      .append("  name varchar(255),\n")
      .append("  surname varchar(255),\n")
      .append("  patronymic varchar(255),\n")
      .append("  gender varchar(10),\n")
      .append("  birthDate date,\n")
      .append("  charm varchar(32),\n")
      .append("  actual boolean default false,\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (id)\n")
      .append(")");

    StringBuilder addrTable = new StringBuilder();
    addrTable.append("create table ClientAddr (\n")
      .append("  client character varying(32),\n")
      .append("  cia_id character varying(32),\n")
      .append("  type character varying(100),\n")
      .append("  street character varying(100),\n")
      .append("  house character varying(100),\n")
      .append("  flat character varying(100),\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (client, type)\n")
      .append(")\n");

    StringBuilder phoneTable = new StringBuilder();
    phoneTable.append("create table ClientPhone (\n")
      .append("  client character varying(32),\n")
      .append("  cia_id character varying(32),\n")
      .append("  number character varying(100),\n")
      .append("  type character varying(100),\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (client, number)\n")
      .append(")\n");

    execSql(clientTable);
    execSql(addrTable);
    execSql(phoneTable);
  }

  @Override
  protected void parseFileAndUploadToTempTables() throws Exception {
    try (CiaHandler handler = new CiaHandler(config.idGenerator, getMaxBatchSize(), connection, tableNames)) {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(config.toMigrate, handler);
    }
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
