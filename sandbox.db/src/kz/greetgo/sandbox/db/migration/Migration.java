package kz.greetgo.sandbox.db.migration;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class Migration {

    public Connection connection;
    private int batchSize = 0;
    public int downloadMaxBatchSize = 10_000;
    private PreparedStatement accPS;
    private PreparedStatement trPS;

    Migration(Connection connection) {
        this.connection = connection;
    }

    public void migrate() throws Exception {

        createTempTables();
        for (String extractedFile : DataUtil.downloadFilesAndExtract())
            parseAndInsertToTempTables(extractedFile);


        if (hasDataToMigrate()) {
            migrateFromTmp();
            finishMigration();
        }
    }

    private void parseAndInsertToTempTables(String extractedFile) throws Exception {
        if (extractedFile.endsWith("xml"))
            parseINSERT_XML(extractedFile);
        else
            parseINSERT_JSON(extractedFile);
    }

    private void parseINSERT_JSON(String filename) throws Exception {
        System.out.println("Starting parsing JSON and inserting to TMP_TABLES ......");
        long start = System.currentTimeMillis();

        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String line;
        ObjectMapper mapper = new ObjectMapper();

        Insert acc = new Insert("TMP_ACC");
        acc.field(1, "client_id", "?");
        acc.field(2, "type", "?");
        acc.field(3, "account_number", "?");
        acc.field(4, "registered_at", "?");

        Insert tr = new Insert("TMP_TRANS");
        tr.field(1, "money", "?");
        tr.field(2, "type", "?");
        tr.field(3, "account_number", "?");
        tr.field(4, "transaction_type", "?");
        tr.field(5, "finished_at", "?");

        int rowsAcc = 0, rowsTrans = 0;
        connection.setAutoCommit(false);

        accPS = connection.prepareStatement(acc.toString());
        trPS = connection.prepareStatement(tr.toString());

        while ((line = bf.readLine()) != null) {
            if (line.contains("new_account")) {
                Account account = mapper.readValue(line, Account.class);
                insertAccount(account);
                rowsAcc++;
            } else {
                Transaction transaction = mapper.readValue(line, Transaction.class);
                insertTransaction(transaction);
                rowsTrans++;
            }
        }

        if (batchSize > 0) {
            accPS.executeBatch();
            trPS.executeBatch();
            connection.commit();
        }

        accPS.close();
        trPS.close();
        connection.setAutoCommit(true);
        long end = System.currentTimeMillis();
        System.out.println("ending parsing JSON and Inserting to TMP_TABLES....");
        System.out.println("TIME TO PARSE and INSERT = " + (end - start));
        System.out.println("INSERTED = " + rowsAcc + " ACCOUNT AND " + rowsTrans + " TRANSACTIONS");
        System.out.println("TOTAL rows = " + (rowsAcc + rowsTrans));
    }

    private void insertAccount(Account a) throws Exception {
        accPS.setString(1, a.client_id);
        accPS.setString(2, a.type);
        accPS.setString(3, a.account_number);
        accPS.setString(4, a.registered_at);
        accPS.addBatch();
        execute();
    }

    private void insertTransaction(Transaction t) throws Exception {
        trPS.setString(1, t.money);
        trPS.setString(2, t.type);
        trPS.setString(3, t.account_number);
        trPS.setString(4, t.transaction_type);
        trPS.setString(5, t.finished_at);
        trPS.addBatch();
        execute();
    }

    private void execute() throws Exception {
        batchSize++;
        if (batchSize >= downloadMaxBatchSize) {
            System.out.println("COMMIT " + batchSize);
            accPS.executeBatch();
            trPS.executeBatch();
            connection.commit();
            batchSize = 0;
        }
    }


    private void parseINSERT_XML(String filename) {
    }

    private boolean hasDataToMigrate() {
        return false;
    }

    private void createTempTables() {

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_CLIENT CASCADE;" +
                "CREATE TABLE TMP_CLIENT (" +
                "   error VARCHAR(100)," +
                "   id VARCHAR(100)," +
                "   name VARCHAR(100)," +
                "   surname VARCHAR(100)," +
                "   patronymic VARCHAR(100)," +
                "   birth DATE," +
                "   charm VARCHAR(100)," +
                "   gender VARCHAR(100)" +
                ")");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ADDRESS CASCADE;" +
                "CREATE TABLE TMP_ADDRESS (" +
                "   error VARCHAR(100)," +
                "   client VARCHAR(100)," +
                "   type VARCHAR(50)," +
                "   street VARCHAR(100)," +
                "   house VARCHAR(50)," +
                "   flat VARCHAR(50)" +
                ")");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_PHONE CASCADE;" +
                "CREATE TABLE TMP_PHONE (" +
                "   error VARCHAR(100)," +
                "   client VARCHAR(100)," +
                "   type VARCHAR(50)," +
                "   number VARCHAR(100)" +
                ")");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_TRANS CASCADE;" +
                "CREATE TABLE TMP_TRANS (" +
                "   error VARCHAR(100)," +
                "   type VARCHAR(50)," +
                "   money VARCHAR(100)," +
                "   finished_at VARCHAR(100)," +
                "   transaction_type VARCHAR(100)," +
                "   account_number VARCHAR(100)" +
                ")");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ACC CASCADE;" +
                "CREATE TABLE TMP_ACC (" +
                "   error VARCHAR(100)," +
                "   type VARCHAR(50)," +
                "   client_id VARCHAR(100)," +
                "   account_number VARCHAR(100)," +
                "   registered_at VARCHAR(100)" +
                ")");
    }

    private void migrateFromTmp() {
        System.out.println("STARTING MIGRATION ...");
        long start = System.currentTimeMillis();

        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET error = 'surname is not defined'" +
                "   WHERE error ISNULL AND surname ISNULL");

        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET error = 'name is not defined'" +
                "   WHERE error ISNULL AND name ISNULL");

        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET error = 'birth_date is not defined'" +
                "   WHERE error ISNULL AND birth ISNULL");


        //
        // МИГРАЦИЯ
        //

        //language=PostgreSQL
        exec("INSERT INTO charm(name)" +
                "   SELECT DISTINCT charm FROM tmp_cia " +
                "ON CONFLICT (name) DO NOTHING");

        // TODO execute some SQL

        long end = System.currentTimeMillis();
        System.out.println("TIME TO MIGRATION = " + (end - start));
    }

    private void finishMigration() {
        // TODO SET status true
    }

    private void exec(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
