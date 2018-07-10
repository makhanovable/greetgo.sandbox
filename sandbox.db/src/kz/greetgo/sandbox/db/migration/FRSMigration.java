package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;

public class FRSMigration extends MigrationAbstract {

    private String file;
    private int maxBatchSize;
    private Connection connection;

    public FRSMigration(Connection connection, String file, int maxBatchSize) {
        super(connection);
        this.file = file;
        this.maxBatchSize = maxBatchSize;
        this.connection = connection;
    }

    @Override
    public void migrate() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Starting parsing and insert " + file);

        {
            createTempTables();
            prepareData();
        }

        System.out.println("Time to parsing and inserting " + (System.currentTimeMillis() - start) + " " + file);
        start = System.currentTimeMillis();
        System.out.println("Starting inner migration " + file);

        {
            validateTableData();
            migrateTransactionTypeTable();
            migrateAccountTable();
            migrateTransactionTable();
        }

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
                "   status INTEGER DEFAULT 0," +
                "   money DOUBLE PRECISION," +
                "   finished_at TIMESTAMP," +
                "   transaction_type VARCHAR(100)," +
                "   account_number VARCHAR(50))");
    }

    // status 1 means transaction exists
    private void validateTableData() {
        //language=PostgreSQL
        exec("UPDATE tmp_trans SET status = 1 " +
                " FROM client_account_transaction " +
                "JOIN client_account ON client_account.id = client_account_transaction.account " +
                "WHERE tmp_trans.status = 0 AND tmp_trans.account_number = client_account.number" +
                " AND tmp_trans.finished_at = client_account_transaction.finished_at " +
                "AND tmp_trans.money = client_account_transaction.money");
    }

    private void migrateTransactionTypeTable() {
        //language=PostgreSQL
        exec("INSERT INTO transaction_type(name)" +
                "   SELECT DISTINCT transaction_type FROM TMP_TRANS WHERE tmp_trans.status = 0 " +
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

        //language=PostgreSQL
        exec("INSERT INTO client_account(client, money, number, registered_at) " +
                "   SELECT " +
                "    c.id," +
                " (SELECT sum(money) FROM tmp_trans" +
                "  WHERE tmp_trans.account_number = tmp_acc.account_number), "+
                "   tmp_acc.account_number, tmp_acc.registered_at " +
                "   FROM tmp_acc " +
                "JOIN client c ON c.cia_client_id = tmp_acc.cia_client_id " +
                " ON CONFLICT (number) DO NOTHING");
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
                "WHERE tmp_trans.status = 0");
    }

}
