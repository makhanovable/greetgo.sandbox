package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.handler.CiaHandler;
import kz.greetgo.sandbox.db.util.DateUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError.*;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationStatus.*;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.*;


public class MigrationCia extends Migration {


  @SuppressWarnings("WeakerAccess")
  public MigrationCia(MigrationConfig config, Connection connection) throws SQLException {
    super(config, connection);

  }

  @Override
  protected void createTempTables() throws SQLException {
    String date = DateUtils.getDateWithTimeString(new Date());

    String client = TMP_CLIENT.name() + date + "_" + config.id;
    String address = TMP_ADDRESS.name() + date + "_" + config.id;
    String phoneNumber = TMP_PHONE.name() + date + "_" + config.id;
    tableNames.put(TMP_CLIENT, client);
    tableNames.put(TMP_ADDRESS, address);
    tableNames.put(TMP_PHONE, phoneNumber);

    String clientTable = "create table " + TMP_CLIENT.code + " (\n" +
      "  no bigserial,\n" +
      "  id varchar(32),\n" +
      "  cia_id varchar(100),\n" +
      "  client_id varchar(100),\n" +
      "  name varchar(255),\n" +
      "  surname varchar(255),\n" +
      "  patronymic varchar(255),\n" +
      "  gender varchar(10),\n" +
      "  birthDate varchar(20),\n" +
      "  birthDateParsed date,\n" +
      "  charm varchar(32),\n" +
      "  actual boolean default false,\n" +
      "  error varchar(100),\n" +
      "  mig_status smallint default " + NOT_READY + ",\n" +
      "  PRIMARY KEY (no)\n" +
      ")";

    String addrTable = "create table " + TMP_ADDRESS.code + " (\n " +
      "  client_id varchar(32),\n" +
      "  type varchar(100),\n" +
      "  street varchar(100),\n" +
      "  house varchar(100),\n" +
      "  flat varchar(100)\n" +
      ")\n";

    String phoneTable = "create table " + TMP_PHONE.code + " (\n" +
      "  client_id varchar(32),\n" +
      "  number varchar(100),\n" +
      "  type varchar(100)\n" +
      ")\n";

    execSql(clientTable);
    execSql(addrTable);
    execSql(phoneTable);

    execSql(String.format("CREATE INDEX client_mig_%s ON " + TMP_CLIENT.code + " (mig_status);", config.id));
  }

  @Override
  protected void parseFileAndUploadToTempTables() throws Exception {
    try (CiaHandler handler = new CiaHandler(config.idGenerator, getMaxBatchSize(), connection, tableNames);
         InputStream is = new FileInputStream(config.toMigrate)) {

      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(is, handler);
    }
  }


