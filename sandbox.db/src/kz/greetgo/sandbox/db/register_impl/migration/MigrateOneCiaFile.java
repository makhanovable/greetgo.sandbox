package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;
import kz.greetgo.sandbox.db.register_impl.migration.report.MigrationSimpleReport;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneCiaFile {
  public InputStream inputStream;
  public ErrorFile outputErrorFile;
  public MigrationSimpleReport migrationSimpleReport;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTableName;
  String tmpClientAddressTableName;
  String tmpClientPhoneTableName;

  private Logger logger = Logger.getLogger(MigrationController.class);

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws Exception {
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

    long init = System.currentTimeMillis();

    reader.parse(new InputSource(inputStream));

    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.appendParseInfo((post - init) / 1000f);

    connection.setAutoCommit(true);
  }

  /*void processValidationErrors() throws Exception {
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

  void migrateData() throws Exception {
    migrateData_checkForDuplicatesOfTmpClient();
    migrateData_checkForDuplicatesOfTmpClientPhoneTable();

    migrateData_finalOfCharmTable();

    migrateData_checkForExistingRecordsOfClientTable();

    migrateData_finalOfClientTable();
    migrateData_finalOfClientAddressTable();
    migrateData_finalOfClientPhoneTable();

    migrateData_close();
  }

  /**
   * Статус = 1, отсеивание дубликатов с приоритетом на последнюю запись в списке
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClient() throws Exception {
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
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClientPhoneTable() throws Exception {
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
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfCharmTable() throws Exception {
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
   * Статус = 2, если cia_id присутствует в постоянной таблице client (update)
   * Статус = 3, если отсутствует в постоянной таблице client (insert)
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_checkForExistingRecordsOfClientTable() throws Exception {
    exec("UPDATE client_to_replace " +
      "SET id = c.id, status = 2 " +
      "FROM client AS c " +
      "WHERE status = 1 AND c.migration_cia_id = cia_id"
    );

    exec("UPDATE client_to_replace " +
      "SET id = nextval('client_id_seq'), status = 3 " +
      "WHERE status = 1"
    );
  }

  /**
   * Заполнение постоянной client таблицы
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfClientTable() throws Exception {
    exec("UPDATE client " +
      "SET id = c_r.id," +
      "  surname = c_r.surname, " +
      "  name = c_r.name, " +
      "  patronymic = c_r.patronymic, " +
      "  gender = c_r.gender, " +
      "  birth_date = c_r.birth_date, " +
      "  charm = ch.id," +
      "  actual = 1 " +
      "FROM client_to_replace AS c_r " +
      "JOIN charm AS ch ON ch.name = c_r.charm_name " +
      "WHERE c_r.status = 2 AND migration_cia_id = c_r.cia_id"
    );

    exec("INSERT INTO client(id, surname, name, patronymic, gender, birth_date, charm, migration_cia_id) " +
      "SELECT c_r.id, c_r.surname, c_r.name, c_r.patronymic, c_r.gender, c_r.birth_date, ch.id, c_r.cia_id " +
      "FROM client_to_replace AS c_r " +
      "JOIN charm AS ch ON ch.name = c_r.charm_name " +
      "WHERE c_r.status = 3"
    );
  }

  /**
   * Заполнение постоянной client_address таблицы
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfClientAddressTable() throws Exception {
    exec("INSERT INTO client_addr(client, type, street, house, flat) " +
      "SELECT c_r.id, cad_r.type, cad_r.street, cad_r.house, cad_r.flat " +
      "FROM client_to_replace AS c_r " +
      "JOIN client_address_to_replace AS cad_r ON c_r.record_no = cad_r.client_record_no " +
      "WHERE c_r.status = 3 " +
      "ON CONFLICT(client, type) DO UPDATE " +
      "SET street = excluded.street, house = excluded.flat, flat = excluded.flat"
    );
  }

  /**
   * Заполнение постоянной client_phone таблицы
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_finalOfClientPhoneTable() throws Exception {
    exec("INSERT INTO client_phone(client, number, type) " +
      "SELECT c_r.id, ph_r.number, ph_r.type " +
      "FROM client_to_replace AS c_r " +
      "JOIN client_phone_to_replace AS ph_r ON c_r.record_no = ph_r.client_record_no " +
      "WHERE c_r.status = 3 AND ph_r.status = 1 " +
      "ON CONFLICT(client, number) DO UPDATE " +
      "SET actual = 1"
    );
  }

  /**
   * Статусы для пройденной миграции
   *
   * @throws Exception проброс для удобства
   */
  void migrateData_close() throws Exception {
    exec("UPDATE client_to_replace " +
      "SET status = 4 " +
      "WHERE status IN (2, 3)"
    );
  }

  void downloadErrors() {

  }

  private void exec(String sql) throws Exception {
    sql = sql.replaceAll("client_to_replace", tmpClientTableName);
    sql = sql.replaceAll("client_address_to_replace", tmpClientAddressTableName);
    sql = sql.replaceAll("client_phone_to_replace", tmpClientPhoneTableName);

    long init = System.currentTimeMillis();

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }

    long post = System.currentTimeMillis();
    if (migrationSimpleReport != null)
      migrationSimpleReport.append((post - init) / 1000f, sql);
  }
}
