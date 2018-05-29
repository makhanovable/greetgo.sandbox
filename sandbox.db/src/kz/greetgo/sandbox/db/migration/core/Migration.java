package kz.greetgo.sandbox.db.migration.core;

import org.xml.sax.SAXException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static kz.greetgo.sandbox.db.migration.util.TimeUtils.showTime;


public class Migration implements Closeable {

  private Connection connection = null;

  public Migration(Connection connection) {
    this.connection = connection;

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//    Date nowDate = new Date();
    tmpClientTable = "cia_migration_client_";
    tmpPhoneTable = "cia_migration_phone_";
    tmpAccountTable = "cia_migration_account_";
    tmpTransactionTable = "cia_migration_transaction_";
    info("TMP_CLIENT = " + tmpClientTable);
    info("TMP_PHONE = " + tmpPhoneTable);
    info("TMP_ACCOUNT = " + tmpAccountTable);
    info("TMP_TRANSACTION = " + tmpTransactionTable);
  }

  @Override
  public void close() {
    closeOperConnection();
  }

  private void closeOperConnection() {
    if (this.connection != null) {
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.connection = null;
    }
  }

  private void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  private String r(String sql) {
    sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
    sql = sql.replaceAll("TMP_PHONE", tmpPhoneTable);
    sql = sql.replaceAll("TMP_ACCOUNT", tmpAccountTable);
    sql = sql.replaceAll("TMP_TRANSACTION", tmpTransactionTable);
    return sql;
  }

