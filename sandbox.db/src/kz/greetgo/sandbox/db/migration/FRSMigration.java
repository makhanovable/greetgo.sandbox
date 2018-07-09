package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class FRSMigration {

    private String file;
    private int maxBatchSize;
    private Connection connection;

    public FRSMigration(Connection connection, String file, int maxBatchSize) {
        this.file = file;
        this.maxBatchSize = maxBatchSize;
        this.connection = connection;
    }

    public void migrate() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Starting parsing and insert " + file);
        createTempTables();
        prepareData();

        System.out.println("Time to parsing and inserting " + (System.currentTimeMillis() - start) + " " + file);
        start = System.currentTimeMillis();
        System.out.println("Starting inner migration " + file);

        validateTableData();
        migrateTransactionTypeTable();
        migrateAccountTable();
        migrateTransactionTable();
        System.out.println("Time to inner migration " + (System.currentTimeMillis() - start) + " " + file);
    }

    private void prepareData() throws Exception {
        connection.setAutoCommit(false);
        FRSParser frsParser = new FRSParser(connection, maxBatchSize);
        frsParser.parseAndInsertToTempTables(file);
        connection.setAutoCommit(true);
    }

    private void createTempTables() {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ACC CASCADE;" +
                "CREATE TABLE TMP_ACC (" +
                "   cia_client_id VARCHAR(50)," +
                "   account_number VARCHAR(50)," +
                "   registered_at TIMESTAMP)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_TRANS CASCADE;" +
                "CREATE TABLE TMP_TRANS (" +
                "   info VARCHAR(50)," +
                "   money DOUBLE PRECISION," +
                "   finished_at TIMESTAMP," +
                "   transaction_type VARCHAR(100)," +
                "   account_number VARCHAR(50))");
    }

    private void validateTableData() {
        //language=PostgreSQL
//        exec("WITH num_ord AS (" +
//                "    SELECT num," +
//                "    row_number() OVER (PARTITION BY account_number ORDER BY num DESC ) AS ord " +
//                "    FROM TMP_ACC" +
//                ")" +
//                "UPDATE TMP_ACC SET info = 'NOT ACTUAL' " +
//                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
//        exec("WITH num_ord AS (" +
//                "    SELECT num," +
//                "    row_number() OVER (PARTITION BY (money, finished_at, account_number) ORDER BY num DESC ) AS ord " +
//                "    FROM TMP_TRANS" +
//                ")" +
//                "UPDATE TMP_TRANS SET info = 'NOT ACTUAL' " +
//                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

//        //language=PostgreSQL
//        exec("UPDATE tmp_acc SET client_id = client_account.client " +
//                "FROM client_account WHERE tmp_acc.info ISNULL " +
//                "AND tmp_acc.account_number = client_account.number");

        //language=PostgreSQL
        exec("UPDATE tmp_trans SET info = 'TRANSACTION EXISTS' " +
                " FROM client_account_transaction " +
                "JOIN client_account ON client_account.id = client_account_transaction.account " +
                "WHERE tmp_trans.info ISNULL AND tmp_trans.account_number = client_account.number" +
                " AND tmp_trans.finished_at = client_account_transaction.finished_at " +
                "AND tmp_trans.money = client_account_transaction.money");
    }

    private void migrateTransactionTypeTable() {
        //language=PostgreSQL
        exec("INSERT INTO transaction_type(name)" +
                "   SELECT DISTINCT transaction_type FROM TMP_TRANS WHERE tmp_trans.info ISNULL " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateAccountTable() {
        //language=PostgreSQL
        exec("INSERT INTO charm(id, name) " +
                "VALUES (-1, 'FAKE') " +
                "ON CONFLICT (name) DO NOTHING ");

        //language=PostgreSQL
        exec("INSERT INTO client(surname, name, gender, birth_date, charm, cia_client_id, actual) " +
                "SELECT 'NULL', 'NULL', 'NULL', '2010-01-01', -1, tmp_acc.cia_client_id, FALSE" +
                " FROM tmp_acc" +
                " ON CONFLICT (cia_client_id) DO NOTHING");

        //language=PostgreSQL // TODO use concurrently or not?
        exec("CREATE INDEX tmp_trans_acc_num ON tmp_trans (account_number);");
        //language=PostgreSQL
        exec("CREATE INDEX tmp_acc_acc_num ON tmp_acc (account_number);");
        //language=PostgreSQL
        exec("CREATE INDEX tmp_trans_money ON tmp_trans (money);");

        //language=PostgreSQL // TODO оптимизировать
        exec("INSERT INTO client_account(client, money, number, registered_at) " +
                "   SELECT " +
                "    c.id," +
                " (SELECT sum(money) FROM tmp_trans" +
                "  WHERE tmp_trans.account_number = tmp_acc.account_number), "+
                "   tmp_acc.account_number, tmp_acc.registered_at " +
                "   FROM tmp_acc " +
                "JOIN client c ON c.cia_client_id = tmp_acc.cia_client_id " +
                " ON CONFLICT (number) DO NOTHING");

//        //language=PostgreSQL // TODO оптимизировать
//        exec("INSERT INTO client_account(client, money, number, registered_at) " +
//                "   SELECT " +
//                "    c.id," +
//                " (SELECT sum(tr.money)), "+
//                "   tmp_acc.account_number, tmp_acc.registered_at " +
//                "   FROM tmp_acc " +
//                "JOIN client c ON c.cia_client_id = tmp_acc.cia_client_id " +
//                "JOIN tmp_trans tr ON tr.account_number = tmp_acc.account_number " +
//                " ON CONFLICT (number) DO NOTHING");
    }

    private void migrateTransactionTable() {
        //language=PostgreSQL
        exec("INSERT INTO client_account_transaction(account, money, finished_at, type)" +
                "SELECT " +
                "   ca.id," +
                "   tmp_trans.money, tmp_trans.finished_at," +
                "   transaction_type.id " +
                "FROM tmp_trans " +
                "JOIN client_account ca ON ca.number = tmp_trans.account_number " +
                "JOIN transaction_type ON transaction_type.name LIKE tmp_trans.transaction_type " +
                "WHERE tmp_trans.info ISNULL");
    }

//    private void calculateAccountsMoney(){
//        //language=PostgreSQL
//        exec("UPDATE client_account " +
//                "SET money = (SELECT sum(money) FROM client_account_transaction " +
//                "WHERE client_account.id = account)");
//
//    }

    private Map<String, Long> topSqlList = new HashMap<>();

    private void exec(String sql) {
        long start = System.currentTimeMillis();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        topSqlList.put(sql, end - start);
        System.out.println("Sql time: " + (end - start));
        System.out.println(sql);
    }


}
