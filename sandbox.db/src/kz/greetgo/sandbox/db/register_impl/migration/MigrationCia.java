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
      .append("  mig_status varchar(100) default 'NOT READY',\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")");

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder addrTable = new StringBuilder();
    addrTable.append("create table TMP_ADDRESS (\n")
      .append("  no bigserial,\n")
      .append("  cia_id character varying(32),\n")
      .append("  type character varying(100),\n")
      .append("  street character varying(100),\n")
      .append("  house character varying(100),\n")
      .append("  flat character varying(100),\n")
      .append("  mig_status character varying(100) default 'NOT READY',\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")\n");

    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder phoneTable = new StringBuilder();
    phoneTable.append("create table TMP_PHONE (\n")
      .append("  no bigserial,\n")
      .append("  cia_id character varying(32),\n")
      .append("  number character varying(100),\n")
      .append("  type character varying(100),\n")
      .append("  mig_status character varying(100) default 'NOT READY',\n")
      .append("  error varchar(100),\n")
      .append("  PRIMARY KEY (no)\n")
      .append(")\n");

    execSql(clientTable.toString());
    execSql(addrTable.toString());
    execSql(phoneTable.toString());
  }

  @Override
  protected void parseFileAndUploadToTempTables() throws Exception {
    try (CiaHandler handler = new CiaHandler(config.idGenerator, getMaxBatchSize(), connection, tableNames)) {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(config.toMigrate, handler);
    }
  }

  @Override
  protected void UpsertIntoDbValidRowsAndMarkErrors() throws SQLException {
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
    execSql("update TMP_CLIENT tmp set mig_status='LAST ACTUAL' FROM\n" +
      "  (SELECT id, error, ROW_NUMBER() OVER(PARTITION BY cia_id order by no desc) AS rn\n" +
      "                  from TMP_CLIENT WHERE error is NULL) as rown\n" +
      "WHERE tmp.id = rown.id and rown.rn = 1;");

    execSql("update TMP_CLIENT as tmp\n" +
      "set client_id = c.id\n" +
      "from client as c\n" +
      "where c.cia_id=tmp.cia_id AND TMP.mig_status='LAST ACTUAL';");

    execSql("update TMP_CLIENT\n" +
      "set mig_status = 'TO INSERT'\n" +
      "WHERE mig_status='LAST ACTUAL' AND client_id IS NULL\n");

    execSql("update TMP_CLIENT\n" +
      "set mig_status = 'TO UPDATE'\n" +
      "WHERE mig_status='LAST ACTUAL' AND client_id NOTNULL\n");

    execSql("INSERT INTO client (id, cia_id, name, surname, patronymic, gender, birthdate, charm, mig_status)\n" +
      "  SELECT id, cia_id, name, surname, patronymic, gender, birthdate, charm, 'just migrated'\n" +
      "  FROM TMP_CLIENT tmp\n" +
      "WHERE tmp.mig_status='TO INSERT';");

    execSql("UPDATE client as c\n" +
      "SET  (name, surname, patronymic, gender, birthdate, charm, mig_status)=\n" +
      "    (tmp.name, tmp.surname, tmp.patronymic, tmp.gender, tmp.birthdate, tmp.charm, 'just migrated')\n" +
      "FROM TMP_CLIENT tmp\n" +
      "WHERE tmp.mig_status='TO UPDATE' AND tmp.client_id=c.id;");

    //addr// нужно узнать
//    execSql("update TMP_ADDRESS set error = 'type cant be null' where type is null and error is null");
//    execSql("update TMP_ADDRESS set error = 'street cant be null' where street is null and error is null");
//    execSql("update TMP_ADDRESS set error = 'house cant be null' where house is null and error is null");

    //только последний рекорд одинаковых cia_id актуальный
    execSql("update TMP_ADDRESS tmp set mig_status='LAST ACTUAL' FROM\n" +
      "  (SELECT no, cia_id, ROW_NUMBER() OVER(PARTITION BY type, cia_id order by no desc) AS rn\n" +
      "   from TMP_ADDRESS WHERE error is NULL) as rown\n" +
      "WHERE rown.rn=1 and tmp.no=rown.no;");

    execSql("insert into clientaddr (client, cia_id, type, street, house, flat)\n" +
      "    SELECT cl.id, addr.cia_id, addr.type, addr.street, addr.house, addr.flat\n" +
      "    FROM TMP_ADDRESS addr\n" +
      "      INNER JOIN TMP_CLIENT cl\n" +
      "      on addr.cia_id=cl.cia_id\n" +
      "    WHERE cl.mig_status='TO INSERT' and addr.mig_status='LAST ACTUAL';");

    execSql("UPDATE clientaddr AS addr\n" +
      "  set (street, house, flat)=(tmp.street, tmp.house, tmp.flat)\n" +
      "  from TMP_ADDRESS tmp\n" +
      "  INNER JOIN TMP_CLIENT cl\n" +
      "    on tmp.cia_id=cl.cia_id\n" +
      "WHERE cl.mig_status='TO UPDATE' AND addr.cia_id=tmp.cia_id AND tmp.mig_status='LAST ACTUAL';");


    execSql("update TMP_PHONE tmp set mig_status='LAST ACTUAL' FROM\n" +
      "(SELECT no, cia_id, ROW_NUMBER() OVER(PARTITION BY type, cia_id order by no desc) AS rn\n" +
      "from TMP_PHONE  WHERE error is NULL) as rown\n" +
      "WHERE rown.rn=1 and tmp.no=rown.no;");


    execSql("insert into clientphone (client, number, type)\n" +
      "  SELECT cl.id, phone.number, phone.type\n" +
      "  FROM TMP_PHONE phone\n" +
      "    INNER JOIN TMP_CLIENT cl\n" +
      "      on phone.cia_id=cl.cia_id\n" +
      "  WHERE cl.mig_status='TO INSERT' and phone.mig_status='LAST ACTUAL';");

    execSql("insert into clientphone (client, number, type)\n" +
      "  SELECT client_id, number, type from TMP_PHONE tmp\n" +
      "    INNER JOIN TMP_CLIENT cl\n" +
      "      ON tmp.cia_id = cl.cia_id\n" +
      "  WHERE cl.mig_status='TO UPDATE' and tmp.mig_status='LAST ACTUAL'\n" +
      "ON CONFLICT (client, number)\n" +
      "  do UPDATE set type=EXCLUDED.type");

    //actualize
    execSql("update Client c set mig_status='actualized', actual=true\n" +
      "where c.mig_status='just inserted' or c.mig_status='just updated';\n");

//    throw new NotImplementedException();

  }

  @Override
  protected void loadErrorsAndWrite() {
    throw new NotImplementedException();
  }


}
