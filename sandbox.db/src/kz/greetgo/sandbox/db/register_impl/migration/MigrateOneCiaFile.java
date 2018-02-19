package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
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
      "  cia_id varchar(64), " +
      "  id bigint, " +
      "  surname varchar(256), " +
      "  name varchar(256), " +
      "  patronymic varchar(128), " +
      "  gender varchar(16), " +
      "  charm_name varchar(128), " +
      "  birth_date date, " +
      "  status int NOT NULL DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(record_no)" +
      ")");

    exec("CREATE TABLE client_address_to_replace (" +
      "  record_no bigint NOT NULL, " +
      "  client_record_no bigint NOT NULL, " +
      "  type varchar(64), " +
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
    migrateData_checkForDuplicatesOfClient();
    migrateData_checkForDuplicatesOfClientPhoneTable();
    migrateData_finalOfCharmTable();
    migrateData_finalOfClientTable();
    migrateData_finalOfClientAddressTable();
    migrateData_finalOfClientPhoneTable();
  }

  /**
   * Статус = 1, отсеивание дубликатов с приоритетом на последнюю запись в списке
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfClient() throws SQLException {
    exec("UPDATE client_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT record_no AS rno, row_number() OVER ( PARTITION BY cia_id ORDER BY record_no DESC ) AS rnum " +
      "  FROM client_to_replace " +
      "  WHERE status = 0 AND error IS NULL " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
  }

  /**
   * Отдельный статус для телефонов, где 1 означает, что это уникальный номер
   *
   * @throws SQLException
   */
  void migrateData_checkForDuplicatesOfClientPhoneTable() throws SQLException {
    exec("UPDATE client_phone_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT " +
      "    record_no as rno, " +
      "    row_number() OVER (PARTITION BY client_record_no, number ORDER BY record_no DESC) AS rnum " +
      "  FROM client_phone_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no");
  }

  /**
   * Заполнение таблицы charm
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfCharmTable() throws SQLException {
    exec("INSERT INTO charm(id, name) " +
      "SELECT nextval('charm_id_seq') AS charm_id, charm_dictionary.name " +
      "FROM ( " +
      "  SELECT DISTINCT charm_name AS name " +
      "  FROM client_to_replace " +
      "  WHERE status = 1 AND error IS NULL " +
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
      "SELECT c_r.id, c_r.surname, c_r.name, c_r.patronymic, c_r.gender, c_r.birth_date, ch.id, c_r.cia_id " +
      "FROM client_to_replace AS c_r " +
      "JOIN charm AS ch ON c_r.charm_name = ch.name " +
      "WHERE c_r.status = 1 " +
      "ON CONFLICT(migration_cia_id) DO UPDATE " +
      "SET id = excluded.id, surname = excluded.surname, name = excluded.name, patronymic = excluded.patronymic, " +
      "  gender = excluded.gender, birth_date = excluded.birth_date, charm = excluded.charm, actual = 1 "
    );

    exec("UPDATE client_to_replace " +
      "SET status = 2 " +
      "WHERE status = 1"
    );
  }

  /**
   * Заполнение основной client_address таблицы
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientAddressTable() throws SQLException {
    exec("INSERT INTO client_addr(client, type, street, house, flat) " +
      "SELECT c_r.id, cad_r.type, cad_r.street, cad_r.house, cad_r.flat " +
      "FROM client_to_replace AS c_r " +
      "JOIN client_address_to_replace AS cad_r ON c_r.record_no = cad_r.client_record_no " +
      "WHERE c_r.status = 2 " +
      "ON CONFLICT(client, type) DO UPDATE " +
      "SET street = excluded.street, house = excluded.flat, flat = excluded.flat"
    );
  }

  /**
   * Заполнение основной client_phone таблицы
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientPhoneTable() throws SQLException {
    exec("INSERT INTO client_phone(client, number, type) " +
      "SELECT c_r.id, ph_r.number, ph_r.type " +
      "FROM client_to_replace AS c_r " +
      "JOIN client_phone_to_replace AS ph_r ON c_r.record_no = ph_r.client_record_no " +
      "WHERE c_r.status = 2 AND ph_r.status = 1 " +
      "ON CONFLICT(client, number) DO UPDATE " +
      "SET actual = 1"
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