  @Override
  void markErrorRows() throws SQLException {

//    Записи, у которых нет или пустое поле surname, name, birth_date -ошибочные.
    execSql("UPDATE #{{TMP_CLIENT}} set error = '" + CIA_ID_ERROR.message + "' where cia_id is null");
    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + NAME_ERROR.message + "' where error  is null and name is null");
    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + SURNAME_ERROR.message + "' where error  is null and surname is null");
    execSql("update " + TMP_CLIENT.code + " set error = '" + CHARM_ERROR.message + "' where error  is null and charm is null");

    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + BIRTH_NULL_ERROR.message + "' where error  is null and birthDate is null");

    execSql("UPDATE " + TMP_CLIENT.code + "\n" +
      "  set birthDateParsed = birthDate::date" +
      "  where error is null and is_date(birthDate)");

    execSql("UPDATE " + TMP_CLIENT.code + " set error='" + DATE_INVALID_ERROR.message + "' where error is null and birthDateParsed is null");

    //пустые строки, то есть пробелы
    //name, surname
    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + NAME_EMPTY_ERROR.message + "' where error is null and name::char(255)='';");
    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + SURNAME_EMPTY_ERROR.message + "' where error is null and surname::char(255)='';");

    //birthDate validation [18, 100]
    execSql("UPDATE " + TMP_CLIENT.code + " set error = '" + AGE_ERROR.message + "'\n" +
      " where error is null and date_part('year', age(birthDateParsed)) NOT BETWEEN 18 and 100");
  }


  @Override
  void upsertIntoDbValidRows() throws SQLException {

    execSql("UPDATE " + TMP_CLIENT.code + " set patronymic = null where error is null and patronymic::char(255)='';");

    //    charms
    execSql("insert into Charm (id, name)\n" +
      "  select distinct on(charm) id, charm from " + TMP_CLIENT.code + "\n" +
      "  where error is null and charm NOTNULL\n" +
      "  on CONFLICT (name) do NOTHING;");

//    только последний рекорд одинаковых cia_id актуальный
    execSql("UPDATE " + TMP_CLIENT.code + " cl\n" +
      "  SET mig_status=" + LAST_ACTUAL + "\n" +
      "  FROM (SELECT MAX(no) AS no FROM " + TMP_CLIENT.code + " WHERE error IS NULL GROUP BY cia_id) AS tmp\n" +
      "  WHERE cl.no = tmp.no");

    execSql("UPDATE " + TMP_CLIENT.code + " as tmp\n" +
      "  SET client_id = c.id,\n" +
      "    mig_status = " + TO_UPDATE + "\n" +
      "  FROM client as c\n" +
      "  WHERE tmp.mig_status = 'LAST_ACTUAL' and c.cia_id = tmp.cia_id");

    params.add(config.id);
    execSql("INSERT INTO client (id, cia_id, name, surname, patronymic, gender, birthdate, charm, mig_id)\n" +
      "  SELECT id, cia_id, name, surname, patronymic, gender, birthDateParsed, charm, ?\n" +
      "  FROM " + TMP_CLIENT.code + " tmp\n" +
      "  WHERE tmp.mig_status=" + LAST_ACTUAL);

    params.add(config.id);
    execSql(
      "UPDATE client AS c\n" +
        "  SET  (name, surname, patronymic, gender, birthdate, charm, mig_id)=\n" +
        "    (tmp.name, tmp.surname, tmp.patronymic, tmp.gender, tmp.birthDateParsed, tmp.charm, ?)\n" +
        "  FROM " + TMP_CLIENT.code + " tmp\n" +
        "  WHERE tmp.mig_status=" + TO_UPDATE + " AND tmp.client_id=c.id;");

    execSql("INSERT INTO clientaddr (client, cia_id, type, street, house, flat)\n" +
      "  SELECT cl.id, cl.cia_id, addr.type, addr.street, addr.house, addr.flat\n" +
      "  FROM " + TMP_ADDRESS.code + " addr\n" +
      "  JOIN " + TMP_CLIENT.code + " cl ON cl.id=addr.client_id\n" +
      "  WHERE cl.mig_status=" + LAST_ACTUAL);

    execSql("UPDATE clientaddr AS addr\n" +
      "  SET (street, house, flat)=(tmp.street, tmp.house, tmp.flat)\n" +
      "  FROM " + TMP_ADDRESS.code + " tmp\n" +
      "  JOIN " + TMP_CLIENT.code + " cl ON cl.id=tmp.client_id\n" +
      "  WHERE cl.mig_status=" + TO_UPDATE + " AND addr.client=cl.client_id");

    execSql("insert into clientphone (client, number, type)\n" +
      "  SELECT cl.id, phone.number, phone.type\n" +
      "  FROM " + TMP_PHONE.code + " phone\n" +
      "  JOIN " + TMP_CLIENT.code + " cl ON cl.id=phone.client_id\n" +
      "  WHERE cl.mig_status=" + LAST_ACTUAL);

    execSql("INSERT INTO clientphone (client, number, type)\n" +
      "  SELECT cl.client_id, phone.number, phone.type " +
      "  FROM " + TMP_PHONE.code + " phone\n" +
      "  JOIN " + TMP_CLIENT.code + " cl ON cl.id=phone.client_id\n" +
      "  WHERE cl.mig_status=" + TO_UPDATE + "\n" +
      "  ON CONFLICT (client, number)\n" +
      "    DO UPDATE SET type=EXCLUDED.type");

    params.add(config.id);
    //actualize
    execSql("UPDATE Client c " +
      "  SET actual=true\n" +
      "  WHERE c.mig_id=?;\n");

  }

  @Override
  void loadErrorsAndWrite() throws SQLException, IOException {

    String[] ciaColumns = {"cia_id", "error"};

    try (FileWriter writer = new FileWriter(config.error, true);
         BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
      writeErrors(ciaColumns, tableNames.get(TMP_CLIENT), bufferedWriter);
    }


  }


}

