package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneCiaFile {
  public File inputFile;
  public File outputErrorFile;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTableName;
  String tmpClientAddressTableName;
  String tmpClientPhoneTableName;
  String tmpCharmTableName;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    processErrors();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientTableName = "tmp_migration_client_" + additionalId;
    tmpClientAddressTableName = "tmp_migration_address_" + additionalId;
    tmpClientPhoneTableName = "tmp_migration_phone_" + additionalId;
    tmpCharmTableName = "tmp_migration_charm_" + additionalId;

    exec("CREATE TABLE client_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  client_id bigint UNIQUE, " +
      "  cia_id varchar(64), " +
      "  surname varchar(256), " +
      "  name varchar(256), " +
      "  patronymic varchar(128), " +
      "  gender varchar(16), " +
      "  charm_name varchar(128), " +
      "  charm_id int, " +
      "  birth_date varchar(16), " +
      "  birth_date_typed Date, " +
      "  status int NOT NULL DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_address_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  client_record_no bigint NOT NULL, " +
      "  type varchar(64) NOT NULL, " +
      "  street varchar(128), " +
      "  house varchar(128), " +
      "  flat varchar(128), " +
      "  PRIMARY KEY(record_no, type)" +
      ")");

    exec("CREATE TABLE client_phone_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  client_record_no bigint NOT NULL, " +
      "  number varchar(64) NOT NULL, " +
      "  type varchar(128), " +
      "  status int DEFAULT 0, " +
      "  PRIMARY KEY(record_no, number)" +
      ")");
  }

  void uploadData() throws Exception {
    connection.setAutoCommit(false);

    XMLReader reader = XMLReaderFactory.createXMLReader();

    CiaUploader ciaUploader = new CiaUploader();
    ciaUploader.connection = connection;
    ciaUploader.maxBatchSize = maxBatchSize;
    ciaUploader.clientTable = tmpClientTableName;
    ciaUploader.clientAddressTable = tmpClientAddressTableName;
    ciaUploader.clientPhoneTable = tmpClientPhoneTableName;
    reader.setContentHandler(ciaUploader);

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      reader.parse(new InputSource(fileInputStream));
    }

    connection.setAutoCommit(true);
  }

  void processErrors() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET error = 'Пустое значение surname у ciaId = '||cia_id " +
      "WHERE surname IS NULL OR length(trim(surname)) = 0 "
    );

    exec("UPDATE client_to_replace " +
      "SET error = 'Пустое значение name у ciaId = '||cia_id " +
      "WHERE error IS NULL AND (name IS NULL OR length(trim(name)) = 0) "
    );

    exec("UPDATE client_to_replace " +
      "SET error = 'Пустое значение birth у ciaId = '||cia_id " +
      "WHERE error IS NULL AND (birth_date IS NULL OR length(trim(birth_date)) = 0) "
    );

    exec("UPDATE client_to_replace " +
      "SET error = " +
      "  'Неправильный формат даты у ciaId = '||cia_id||'. Принятый формат ГОСДЕПа YYYY-MM-DD' " +
      "WHERE error IS NULL AND " +
      "  is_date_custom(birth_date) = false "
    );

    exec("UPDATE client_to_replace " +
      "SET error = " +
      "  'Значение birth выходит за рамки у ciaId = '||cia_id||'. Возраст должен быть между 3 и 1000 годами' " +
      "WHERE error IS NULL AND " +
      "  (extract(YEAR FROM age(to_date(birth_date, \'" + "YYYY-MM-DD" + "\'))) <= 3 OR " +
      "  extract(YEAR FROM age(to_date(birth_date, \'" + "YYYY-MM-DD" + "\'))) >= 1000) "
    );
  }

  void migrateData() throws SQLException {
    migrateData_status1();
    migrateData_status2();
    migrateData_status3();
    migrateData_status4_finalOfClientTable();


  }

  // Статус = 1, если не имеется ошибки
  void migrateData_status1() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET status = 1, birth_date_typed = to_date(birth_date, 'YYYY-MM-DD') " +
      "WHERE error IS NULL AND status = 0");
  }

  // Статус = 2, отсеивание дубликатов с приоритетом на последнюю запись в списке
  void migrateData_status2() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET status = 2 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY cia_id ORDER BY record_no DESC ) AS rnum " +
      "  FROM client_to_replace " +
      "  WHERE status = 1 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }

  // Статус = 3, если заполнена колонка charmId, предварительно заполнив таблицу charm
  void migrateData_status3() throws SQLException {
    migrateData_status3_fillCharmTable();

    exec("UPDATE client_to_replace AS t " +
      "SET charm_id = ch.id, status = 3 " +
      "FROM charm AS ch " +
      "WHERE ch.name = t.charm_name AND t.status = 2"
    );
  }

  /**
   * Заполнение таблицы charm до того, как присваивать статус 3
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_status3_fillCharmTable() throws SQLException {
    exec("INSERT INTO charm(id, name) " +
      "SELECT nextval('charm_id_seq') AS charm_id, charm_dictionary.name " +
      "FROM ( " +
      "  SELECT DISTINCT charm_name AS name " +
      "  FROM client_to_replace " +
      "  WHERE status = 2 " +
      "  EXCEPT SELECT name FROM charm " +
      ") AS charm_dictionary");
  }

  // Статус = 4, после перекидывания записей с временной таблицы tmp_client в основную client
  void migrateData_status4_finalOfClientTable() throws SQLException {
    exec("INSERT INTO client(id, surname, name, patronymic, gender, birth_date, charm, migration_id) " +
      "SELECT " +
      "  nextval('client_id_seq') AS client_id, " +
      "  t.surname, t.name, t.patronymic, t.gender, birth_date_typed, t.charm_id, t.record_no " +
      "FROM client_to_replace AS t " +
      "WHERE status = 3"
    );

    // TODO: возможное место ускорения
    exec("UPDATE client_to_replace " +
      "SET status = 4 " +
      "WHERE status = 3");

    exec("UPDATE client_to_replace AS x " +
      "SET client_id = cl.id " +
      "FROM client AS cl " +
      "WHERE x.status = 4 and cl.migration_id = x.record_no"
    );
  }

  // Статус = 5, после заполнения основной таблицы адресов
  void migrateData_status5_finalOfClientAddressTable() throws SQLException {
    exec("INSERT INTO client_addr(client, type, street, house, flat) " +
      "SELECT cl.client_id, adr.type, adr.street, adr.house, adr.flat " +
      "FROM client_to_replace AS cl, client_address_to_replace AS adr " +
      "WHERE cl.status = 4 AND cl.record_no = adr.client_record_no"
    );

    exec("UPDATE client_to_replace " +
      "SET status = 5 " +
      "WHERE status = 4");
  }

  // Статус = 6, после заполнения основной таблицы телефонов
  // Также имеется отдельный статус для телефонов, где 1 означает, что это уникальный номер
  void migrateData_status6_finalOfClientPhoneTable() throws SQLException {
    exec("UPDATE client_phone_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT " +
      "    client_record_no as cl_rno," +
      "    row_number() OVER (PARTITION BY client_record_no, number ORDER BY client_record_no DESC) AS rnum " +
      "  FROM client_phone_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.cl_rno = record_no");

    exec("INSERT INTO client_phone(client, number, type) " +
      "SELECT cl.client_id, ph.number, ph.type " +
      "FROM client_to_replace AS cl, client_phone_to_replace AS ph " +
      "WHERE cl.status = 5 AND cl.record_no = ph.client_record_no AND ph.status = 1"
    );

    exec("UPDATE client_to_replace " +
      "SET status = 6 " +
      "WHERE status = 5");
  }

  void downloadErrors() {

  }

  private void exec(String sql) throws SQLException {
    sql = sql.replaceAll("client_to_replace", tmpClientTableName);
    sql = sql.replaceAll("client_address_to_replace", tmpClientAddressTableName);
    sql = sql.replaceAll("client_phone_to_replace", tmpClientPhoneTableName);

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }
}
