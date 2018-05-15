package kz.greetgo.sandbox.controller.migration.core;

import kz.greetgo.sandbox.controller.migration.interfaces.ConnectionConfig;
import kz.greetgo.sandbox.controller.migration.util.TimeUtils;
import org.xml.sax.SAXException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static kz.greetgo.sandbox.controller.migration.util.TimeUtils.recordsPerSecond;
import static kz.greetgo.sandbox.controller.migration.util.TimeUtils.showTime;

public class Migration implements Closeable {

  private final ConnectionConfig operConfig;
  private final ConnectionConfig ciaConfig;
  private Connection operConnection = null, ciaConnection = null;

  public Migration(ConnectionConfig operConfig, ConnectionConfig ciaConfig) {
    this.operConfig = operConfig;
    this.ciaConfig = ciaConfig;
  }

  @Override
  public void close() {
    closeOperConnection();
    closeCiaConnection();
  }

  private void closeCiaConnection() {
    if (ciaConnection != null) {
      try {
        ciaConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      ciaConnection = null;
    }
  }

  private void closeOperConnection() {
    if (this.operConnection != null) {
      try {
        this.operConnection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.operConnection = null;
    }
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  private String r(String sql) {
    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    return sql;
  }

  private void exec(String sql) throws SQLException {
    String executingSql = r(sql);

    long startedAt = System.nanoTime();
    try (Statement statement = operConnection.createStatement()) {
      int updates = statement.executeUpdate(executingSql);
      info("Updated " + updates
        + " records for " + showTime(System.nanoTime(), startedAt)
        + ", EXECUTED SQL : " + executingSql);
    } catch (SQLException e) {
      info("ERROR EXECUTE SQL for " + showTime(System.nanoTime(), startedAt)
        + ", message: " + e.getMessage() + ", SQL : " + executingSql);
      throw e;
    }
  }

  public int portionSize = 1_000_000;
  public int downloadMaxBatchSize = 50_000;
  public int uploadMaxBatchSize = 50_000;
  public int showStatusPingMillis = 5000;

  private String tmpClientTable;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
    info("TMP_CLIENT = " + tmpClientTable);

    createOperConnection();

    //language=PostgreSQL
    exec("create table TMP_CLIENT (\n" +
      "  client_id int8,\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  number bigint not null primary key,\n" +
      "  cia_id varchar(100) not null,\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300),\n" +
      "  charm varchar(300),\n" +
      "  gender varchar(300),\n" +
      "  birth_date date\n" +
      ")");

    createCiaConnection();

    int portionSize = download();

    {
      long now = System.nanoTime();
      info("Downloaded of portion " + portionSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    if (portionSize == 0) return 0;

    closeCiaConnection();

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + portionSize + " finished for " + TimeUtils.showTime(now, startedAt));
    }

    return portionSize;
  }

  private void createOperConnection() throws Exception {
//    operConnection = ConnectionUtils.create(operConfig);
  }

  private void createCiaConnection() throws Exception {
//    ciaConnection = ConnectionUtils.create(ciaConfig);
  }

  private int download() throws SQLException, IOException, SAXException {

    final AtomicBoolean working = new AtomicBoolean(true);
    final AtomicBoolean showStatus = new AtomicBoolean(false);

    final Thread see = new Thread(() -> {

      while (working.get()) {

        try {
          Thread.sleep(showStatusPingMillis);
        } catch (InterruptedException e) {
          break;
        }

        showStatus.set(true);

      }

    });
    see.start();

    try (PreparedStatement ciaPS = ciaConnection.prepareStatement(
      "select * from transition_client where status='JUST_INSERTED' order by number limit ?")) {

      info("Prepared statement for : select * from transition_client");

      ciaPS.setInt(1, portionSize);

      Insert insert = new Insert("TMP_CLIENT");
      insert.field(1, "number", "?");
      insert.field(2, "cia_id", "?");
      insert.field(3, "surname", "?");
      insert.field(4, "name", "?");
      insert.field(5, "patronymic", "?");
      insert.field(6, "gender", "?");
      insert.field(7, "charm", "?");
      insert.field(8, "birth_date", "?");

      operConnection.setAutoCommit(false);
      try (PreparedStatement operPS = operConnection.prepareStatement(r(insert.toString()))) {

        try (ResultSet ciaRS = ciaPS.executeQuery()) {

          info("Got result set for : select * from transition_client");

          int batchSize = 0, recordsCount = 0;

          long startedAt = System.nanoTime();

          while (ciaRS.next()) {
            ClientRecord r = new ClientRecord();
            r.number = ciaRS.getLong("number");
            r.parseRecordData(ciaRS.getString("record_data"));

            operPS.setLong(1, r.number);
            operPS.setString(2, r.id);
            operPS.setString(3, r.surname);
            operPS.setString(4, r.name);
            operPS.setString(5, r.patronymic);
            operPS.setString(6, r.gender);
            operPS.setString(7, r.charm);
            operPS.setDate(8, r.birthDate);

            operPS.addBatch();
            batchSize++;
            recordsCount++;

            if (batchSize >= downloadMaxBatchSize) {
              operPS.executeBatch();
              operConnection.commit();
              batchSize = 0;
            }

            if (showStatus.get()) {
              showStatus.set(false);

              long now = System.nanoTime();
              info(" -- downloaded records " + recordsCount + " for " + showTime(now, startedAt)
                + " : " + recordsPerSecond(recordsCount, now - startedAt));
            }

          }

          if (batchSize > 0) {
            operPS.executeBatch();
            operConnection.commit();
          }

          {
            long now = System.nanoTime();
            info("TOTAL Downloaded records " + recordsCount + " for " + showTime(now, startedAt)
              + " : " + recordsPerSecond(recordsCount, now - startedAt));
          }

          return recordsCount;
        }
      } finally {
        operConnection.setAutoCommit(true);
        working.set(false);
        see.interrupt();
      }
    }
  }


  private void uploadAndDropErrors() throws Exception {
    info("uploadAndDropErrors goes : maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createCiaConnection();
    ciaConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r(
        "select number, error from TMP_CLIENT where error is not null"))) {

        info("Prepared statement for : select number, error from TMP_CLIENT where error is not null");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number, error from TMP_CLIENT where error is not null");

          try (PreparedStatement outPS = ciaConnection.prepareStatement(
            "update transition_client set status = 'ERROR', error = ? where number = ?")) {

            int batchSize = 0, recordsCount = 0;

            final AtomicBoolean showStatus = new AtomicBoolean(false);

            new Thread(() -> {

              while (working.get()) {

                try {
                  Thread.sleep(showStatusPingMillis);
                } catch (InterruptedException e) {
                  break;
                }

                showStatus.set(true);

              }

            }).start();

            long startedAt = System.nanoTime();

            while (inRS.next()) {

              outPS.setString(1, inRS.getString("error"));
              outPS.setLong(2, inRS.getLong("number"));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                ciaConnection.commit();
                batchSize = 0;
              }

              if (showStatus.get()) {
                showStatus.set(false);

                long now = System.nanoTime();
                info(" -- uploaded errors " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                  + " : " + recordsPerSecond(recordsCount, now - startedAt));
              }
            }

            if (batchSize > 0) {
              outPS.executeBatch();
              ciaConnection.commit();
            }

            {
              long now = System.nanoTime();
              info("TOTAL Uploaded errors " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                + " : " + recordsPerSecond(recordsCount, now - startedAt));
            }
          }
        }
      }

    } finally {
      closeCiaConnection();
      working.set(false);
    }

    //language=PostgreSQL
    exec("delete from TMP_CLIENT where error is not null");
  }

  private void uploadAllOk() throws Exception {

    info("uploadAllOk goes: maxBatchSize = " + uploadMaxBatchSize);

    final AtomicBoolean working = new AtomicBoolean(true);

    createCiaConnection();
    ciaConnection.setAutoCommit(false);
    try {

      try (PreparedStatement inPS = operConnection.prepareStatement(r("select number from TMP_CLIENT"))) {

        info("Prepared statement for : select number from TMP_CLIENT");

        try (ResultSet inRS = inPS.executeQuery()) {
          info("Query executed for : select number from TMP_CLIENT");

          try (PreparedStatement outPS = ciaConnection.prepareStatement(
            "update transition_client set status = 'OK' where number = ?")) {

            int batchSize = 0, recordsCount = 0;

            final AtomicBoolean showStatus = new AtomicBoolean(false);

            new Thread(() -> {

              while (true) {

                if (!working.get()) break;

                try {
                  Thread.sleep(showStatusPingMillis);
                } catch (InterruptedException e) {
                  break;
                }

                showStatus.set(true);
              }

            }).start();

            long startedAt = System.nanoTime();

            while (inRS.next()) {

              outPS.setLong(1, inRS.getLong("number"));
              outPS.addBatch();
              batchSize++;
              recordsCount++;

              if (batchSize >= uploadMaxBatchSize) {
                outPS.executeBatch();
                ciaConnection.commit();
                batchSize = 0;
              }

              if (showStatus.get()) {
                showStatus.set(false);

                long now = System.nanoTime();
                info(" -- uploaded ok records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                  + " : " + recordsPerSecond(recordsCount, now - startedAt));
              }
            }

            if (batchSize > 0) {
              outPS.executeBatch();
              ciaConnection.commit();
            }

            {
              long now = System.nanoTime();
              info("TOTAL Uploaded ok records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
                + " : " + recordsPerSecond(recordsCount, now - startedAt));
            }
          }
        }
      }

    } finally {
      closeCiaConnection();
      working.set(false);
    }

  }

  private void migrateFromTmp() throws Exception {

    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'surname is not defined'\n" +
      "where error is null and surname is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'name is not defined'\n" +
      "where error is null and name is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'birth_date is not defined'\n" +
      "where error is null and birth_date is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'gender is not defined'\n" +
            "where error is null and gender is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'charm is not defined'\n" +
            "where error is null and charm is null");

    uploadAndDropErrors();

    //language=PostgreSQL
    exec("with num_ord as (\n" +
      "  select number, cia_id, row_number() over(partition by cia_id order by number desc) as ord \n" +
      "  from TMP_CLIENT\n" +
      ")\n" +
      "\n" +
      "update TMP_CLIENT set status = 2\n" +
      "where status = 0 and number in (select number from num_ord where ord > 1)");

    //language=PostgreSQL
    exec("update TMP_CLIENT t set client_id = c.id\n" +
      "  from client c\n" +
      "  where c.cia_id = t.cia_id\n");

    //language=PostgreSQL
    exec("update TMP_CLIENT set status = 3 where client_id is not null and status = 0");

    //language=PostgreSQL
    exec("update TMP_CLIENT set client_id = nextval('s_client') where statu`s = 0");

    //language=PostgreSQL
    exec("insert into client (id, cia_id, surname, \"name\", patronymic, birth_date)\n" +
      "select client_id, cia_id, surname, \"name\", patronymic, birth_date\n" +
      "from TMP_CLIENT where status = 0");

    //language=PostgreSQL
    exec("update client c set surname = s.surname\n" +
      "                 , \"name\" = s.\"name\"\n" +
      "                 , patronymic = s.patronymic\n" +
      "                 , birth_date = s.birth_date\n" +
      "from TMP_CLIENT s\n" +
      "where c.id = s.client_id\n" +
      "and s.status = 3");

    //language=PostgreSQL
    exec("update client set actual = 1 where id in (\n" +
      "  select client_id from TMP_CLIENT where status = 0\n" +
      ")");

    uploadAllOk();
  }
}