  private void exec(String sql) throws SQLException {
    String executingSql = r(sql);

    long startedAt = System.nanoTime();
    try (Statement statement = connection.createStatement()) {
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

  private String tmpClientTable, tmpPhoneTable;
  private String tmpAccountTable, tmpTransactionTable;

  public int migrate() throws Exception {
    long startedAt = System.nanoTime();

    //language=PostgreSQL
    exec("create table TMP_CLIENT (\n" +
      "  client_id varchar(20),\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  number bigserial primary key,\n" +
      "  id varchar(100) not null,\n" +
      "  cia_id varchar(100) not null,\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300),\n" +
      "  charm varchar(300),\n" +
      "  gender varchar(300),\n" +
      "  birth_date date,\n" +
      "  rStreet varchar(100),\n" +
      "  rHouse varchar(100),\n" +
      "  rFlat varchar(100),\n" +
      "  fStreet varchar(100),\n" +
      "  fHouse varchar(100),\n" +
      "  fFlat varchar(100)\n" +
      ")");

    //language=PostgreSQL
    exec("create table TMP_PHONE (\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  cia_id varchar(100) not null,\n" +
      "  tmp_client_id varchar(100) not null,\n" +
      "  number varchar(100),\n" +
      "  phoneType varchar(20),\n" +
      "  client_id varchar(100)\n" +
      ")");

    //language=PostgreSQL
    exec("create table TMP_ACCOUNT (\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  number bigserial primary key,\n" +
      "  account_number varchar(100),\n" +
      "  registered_at timestamp,\n" +
      "  client_cia_id varchar(100),\n" +
      "  client_id varchar(100)\n" +
      ")");

    //language=PostgreSQL
    exec("create table TMP_TRANSACTION (\n" +
      "  status int not null default 0,\n" +
      "  error varchar(300),\n" +
      "  \n" +
      "  number bigserial primary key,\n" +
      "  money float,\n" +
      "  account_number varchar(100),\n" +
      "  account_id bigint,\n" +
      "  finished_at timestamp,\n" +
      "  transaction_type varchar(300),\n" +
      "  transaction_type_id bigint\n" +
      ")");

//    int portionSize = downloadFromCIA();

    {
      long now = System.nanoTime();
      info("Downloaded of portion " + portionSize + " from CIA finished for " + showTime(now, startedAt));
    }

//    portionSize = downloadFromFRS();

    {
      long now = System.nanoTime();
      info("Downloaded of portion " + portionSize + " from FRS finished for " + showTime(now, startedAt));
    }

    if (portionSize == 0) return 0;

//    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + portionSize + " finished for " + showTime(now, startedAt));
    }

    return portionSize;
  }

  public int downloadFromCIA() throws SQLException, IOException, SAXException {

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

    Insert client_insert = new Insert("TMP_CLIENT");
    client_insert.field(1, "cia_id", "?");
    client_insert.field(2, "surname", "?");
    client_insert.field(3, "name", "?");
    client_insert.field(4, "patronymic", "?");
    client_insert.field(5, "gender", "?");
    client_insert.field(6, "charm", "?");
    client_insert.field(7, "birth_date", "?");
    client_insert.field(8, "id", "?");
    client_insert.field(9, "rStreet", "?");
    client_insert.field(10, "rHouse", "?");
    client_insert.field(11, "rFlat", "?");
    client_insert.field(12, "fStreet", "?");
    client_insert.field(13, "fHouse", "?");
    client_insert.field(14, "fFlat", "?");

    Insert phone_insert = new Insert("TMP_PHONE");
    phone_insert.field(1, "cia_id", "?");
    phone_insert.field(2, "number", "?");
    phone_insert.field(3, "phoneType", "?");
    phone_insert.field(4, "tmp_client_id", "?");

      connection.setAutoCommit(false);
      try (PreparedStatement clientPS = connection.prepareStatement(r(client_insert.toString()))) {

        try (PreparedStatement phonePS = connection.prepareStatement(r(phone_insert.toString()))) {

          int recordsCount = 0;

          FromXMLParser fromXMLParser = new FromXMLParser();

          try {
            File inputFile = new File("build/out_files/from_cia_2018-05-24-095644-2-3000.xml");

            fromXMLParser.execute(connection, clientPS, phonePS, downloadMaxBatchSize);
            recordsCount =  fromXMLParser.parseRecordData(String.valueOf(inputFile));

          } catch (Exception e) {
            e.printStackTrace();
          }

          if (fromXMLParser.getClientBatchSize() > 0 || fromXMLParser.getPhoneBatchSize() > 0) {
            phonePS.executeBatch();
            clientPS.executeBatch();
            connection.commit();
          }

          return recordsCount;
        }

      } finally {
        connection.setAutoCommit(true);
        working.set(false);
        see.interrupt();
      }
  }
  public int downloadFromFRS() throws SQLException, IOException, SAXException {

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

    Insert account_insert = new Insert("TMP_ACCOUNT");
    account_insert.field(1, "account_number", "?");
    account_insert.field(2, "registered_at", "?");
    account_insert.field(3, "client_cia_id", "?");

    Insert transaction_insert = new Insert("TMP_TRANSACTION");
    transaction_insert.field(1, "money", "?");
    transaction_insert.field(2, "account_number", "?");
    transaction_insert.field(3, "finished_at", "?");
    transaction_insert.field(4, "transaction_type", "?");

    connection.setAutoCommit(false);
    try (PreparedStatement accountPS = connection.prepareStatement(r(account_insert.toString()))) {

      try (PreparedStatement transPS = connection.prepareStatement(r(transaction_insert.toString()))) {

        int recordsCount = 0;

        FromJSONParser fromJSONParser = new FromJSONParser();

        try {
          File inputFile = new File("build/out_files/from_frs_2018-05-24-095714-1-30005.json_row.txt");

          fromJSONParser.execute(connection, accountPS, transPS, uploadMaxBatchSize);
          recordsCount = fromJSONParser.parseRecordData(inputFile);

        } catch (Exception e) {
          e.printStackTrace();
        }

        if (fromJSONParser.getAccBatchSize() > 0 || fromJSONParser.getTransBatchSize() > 0) {
          accountPS.executeBatch();
          transPS.executeBatch();
          connection.commit();
        }

        return recordsCount;
      }

    } finally {
      connection.setAutoCommit(true);
      working.set(false);
      see.interrupt();
    }
  }

  public void migrateFromTmp() throws Exception {

    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'surname is not defined', status = 1\n" +
      "where error is null and surname is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'name is not defined', status = 1\n" +
      "where error is null and name is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'birth_date is not defined', status = 1\n" +
      "where error is null and birth_date is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'gender is not defined', status = 1\n" +
            "where error is null and gender is null");
    //language=PostgreSQL
    exec("update TMP_CLIENT set error = 'charm is not defined', status = 1\n" +
            "where error is null and charm is null");
    //language=PostgreSQL
    exec("update TMP_PHONE set error = 'number is not defined', status = 1\n" +
            "where error is null and number is null");
    //language=PostgreSQL
    exec("update TMP_TRANSACTION set error = 'transaction type is not defined', status = 1\n" +
            "where error is null and transaction_type is null");
    //language=PostgreSQL
    exec("update TMP_TRANSACTION set error = 'account number is not defined', status = 1\n" +
            "where error is null and account_number is null");
    //language=PostgreSQL
    exec("update TMP_ACCOUNT set error = 'client cia id is not defined', status = 1\n" +
            "where error is null and client_cia_id is null");
    //language=PostgreSQL
    exec("update TMP_ACCOUNT set error = 'account number is not defined', status = 1\n" +
            "where error is null and account_number is null");

    //language=PostgreSQL
    exec("update TMP_PHONE ph set status = 1" +
            " from TMP_CLIENT cl where cl.id = ph.tmp_client_id and cl.status = 1");

    //language=PostgreSQL
    exec("with num_ord as (\n" +
      "  select number, cia_id, row_number() over(partition by cia_id order by number desc) as ord \n" +
      "  from TMP_CLIENT\n" +
      ")\n" +
      "\n" +
      "update TMP_CLIENT set status = 2\n" +
      "where status = 0 and number in (select number from num_ord where ord > 1)");

    //language=PostgreSQL
    exec("update TMP_PHONE ph set status = 2" +
            " from TMP_CLIENT cl where cl.id = ph.tmp_client_id and cl.status = 2");

    //language=PostgreSQL
    exec("update TMP_CLIENT t set client_id = c.id\n" +
      "  from tmp_clients c\n" +
      "  where c.cia_id = t.cia_id\n");

    //language=PostgreSQL
    exec("update TMP_PHONE t set client_id = ph.client_id\n" +
            "  from tmp_phones ph\n" +
            "  where ph.cia_id = t.cia_id and ph.phonetype = t.phoneType\n");

    //language=PostgreSQL
    exec("update TMP_CLIENT set status = 3 where client_id is not null and status = 0");

    //language=PostgreSQL
    exec("update TMP_CLIENT set client_id = nextval('s_client') where status = 0");

    //language=PostgreSQL
    exec("update TMP_PHONE set client_id = cl.client_id " +
            "from TMP_CLIENT cl where cl.client_id is not null and tmp_client_id = cl.id" +
            " and cl.status = 0 or cl.status = 3");

    //language=PostgreSQL
    exec("insert into tmp_clients (id, cia_id, surname, name, patronymic, birth_date, charm, gender)\n" +
      "select client_id, cia_id, surname, name, patronymic, birth_date, charm, gender\n" +
      "from TMP_CLIENT where status = 0");

    //language=PostgreSQL
    exec("insert into tmp_phones (number, phoneType, client_id, cia_id)\n" +
            "select number, phoneType, client_id, cia_id\n" +
            "from TMP_PHONE where status = 0 " +
            "on conflict do nothing");

    //language=PostgreSQL
    exec("update tmp_clients c set surname = s.surname\n" +
      "                 , name = s.name\n" +
      "                 , patronymic = s.patronymic\n" +
      "                 , birth_date = s.birth_date\n" +
      "                 , charm = s.charm\n" +
      "                 , gender = s.gender\n" +
      "from TMP_CLIENT s\n" +
      "where c.id = s.client_id\n" +
      "and s.status = 3");

    //language=PostgreSQL
    exec("insert into tmp_adresses (street, house, flat, client_id)\n" +
            "select  fStreet, fHouse, fFlat, client_id\n" +
            "from TMP_CLIENT cl \n" +
            "where cl.status = 0 and cl.fStreet is not null and cl.fHouse is not null and cl.fFlat is not null");
    //language=PostgreSQL
    exec("update tmp_adresses set adresstype = 'FACT'\n" +
            "where adresstype = 'NONE'");
    //language=PostgreSQL
    exec("update tmp_adresses a set street = cl.fStreet,\n" +
            "                            house = cl.fHouse,\n" +
            "                            flat = cl.fFlat,\n" +
            "                            client_id = cl.client_id,\n" +
            "                            adresstype = 'FACT'\n" +
            "from TMP_CLIENT cl \n" +
            "where cl.status = 3 and cl.fStreet is not null and cl.fHouse is not null and cl.fFlat is not null");

    //language=PostgreSQL
    exec("insert into tmp_adresses (street, house, flat, client_id)\n" +
            "select  rStreet, rHouse, rFlat, client_id\n" +
            "from TMP_CLIENT cl \n" +
            "where cl.status = 0 and cl.rStreet is not null and cl.rHouse is not null and cl.rFlat is not null");
    //language=PostgreSQL
    exec("update tmp_adresses set adresstype = 'REG'\n" +
            "where adresstype = 'NONE'");
    //language=PostgreSQL
    exec("update tmp_adresses a set street = cl.rStreet,\n" +
            "                            house = cl.rHouse,\n" +
            "                            flat = cl.rFlat,\n" +
            "                            client_id = cl.client_id,\n" +
            "                            adresstype = 'REG'\n" +
            "from TMP_CLIENT cl \n" +
            "where cl.status = 3 and cl.rStreet is not null and cl.rHouse is not null and cl.rFlat is not null");

    //language=PostgreSQL
    exec("update TMP_ACCOUNT tmp set client_id = c.id\n" +
            "from tmp_clients c\n" +
            "where tmp.client_cia_id = c.cia_id and tmp.status = 0");

    //language=PostgreSQL
    exec("insert into tmp_accounts (number, registered_at, client_id)\n" +
            "select account_number, registered_at, client_id \n" +
            "from TMP_ACCOUNT tmp\n" +
            "where tmp.client_id is not null and tmp.status = 0");

    //language=PostgreSQL
    exec("insert into tmp_transaction_types (name)\n" +
            "select transaction_type \n" +
            "from TMP_TRANSACTION tmp\n" +
            "where tmp.transaction_type not in (select name from tmp_transaction_types) and tmp.status = 0");

    //language=PostgreSQL
    exec("update TMP_TRANSACTION tmp set transaction_type_id = t.id\n" +
            "from tmp_transaction_types t\n" +
            "where tmp.transaction_type = t.name and tmp.status = 0");

    //language=PostgreSQL
    exec("update TMP_TRANSACTION tmp set account_id = acc.id\n" +
            "from tmp_accounts acc\n" +
            "where tmp.account_number = acc.number and tmp.status = 0");

    //language=PostgreSQL
    exec("insert into tmp_transactions (money, finished_at, account_id, transaction_type_id)\n" +
            "select money, finished_at, account_id, transaction_type_id \n" +
            "from TMP_TRANSACTION tmp\n" +
            "where tmp.account_id is not null and tmp.transaction_type_id is not null and tmp.status = 0");

    //language=PostgreSQL
    exec("update tmp_clients set actual = 1 where id in (\n" +
      "  select client_id from TMP_CLIENT where status = 0\n" +
      ")");
  }
}
