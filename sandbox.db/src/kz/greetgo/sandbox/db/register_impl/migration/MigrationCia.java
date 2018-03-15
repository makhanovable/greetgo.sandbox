package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.handler.CiaHandler;
import kz.greetgo.sandbox.db.util.DateUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class MigrationCia extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationCia(MigrationConfig config, Connection connection) {
    super(config, connection);

  }

  @Override
  protected void createTempTablesImpl() throws SQLException {
    String date = DateUtils.getDateWithTimeString(new Date());

    String client = "TMP_CLIENT_" + date + "_" + config.id;
    String address = "TMP_CLIENT_ADDRESS_" + date + "_" + config.id;
    String phoneNumber = "TMP_CLIENT_PHONE_" + date + "_" + config.id;
    tableNames.put("TMP_CLIENT", client);
    tableNames.put("TMP_ADDRESS", address);
    tableNames.put("TMP_PHONE", phoneNumber);

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder clientTable = new StringBuilder();
    clientTable.append("create table TMP_CLIENT (\n")
      .append("  no bigserial,\n")
      .append("  id varchar(32),\n")
      .append("  cia_id varchar(100),\n")
      .append("  client_id varchar(100),\n")
      .append("  name varchar(255),\n")
      .append("  surname varchar(255),\n")
      .append("  patronymic varchar(255),\n")
      .append("  gender varchar(10),\n")
      .append("  birthDate date,\n")
      .append("  charm varchar(32),\n")
      .append("  actual boolean default false,\n")
      .append("  error varchar(100),\n")
      .append("  mig_status varchar(100) default 'NOT_READY',\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")");

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder addrTable = new StringBuilder();
    addrTable.append("create table TMP_ADDRESS (\n")
      .append("  no bigserial,\n")
      .append("  client_id varchar(32),\n")
      .append("  cia_id varchar(32),\n")
      .append("  type varchar(100),\n")
      .append("  street varchar(100),\n")
      .append("  house varchar(100),\n")
      .append("  flat varchar(100),\n")
      .append("  mig_status varchar(100) default 'NOT_READY',\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")\n");

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder phoneTable = new StringBuilder();
    phoneTable.append("create table TMP_PHONE (\n")
      .append("  no bigserial,\n")
      .append("  client_id varchar(32),\n")
      .append("  cia_id varchar(32),\n")
      .append("  number varchar(100),\n")
      .append("  type varchar(100),\n")
      .append("  mig_status character varying(100) default 'NOT_READY',\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")\n");

    execSql(clientTable.toString());
    execSql(addrTable.toString());
    execSql(phoneTable.toString());

    execSql(String.format("CREATE INDEX client_idx_%s ON TMP_CLIENT (mig_status);", config.id));
    execSql(String.format("CREATE INDEX address_idx_%s ON TMP_ADDRESS (mig_status);", config.id));
    execSql(String.format("CREATE INDEX phone_idx_%s ON TMP_PHONE (mig_status);", config.id));

  }

  @Override
  protected void parseFileAndUploadToTempTablesImpl() throws Exception {
    try (CiaHandler handler = new CiaHandler(config.idGenerator, getMaxBatchSize(), connection, tableNames)) {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(config.toMigrate, handler);
    }
  }

  @Override
  protected void markErrorsAndUpsertIntoDbValidRowsImpl() throws SQLException {
    //required: Записи, у которых нет или пустое поле surname, name, birth_date - ошибочные.
    execSql("update TMP_CLIENT set error = 'cia_id cant be null' where cia_id is null and error is null");
    execSql("update TMP_CLIENT set error = 'name cant be null' where name is null and error is null");
    execSql("update TMP_CLIENT set error = 'surname cant be null' where surname is null and error is null");
    execSql("update TMP_CLIENT set error = 'birthDate cant be null' where birthDate is null and error is null");

    //charm
    execSql("update TMP_CLIENT set error = 'charm cant be null' where charm is null and error is null");

    //пустые строки, то есть пробелы
    //name, surname
    execSql("update TMP_CLIENT set error = 'name cant be empty' where name::char(255)='' and error is null");
    execSql("update TMP_CLIENT set error = 'surname cant be empty' where surname::char(255)='' and error is null");

    //birthDate validation [3, 1000]
    execSql("update TMP_CLIENT set error = 'birthDate must be not more than 1000 and not less than 3'\n" +
      "where date_part('year', age(birthDate)) NOT BETWEEN 10 and 200 and error is null");

    //charms
    execSql("insert into Charm (id, name)\n" +
      "  select distinct on(charm) id, charm from TMP_CLIENT\n" +
      "  where error is null and charm NOTNULL\n" +
      "  group by id, charm" +
      "  on CONFLICT (name) do NOTHING;");

    //только последний рекорд одинаковых cia_id актуальный
    execSql("update TMP_CLIENT tmp set mig_status='LAST_ACTUAL' FROM\n" +
      "  (SELECT id, error, ROW_NUMBER() OVER(PARTITION BY cia_id order by no desc) AS rn\n" +
      "                  from TMP_CLIENT WHERE error is NULL) as rown\n" +
      "WHERE tmp.id = rown.id and rown.rn = 1;");

    execSql("update TMP_CLIENT as tmp\n" +
      "set client_id = c.id\n" +
      "from client as c\n" +
      "where c.cia_id=tmp.cia_id AND TMP.mig_status='LAST_ACTUAL';");

    execSql("update TMP_CLIENT\n" +
      "set mig_status = 'TO_INSERT'\n" +
      "WHERE mig_status='LAST_ACTUAL' AND client_id IS NULL\n");

    execSql("update TMP_CLIENT\n" +
      "set mig_status = 'TO_UPDATE'\n" +
      "WHERE mig_status='LAST_ACTUAL' AND client_id NOTNULL\n");

    execSql(String.format("INSERT INTO client (id, cia_id, name, surname, patronymic, gender, birthdate, charm, mig_id)\n" +
      "  SELECT id, cia_id, name, surname, patronymic, gender, birthdate, charm, '%s'\n" +
      "  FROM TMP_CLIENT tmp\n" +
      "WHERE tmp.mig_status='TO_INSERT';", config.id));

    execSql(String.format("UPDATE client as c\n" +
      "SET  (name, surname, patronymic, gender, birthdate, charm, mig_id)=\n" +
      "    (tmp.name, tmp.surname, tmp.patronymic, tmp.gender, tmp.birthdate, tmp.charm, '%s')\n" +
      "FROM TMP_CLIENT tmp\n" +
      "WHERE tmp.mig_status='TO_UPDATE' AND tmp.client_id=c.id;", config.id));

    execSql("update TMP_ADDRESS tmp\n" +
      "  SET mig_status=cl.mig_status\n" +
      "  FROM TMP_CLIENT cl\n" +
      "  WHERE tmp.cia_id=cl.cia_id"); // and (cl.mig_status='TO_INSERT' OR cl.mig_status='TO_UPDATE')

    execSql("INSERT INTO clientaddr (client, cia_id, type, street, house, flat)\n" +
      "  SELECT addr.client_id, addr.cia_id, addr.type, addr.street, addr.house, addr.flat\n" +
      "  FROM TMP_ADDRESS addr\n" +
      "  WHERE addr.mig_status='TO_INSERT';");

    execSql("UPDATE clientaddr AS addr\n" +
      "  set (street, house, flat)=(tmp.street, tmp.house, tmp.flat)\n" +
      "  from TMP_ADDRESS tmp\n" +
      "  WHERE addr.cia_id=tmp.cia_id AND tmp.mig_status='TO_UPDATE';");


    execSql("update TMP_PHONE tmp\n" +
      "  SET mig_status=cl.mig_status\n" +
      "  FROM TMP_CLIENT cl\n" +
      "  WHERE tmp.cia_id=cl.cia_id");

    execSql("insert into clientphone (client, number, type)\n" +
      "  SELECT phone.client_id, phone.number, phone.type\n" +
      "  FROM TMP_PHONE phone\n" +
      "  WHERE phone.mig_status='TO_INSERT';");

    execSql("insert into clientphone (client, number, type)\n" +
      "  SELECT client_id, number, type " +
      "  from TMP_PHONE tmp\n" +
      "  WHERE tmp.mig_status='TO_UPDATE'\n" +
      "  ON CONFLICT (client, number)\n" +
      "    do UPDATE set type=EXCLUDED.type");

    //actualize
    execSql(String.format("update Client c set actual=true\n" +
      "where c.mig_id='%s';\n", config.id));


    execSql(String.format("DROP INDEX client_idx_%s;", config.id));
    execSql(String.format("DROP INDEX address_idx_%s;", config.id));
    execSql(String.format("DROP INDEX phone_idx_%s;", config.id));
    
  }

  @Override
  protected void loadErrorsAndWriteImpl() throws SQLException, IOException {

    String[] ciaColumns = {"cia_id", "error"};

    try (FileWriter writer = new FileWriter(config.error, true);
         BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
      writeErrors(ciaColumns, tableNames.get("TMP_CLIENT"), bufferedWriter);
      writeErrors(ciaColumns, tableNames.get("TMP_ADDRESS"), bufferedWriter);
      writeErrors(ciaColumns, tableNames.get("TMP_PHONE"), bufferedWriter);
    }


  }

}
