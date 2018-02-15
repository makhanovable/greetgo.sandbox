package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;
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
  public ErrorFile outputErrorFile;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTableName;
  String tmpClientAddressTableName;
  String tmpClientPhoneTableName;

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    //processValidationErrors();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientTableName = "tmp_migration_client_" + additionalId;
    tmpClientAddressTableName = "tmp_migration_client_address_" + additionalId;
    tmpClientPhoneTableName = "tmp_migration_client_phone_" + additionalId;

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
      "  birth_date date, " +
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
      "  PRIMARY KEY(record_no)" +
      ")");
  }

  void uploadData() throws Exception {
    connection.setAutoCommit(false);

    XMLReader reader = XMLReaderFactory.createXMLReader();

    CiaUploader ciaUploader = new CiaUploader();
    ciaUploader.connection = connection;
    ciaUploader.maxBatchSize = maxBatchSize;
    ciaUploader.errorFileWriter = outputErrorFile;
    ciaUploader.clientTable = tmpClientTableName;
    ciaUploader.clientAddressTable = tmpClientAddressTableName;
    ciaUploader.clientPhoneTable = tmpClientPhoneTableName;
    reader.setContentHandler(ciaUploader);

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      reader.parse(new InputSource(fileInputStream));
    }

    connection.setAutoCommit(true);
  }

  /*void processValidationErrors() throws SQLException {
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
      "  (extract(YEAR FROM age(to_date(birth_date, 'YYYY-MM-DD'))) <= 3 OR " +
      "  extract(YEAR FROM age(to_date(birth_date, 'YYYY-MM-DD'))) >= 1000) "
    );
  }*/

  void migrateData() throws SQLException {
    migrateData_status1();
    migrateData_status2();
    migrateData_finalOfCharmTable();
    migrateData_status3();
    migrateData_finalOfClientTable();
    migrateData_finalOfClientAddressTable();
    migrateData_finalOfClientPhoneTable();
  }

  /**
   * Статус = 1, если не имеется ошибки
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_status1() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET status = 1 " +
      "WHERE error IS NULL AND status = 0");
  }

  /**
   * Статус = 2, отсеивание дубликатов с приоритетом на последнюю запись в списке
   *
   * @throws SQLException проброс для удобства
   */
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

  /**
   * Статус = 3, если заполнена колонка charmId, предварительно заполнив таблицу charm
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_status3() throws SQLException {
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
  void migrateData_finalOfCharmTable() throws SQLException {
    exec("INSERT INTO charm(id, name) " +
      "SELECT nextval('charm_id_seq') AS charm_id, charm_dictionary.name " +
      "FROM ( " +
      "  SELECT DISTINCT charm_name AS name " +
      "  FROM client_to_replace " +
      "  WHERE status = 2 " +
      "  EXCEPT SELECT name FROM charm " +
      ") AS charm_dictionary "
    );
  }

  /**
   * Заполнение основной client таблицы
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientTable() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET client_id = nextval('client_id_seq') " +
      "WHERE status = 3 ");
/*
    exec("UPDATE client " +
      "SET id = t.client_id, surname = t.surname, patronymic = t.patronymic, gender = t.gender, " +
      " birth_date = t.birth_date_typed, charm = t.charm_id " +
      "FROM client_to_replace AS t " +
      "WHERE migration_cia_id = t.cia_id");

    exec("INSERT INTO client(id, surname, name, patronymic, gender, birth_date, charm, migration_cia_id) " +
      "SELECT client_id, t.surname, t.name, t.patronymic, t.gender, birth_date_typed, t.charm_id, t.cia_id " +
      "FROM client_to_replace AS t " +
      "WHERE status = 3 AND migration_cia_id = null"
    );*/

    exec("INSERT INTO client(id, surname, name, patronymic, gender, birth_date, charm, migration_cia_id) " +
      "SELECT client_id, t.surname, t.name, t.patronymic, t.gender, birth_date, t.charm_id, t.cia_id " +
      "FROM client_to_replace AS t " +
      "WHERE status = 3 " +
      "ON CONFLICT(migration_cia_id) DO UPDATE " +
      "SET id = excluded.id, surname = excluded.surname, name = excluded.name, " +
      "  patronymic = excluded.patronymic, gender = excluded.gender, birth_date = excluded.birth_date, " +
      "  charm = excluded.charm");
  }

  /**
   * Заполнение основной client_address таблицы
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientAddressTable() throws SQLException {
    exec("INSERT INTO client_addr(client, type, street, house, flat) " +
      "SELECT cl.client_id, adr.type, adr.street, adr.house, adr.flat " +
      "FROM client_to_replace AS cl, client_address_to_replace AS adr " +
      "WHERE cl.status = 3 AND cl.record_no = adr.client_record_no"
    );
  }

  /**
   * Заполнение основной client_phone таблицы
   * Также имеется отдельный статус для телефонов, где 1 означает, что это уникальный номер
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientPhoneTable() throws SQLException {
    exec("UPDATE client_phone_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT " +
      "    record_no as rno," +
      "    row_number() OVER (PARTITION BY client_record_no, number ORDER BY record_no DESC) AS rnum " +
      "  FROM client_phone_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no");

    exec("INSERT INTO client_phone(client, number, type) " +
      "SELECT cl.client_id, ph.number, ph.type " +
      "FROM client_to_replace AS cl, client_phone_to_replace AS ph " +
      "WHERE cl.status = 3 AND cl.record_no = ph.client_record_no AND ph.status = 1"
    );
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
