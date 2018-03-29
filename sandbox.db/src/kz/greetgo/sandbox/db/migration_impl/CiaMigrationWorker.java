package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.util.RND;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kz.greetgo.sandbox.db.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public class CiaMigrationWorker extends AbstractMigrationWorker {

  public String tmpClientTable;
  public String tmpAddrTable;
  public String tmpPhoneTable;

  public CiaMigrationWorker(Connection connection) {
    super(connection);
  }

//  @Override
//  protected List<String> prepareInFiles() throws IOException, SftpException {
//    List<String> fileNamesToLoad = renameFiles(".xml.tar.bz2");
//    fileNamesToLoad.sort(String::compareTo);
//    return fileNamesToLoad;
//  }

  @Override
  protected String r(String sql) {
    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_ADDR", tmpAddrTable);
    sql = sql.replaceAll("TMP_PHONE", tmpPhoneTable);
    return sql;
  }

  protected void validateErrors() throws SQLException, IOException, SftpException {
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'cia_id is not defined' " +
      "WHERE error IS NULL AND (cia_id <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'surname is not defined, cia_id = ' || cia_id::text " +
      "WHERE error IS NULL AND (surname <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'name is not defined, cia_id = ' || cia_id::text\n" +
      "WHERE error IS NULL AND (name <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'birth_date is not defined, cia_id = ' || cia_id::text\n" +
      "WHERE error IS NULL AND birth_date IS NULL");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'charm is not defined, cia_id = ' || cia_id::text\n" +
      "WHERE error IS NULL AND (charm_name <> '') IS NOT TRUE");
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET error = 'age of the client must be between 10 and 200, cia_id = ' || cia_id::text\n" +
      "WHERE error IS NULL AND date_part('year', age(birth_date)) NOT BETWEEN 11 AND 199"); // equivalent to age <=10 or age >=200

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET status = 4 WHERE error IS NOT NULL");


    uploadErrors();
  }

  protected void uploadErrors() throws SQLException, IOException, SftpException {

//    OutputStream outError = new FileOutputStream(outErrorFile);

    try (PreparedStatement errorPs = connection.prepareStatement(r("SELECT cia_id, error FROM TMP_CLIENT WHERE error IS NOT NULL"))) {
      try (ResultSet errorRs = errorPs.executeQuery()) {
        while (errorRs.next()) {
          outError.write("Error: ".getBytes());
          outError.write(errorRs.getBytes("error"));
//          outError.write(". Record id: ".getBytes());
//          outError.write(errorRs.getBytes("cia_id"));
          outError.write("\n".getBytes());
        }
      }
    } /*finally {
      outError.close();
      inputFileWorker.upload(outErrorFile);
    }*/
  }

  protected void createTmpTables() throws SQLException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    String processId = "_" + RND.intStr(5);
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate) + processId;
    tmpAddrTable = "cia_migration_addr_" + sdf.format(nowDate) + processId;
    tmpPhoneTable = "cia_migration_phone_" + sdf.format(nowDate) + processId;
    info("TMP_CLIENT = " + tmpClientTable);
    info("TMP_ADDR = " + tmpAddrTable);
    info("TMP_PHONE = " + tmpPhoneTable);

    //language=PostgreSQL
    exec("CREATE TABLE TMP_CLIENT (\n" +
      "        cia_id VARCHAR(32),\n" +
      "        client_id VARCHAR(32),\n" +
      "        name VARCHAR(255),\n" +
      "        surname VARCHAR(255),\n" +
      "        patronymic VARCHAR(255),\n" +
      "        gender VARCHAR(12),\n" +
      "        birth_date DATE,\n" +
      "        charm_name VARCHAR(32),\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number INTEGER PRIMARY KEY\n" +
      "      )");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_ADDR (\n" +
      "        cia_id VARCHAR(32),\n" +
      "        client_num INTEGER,\n" +
      "        client_id VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        street VARCHAR(255),\n" +
      "        house VARCHAR(32),\n" +
      "        flat VARCHAR(32)\n" +
      "      )");

//    //language=PostgreSQL
//    exec("CREATE INDEX tmp_address_ind ON TMP_ADDR (status, error);");

    //language=PostgreSQL
    exec("CREATE TABLE TMP_PHONE (\n" +
      "        client_num INTEGER,\n" +
      "        client_id VARCHAR(32),\n" +
      "        phone_number VARCHAR(32),\n" +
      "        type VARCHAR(32),\n" +
      "        actual SMALLINT NOT NULL DEFAULT 0,\n" +
      "        status INT NOT NULL DEFAULT 0,\n" +
      "        error VARCHAR(255),\n" +
      "        number BIGSERIAL PRIMARY KEY\n" +
      "      )");

