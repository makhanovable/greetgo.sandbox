package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.report.MigrateOneFileCommon;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneCiaFile extends MigrateOneFileCommon {

  String tmpClientTableName;
  String tmpClientAddressTableName;
  String tmpClientPhoneTableName;

  @Override
  public void migrate() throws Exception {
    super.migrate();
  }

  @Override
  protected void prepareTmpTables() throws SQLException {
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

  @Override
  protected void uploadData() throws Exception {
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
      migrationSimpleReport.addAction(Util.getSecondsFromMilliseconds(init, post), -1, "Парсинг CIA файла");

    connection.setAutoCommit(true);
  }

  @Override
  protected void prepareIndexes() throws SQLException {
    exec("CREATE INDEX IF NOT EXISTS idx_client_status " +
      "ON client_to_replace(status)");

    exec("CREATE INDEX IF NOT EXISTS idx_client_status " +
      "ON client_to_replace(cia_id)");

    exec("CREATE INDEX IF NOT EXISTS idx_client_status_and_error " +
      "ON client_to_replace(status, error)");

    exec("CREATE INDEX IF NOT EXISTS idx_client_status_and_cia_id " +
      "ON client_to_replace(status, cia_id)");

    exec("CREATE INDEX IF NOT EXISTS idx_client_phone_status " +
      "ON client_phone_to_replace(status)");
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

  @Override
  protected void migrateData() throws SQLException {
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
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClient() throws SQLException {
    execUpdate("UPDATE client_to_replace " +
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
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForDuplicatesOfTmpClientPhoneTable() throws SQLException {
    execUpdate("UPDATE client_phone_to_replace " +
      "SET status = 1 " +
      "FROM ( " +
      "  SELECT " +
      "    record_no as rno, " +
      "    row_number() OVER (PARTITION BY client_record_no, number ORDER BY record_no DESC) AS rnum " +
      "  FROM client_phone_to_replace " +
      "  WHERE status = 0 " +
      ") AS x " +
      "WHERE x.rnum = 1 AND x.rno = record_no"
    );
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
      "  WHERE status = 1 " +
      "  EXCEPT SELECT name FROM charm " +
      ") AS charm_dictionary "
    );
  }

  /**
   * Статус = 2, если cia_id присутствует в постоянной таблице client (update)
   * Статус = 3, если отсутствует в постоянной таблице client (insert)
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_checkForExistingRecordsOfClientTable() throws SQLException {
    execUpdate("UPDATE client_to_replace " +
      "SET id = c.id, status = 2 " +
      "FROM client AS c " +
      "WHERE status = 1 AND c.migration_cia_id = cia_id"
    );

//    execUpdateByParts("WITH x AS ( " +
//      "  SELECT record_no, c.id AS c_id " +
//      "  FROM client_to_replace " +
//      "  INNER JOIN client AS c ON c.migration_cia_id = cia_id " +
//      "  WHERE status = 1 " +
//      "  limit_to_replace " +
//      ") " +
//      "UPDATE client_to_replace AS c_r " +
//      "SET id = x.c_id, status = 2 " +
//      "FROM x " +
//      "WHERE x.record_no = c_r.record_no", 500000
//    );

    execUpdate("UPDATE client_to_replace " +
      "SET id = nextval('client_id_seq'), status = 3 " +
      "WHERE status = 1 "
    );

//    execUpdateByParts("UPDATE client_to_replace " +
//      "SET id = nextval('client_id_seq'), status = 3 " +
//      "WHERE record_no IN ( " +
//      "  SELECT record_no " +
//      "  FROM client_to_replace " +
//      "  WHERE status = 1 " +
//      "  limit_to_replace " +
//      ")", 500000
//    );
  }

  /**
   * Заполнение постоянной client таблицы
   *
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientTable() throws SQLException {
    execUpdate("UPDATE client " +
      "SET id = c_r.id, " +
      "  surname = c_r.surname, " +
      "  name = c_r.name, " +
      "  patronymic = c_r.patronymic, " +
      "  gender = c_r.gender, " +
      "  birth_date = c_r.birth_date, " +
      "  charm = ch.id, " +
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
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientAddressTable() throws SQLException {
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
   * @throws SQLException проброс для удобства
   */
  void migrateData_finalOfClientPhoneTable() throws SQLException  {
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
   * @throws SQLException проброс для удобства
   */
  void migrateData_close() throws SQLException {
    execUpdate("UPDATE client_to_replace " +
      "SET status = 4 " +
      "WHERE status IN (2, 3) "
    );

//    execUpdateByParts("UPDATE client_to_replace " +
//      "SET status = 4 " +
//      "WHERE record_no IN ( " +
//      "  SELECT record_no " +
//      "  FROM client_to_replace " +
//      "  WHERE status IN (2, 3) " +
//      "  limit_to_replace " +
//      ")", 500000
//    );
  }

  @Override
  protected void downloadErrors() {

  }

  private String replaceTableNames(String sqlQuery) {
    sqlQuery = sqlQuery.replaceAll("client_to_replace", tmpClientTableName);
    sqlQuery = sqlQuery.replaceAll("client_address_to_replace", tmpClientAddressTableName);
    sqlQuery = sqlQuery.replaceAll("client_phone_to_replace", tmpClientPhoneTableName);

    return sqlQuery;
  }

  @Override
  protected void exec(String sqlQuery) throws SQLException {
    super.exec(replaceTableNames(sqlQuery));
  }

  @Override
  protected void execUpdate(String sqlQuery) throws SQLException {
    super.execUpdate(replaceTableNames(sqlQuery));
  }
}
