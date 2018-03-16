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

import static kz.greetgo.sandbox.db.register_impl.migration.MigrationStatuses.*;


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
      .append("  mig_status smallint default " + NOT_READY + ",\n")
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
      .append("  mig_status smallint default " + NOT_READY + ",\n")
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
      .append("  mig_status smallint default " + NOT_READY + ",\n")
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
    try (CiaHandler handler = new CiaHandler(config.idGenerator, getMaxBatchSize(), connection, tableNames);
         InputStream is = new FileInputStream(config.toMigrate)) {

      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(is, handler);
    }
  }

  @Override
  protected void markErrorsAndUpsertIntoDbValidRowsImpl() throws SQLException {
    //required: Записи, у которых нет или пустое поле surname, name, birth_date - ошибочные.
//    execSql("UPDATE TMP_CLIENT set error = 'cia_id cant be null'    where cia_id is null");
//    execSql("UPDATE TMP_CLIENT set error = 'name cant be null'      where error  is null and name      is null");
//    execSql("UPDATE TMP_CLIENT set error = 'surname cant be null'   where error  is null and surname   is null");
//    execSql("UPDATE TMP_CLIENT set error = 'birthDate cant be null' where error  is null and birthDate is null");
//    execSql("update TMP_CLIENT set error = 'charm cant be null'     where error  is null and charm     is null");
//
//    //пустые строки, то есть пробелы
//    //name, surname
//    execSql("UPDATE TMP_CLIENT set error = 'name cant be empty' where error is null and name::char(255)='';");
//    execSql("UPDATE TMP_CLIENT set error = 'surname cant be empty' where error is null and surname::char(255)='';");
//
//    //birthDate validation [10, 200]
//    execSql("UPDATE TMP_CLIENT set error = 'birthDate must be not more than 200 and not less than 10'\n" +
//      " where error is null and date_part('year', age(birthDate)) NOT BETWEEN 10 and 200");

    //charms
//    execSql("insert into Charm (id, name)\n" +
//      "  select distinct on(charm) id, charm from TMP_CLIENT\n" +
//      "  where error is null and charm NOTNULL\n" +
//      "  on CONFLICT (name) do NOTHING;");
//
////    только последний рекорд одинаковых cia_id актуальный
////    execSql("UPDATE TMP_CLIENT tmp\n" +
////      "  SET mig_status=" + LAST_ACTUAL + " FROM\n" +
////      "    (SELECT id, ROW_NUMBER() OVER(PARTITION BY cia_id ORDER BY no DESC) AS rn\n" +
////      "      from TMP_CLIENT WHERE error is NULL) AS rown\n" +
////      "WHERE tmp.id = rown.id and rown.rn = 1");
//
//    execSql("UPDATE TMP_CLIENT cl\n" +
//      "  SET mig_status=" + LAST_ACTUAL + "\n" +
//      "  FROM (SELECT MAX(no) AS no FROM TMP_CLIENT WHERE error IS NULL GROUP BY cia_id) AS tmp\n" +
//      "  WHERE cl.no = tmp.no");
//
//    execSql("UPDATE TMP_CLIENT as tmp\n" +
//      "  SET client_id = c.id\n" +
//      "  FROM client as c\n" +
//      "  WHERE tmp.mig_status=" + LAST_ACTUAL + " and c.cia_id=tmp.cia_id");
//
//    execSql("UPDATE TMP_CLIENT\n" +
//      "  SET mig_status =" + TO_INSERT + "\n" +
//      "  WHERE mig_status=" + LAST_ACTUAL + " AND client_id IS NULL\n");
//
//    execSql("UPDATE TMP_CLIENT\n" +
//      "  SET mig_status =" + TO_UPDATE + "\n" +
//      "  WHERE mig_status=" + LAST_ACTUAL + " AND client_id NOTNULL\n");
//
//    execSql(String.format(
//      "INSERT INTO client (id, cia_id, name, surname, patronymic, gender, birthdate, charm, mig_id)\n" +
//        "  SELECT id, cia_id, name, surname, patronymic, gender, birthdate, charm, '%s'\n" +
//        "  FROM TMP_CLIENT tmp\n" +
//        "  WHERE tmp.mig_status=" + TO_INSERT, config.id));
//
//    execSql(String.format(
//      "UPDATE client as c\n" +
//        "  SET  (name, surname, patronymic, gender, birthdate, charm, mig_id)=\n" +
//        "    (tmp.name, tmp.surname, tmp.patronymic, tmp.gender, tmp.birthdate, tmp.charm, '%s')\n" +
//        "  FROM TMP_CLIENT tmp\n" +
//        "  WHERE tmp.mig_status=" + TO_UPDATE + " AND tmp.client_id=c.id;", config.id));
//
//    execSql("INSERT INTO clientaddr (client, cia_id, type, street, house, flat)\n" +
//      "  SELECT addr.client_id, addr.cia_id, addr.type, addr.street, addr.house, addr.flat\n" +
//      "  FROM TMP_ADDRESS addr join TMP_CLIENT cl on cl.cia_id=addr.cia_id\n" +
//      "  WHERE cl.mig_status=" + TO_INSERT);
//
//    execSql("UPDATE clientaddr AS addr\n" +
//      "  set (street, house, flat)=(tmp.street, tmp.house, tmp.flat)\n" +
//      "  from TMP_ADDRESS tmp join TMP_CLIENT cl on cl.cia_id=tmp.cia_id\n" +
//      "  WHERE cl.mig_status=" + TO_UPDATE + " and addr.cia_id=tmp.cia_id");
//
//    execSql("insert into clientphone (client, number, type)\n" +
//      "  SELECT phone.client_id, phone.number, phone.type\n" +
//      "  FROM TMP_PHONE phone join TMP_CLIENT cl on cl.cia_id=phone.cia_id\n" +
//      "  WHERE cl.mig_status=" + TO_INSERT);
//
//    execSql("insert into clientphone (client, number, type)\n" +
//      "  SELECT cl.client_id, phone.number, phone.type " +
//      "  from TMP_PHONE phone join TMP_CLIENT cl on cl.cia_id=phone.cia_id\n" +
//      "  WHERE cl.mig_status=" + TO_UPDATE + "\n" +
//      "  ON CONFLICT (client, number)\n" +
//      "    do update set type=EXCLUDED.type");
//
//    //actualize
//    execSql(String.format("UPDATE Client c set actual=true\n" +
//      "where c.mig_id='%s';\n", config.id));
//
//    execSql(String.format("DROP INDEX client_idx_%s;", config.id));
//    execSql(String.format("DROP INDEX address_idx_%s;", config.id));
//    execSql(String.format("DROP INDEX phone_idx_%s;", config.id));


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