//    //language=PostgreSQL
//    exec("CREATE INDEX client_status_ind ON TMP_CLIENT(status)");
//
//    //language=PostgreSQL
//    exec("CREATE INDEX client_cia_id_ind ON TMP_CLIENT(cia_id)");
//
//    //language=PostgreSQL
//    exec("CREATE INDEX client_status_error_ind ON TMP_CLIENT(status, error)");
//
//    //language=PostgreSQL
//    exec("CREATE INDEX client_status_cia_id_ind ON TMP_CLIENT(status, cia_id)");
//
//    //language=PostgreSQL
//    exec("CREATE INDEX phone_status_ind ON TMP_PHONE (status);");
  }

  protected void migrateFromTmp() throws Exception {

    validateErrors();

    markDuplicateClientRecords();

    checkForClientExistence();

    insertCharms();

    upsertClients();

    upsertClientAddress();

    markDuplicatePhoneNumbers();

    upsertPhoneNumbers();
  }

  /**
   * Cтатус = 2, если дубликат
   */
  void markDuplicateClientRecords() throws SQLException {
    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, row_number() OVER(PARTITION BY cia_id ORDER BY number DESC) AS ord \n" +
      "  FROM TMP_CLIENT WHERE status = 0\n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_CLIENT tc SET status = 2 FROM num_ord\n" +
      "WHERE tc.number = num_ord.number AND num_ord.ord > 1");
  }

  /**
   * Статус = 3, если cia_id присутствует в постоянной таблице client (update)
   * Статус = 0, если отсутствует в постоянной таблице client (insert)
   */
  void checkForClientExistence() throws SQLException {
    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT tc SET client_id = c.id, status = 3\n" +
      "  FROM client c\n" +
      "  WHERE c.cia_id = tc.cia_id AND tc.status = 0");

    //language=PostgreSQL
    exec("UPDATE TMP_CLIENT SET client_id = nextval('s_client') WHERE status = 0");
  }

  void insertCharms() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO charm (id, name)\n" +
      "SELECT nextval('s_client'), charm_name\n" +
      "FROM TMP_CLIENT tcl WHERE tcl.status IN (0, 3) ON CONFLICT (name) DO NOTHING");
  }

  void upsertClients() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client (id, cia_id, surname, name, patronymic, gender, birth_date, charm)\n" +
      "SELECT client_id, tcl.cia_id, surname, tcl.name, patronymic, gender, birth_date, ch.id\n" +
      "FROM TMP_CLIENT tcl LEFT JOIN charm ch ON tcl.charm_name = ch.name WHERE tcl.status = 0");

    //language=PostgreSQL
    exec("UPDATE client c SET cia_id = tc.cia_id\n" +
      "                 , surname = tc.surname\n" +
      "                 , name = tc.\"name\"\n" +
      "                 , patronymic = tc.patronymic\n" +
      "                 , gender = tc.gender\n" +
      "                 , birth_date = tc.birth_date\n" +
      "                 , charm = ch.id\n" +
      "FROM TMP_CLIENT tc LEFT JOIN charm ch ON tc.charm_name = ch.name\n" +
      "WHERE c.id = tc.client_id\n" +
      "AND tc.status = 3");

    //language=PostgreSQL
    exec("UPDATE client SET actual = 1 WHERE id IN (\n" +
      "  SELECT client_id FROM TMP_CLIENT WHERE status = 0\n" +
      ")");
  }

  /**
   * Статус = 3, если cia_id присутствует в постоянной таблице client_addr (update)
   * Статус = 0, если отсутствует в постоянной таблице client_addr (insert)
   */
  void upsertClientAddress() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client_addr(client, type, street, house, flat) " +
      "SELECT tc.client_id, ta.type, ta.street, ta.house, ta.flat " +
      "FROM TMP_CLIENT AS tc " +
      "LEFT JOIN TMP_ADDR AS ta ON tc.number = ta.client_num " +
//      "WHERE tc.status = 0 " +
      "WHERE tc.status IN (0,3) " +
      "ON CONFLICT(client, type) DO UPDATE " +
      "SET street = EXCLUDED.street, house = EXCLUDED.flat, flat = EXCLUDED.flat"
    );
  }

  void markDuplicatePhoneNumbers() throws SQLException {
    //language=PostgreSQL
    exec("WITH num_ord AS (\n" +
      "  SELECT number, row_number() OVER(PARTITION BY client_num, phone_number ORDER BY number DESC) AS ord \n" +
      "  FROM TMP_PHONE WHERE status = 0\n" +
      ")\n" +
      "\n" +
      "UPDATE TMP_PHONE tp SET status = 2 FROM num_ord\n" +
      "WHERE num_ord.ord > 1 AND num_ord.number = tp.number");
  }

  void upsertPhoneNumbers() throws SQLException {
    //language=PostgreSQL
    exec("INSERT INTO client_phone(client, number, type) " +
      "SELECT tc.client_id, tp.phone_number, tp.type " +
      "FROM TMP_CLIENT tc " +
      "JOIN TMP_PHONE tp ON tc.number = tp.client_num " +
      "WHERE tc.status = 0 AND tp.status = 0 " +
      "ON CONFLICT(client, number) DO UPDATE " +
      "SET actual = 1"
    );
  }

  protected int parseDataAndSaveInTmpDb() throws Exception {

    int recordsCount = 0;
    long downloadingStartedAt = System.nanoTime();

    long startedAt = System.nanoTime();
    connection.setAutoCommit(false);
    try (
      CiaTableWorker ciaTableWorker = new CiaTableWorker(connection, maxBatchSize, tmpClientTable, tmpAddrTable, tmpPhoneTable)
    ) {
      ciaTableWorker.startedAt = downloadingStartedAt;
      CiaParser ciaParser = new CiaParser(inputStream, ciaTableWorker, recordsCount);
      ciaParser.outError = outError;
      {
        long now = System.nanoTime();
        info("FILE Extracted for " + showTime(now, startedAt) + " sec");
      }
      recordsCount = ciaParser.parseAndSave();

    } finally {
      connection.setAutoCommit(true);
    }

    {
      long now = System.nanoTime();
      info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
        + " : " + recordsPerSecond(recordsCount, now - startedAt));
    }

    return recordsCount;
  }
}
