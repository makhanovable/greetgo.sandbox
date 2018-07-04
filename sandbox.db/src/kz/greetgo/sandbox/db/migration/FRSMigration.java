package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FRSMigration {

    private String file;
    private Connection connection;

    FRSMigration(Connection connection, String file) {
        this.file = file;
        this.connection = connection;
    }

    public void migrate() throws Exception {
        createTempTables();
        prepareData();

        validateTableData();

        migrateTransactionTypeTable();
        migrateAccountTable();
        migrateTransactionTable();
        calculateAccountMoney();
    }

    private void prepareData() throws Exception {
        connection.setAutoCommit(false);
        FRSParser frsParser = new FRSParser(connection);
        frsParser.parseAndInsertToTempTables(file);
        connection.setAutoCommit(true);
    }

    private void createTempTables() {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ACC CASCADE;" +
                "CREATE TABLE TMP_ACC (" +
                "   info VARCHAR(100)," +
                "   client_id BIGINT DEFAULT NULL," +
                "   cia_client_id VARCHAR(100)," +
                "   account_number VARCHAR(100)," +
                "   registered_at TIMESTAMP," +
                "   num BIGSERIAL)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_TRANS CASCADE;" +
                "CREATE TABLE TMP_TRANS (" +
                "   info VARCHAR(100)," +
                "   money DOUBLE PRECISION," +
                "   finished_at VARCHAR(100)," +
                "   transaction_type VARCHAR(100)," +
                "   account_number VARCHAR(100)," +
                "   num BIGSERIAL)");
    }

    private void validateTableData() {
        //language=PostgreSQL
        exec("WITH num_ord AS (" +
                "    SELECT num," +
                "    row_number() OVER (PARTITION BY cia_client_id ORDER BY num DESC ) AS ord " +
                "    FROM TMP_ACC" +
                ")" +
                "UPDATE TMP_ACC SET info = 'NOT ACTUAL' " +
                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
        exec("WITH num_ord AS (" +
                "    SELECT num," +
                "    row_number() OVER (PARTITION BY (money, finished_at, account_number) ORDER BY num DESC ) AS ord " +
                "    FROM TMP_TRANS" +
                ")" +
                "UPDATE TMP_TRANS SET info = 'NOT ACTUAL' " +
                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
        exec("UPDATE tmp_acc SET info = 'CLIENT NOT EXISTS' " +
                "WHERE tmp_acc.info ISNULL AND tmp_acc.cia_client_id " +
                "NOT IN (SELECT client.cia_client_id FROM client)");

        //language=PostgreSQL
        exec("UPDATE tmp_acc SET info = 'ACCOUNT EXISTS' " +
                " FROM client_account " +
                "WHERE tmp_acc.info ISNULL AND tmp_acc.account_number = client_account.number");

        //language=PostgreSQL // TODO pzd
//        exec("UPDATE tmp_trans SET info = 'TRANSACTION EXISTS' " +
//                " FROM client_account_transaction JOIN client_account ON number = client_account_transaction.account " +
//                "WHERE tmp_trans.info ISNULL AND tmp_trans.account_number = client_account.number" +
//                " AND tmp_trans.finished_at = client_account_transaction.finished_at " +
//                "AND tmp_trans.money = client_account_transaction.money");
    }

    private void migrateTransactionTypeTable() {
        //language=PostgreSQL
        exec("INSERT INTO transaction_type(name)" +
                "   SELECT DISTINCT transaction_type FROM TMP_TRANS WHERE tmp_trans.info ISNULL " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateAccountTable() {
        //language=PostgreSQL // TODO set actual 0
        exec("INSERT INTO client(surname, name, gender, birth_date, charm, cia_client_id) " +
                "SELECT 'NULL', 'NULL', 'NULL', '2010-01-01', 1, tmp_acc.cia_client_id" +
                " FROM tmp_acc WHERE info = 'CLIENT NOT EXISTS'"); // TODO мне не нравится

        //language=PostgreSQL
        exec("INSERT INTO client_account(client, number, registered_at) " +
                "   SELECT " +
                "    (SELECT client.id" +
                "     FROM client" +
                "     WHERE cia_client_id = tmp_acc.cia_client_id)," +
                "   tmp_acc.account_number, tmp_acc.registered_at " +
                "   FROM tmp_acc WHERE info ISNULL OR info = 'CLIENT NOT EXISTS'");
    }

    private void migrateTransactionTable() {
        // TODO
    }

    private void calculateAccountMoney() {
        // TODO
    }

    private void exec(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
