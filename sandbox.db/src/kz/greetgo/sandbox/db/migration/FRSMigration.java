package kz.greetgo.sandbox.db.migration;

import org.apache.log4j.Logger;

import java.sql.Connection;

public class FRSMigration extends MigrationAbstract {

    private String file;
    private int maxBatchSize;
    private Connection connection;
    private final Logger logger = Logger.getLogger(FRSMigration.class);

    public FRSMigration(Connection connection, String file, int maxBatchSize) {
        super(connection);
        this.file = file;
        this.maxBatchSize = maxBatchSize;
        this.connection = connection;

        logger.info("Starting FRS Migration of file: " + file);
    }

    @Override
    public void migrate() throws Exception {
        logger.info("FRSMigration.createTempTables()");
        createTempTables();

        logger.info("FRSMigration.prepareData()");
        prepareData();

        logger.info("FRSMigration.validateTableData()");
        validateTableData();

        logger.info("FRSMigration.migrateTransactionTypeTable()");
        migrateTransactionTypeTable();

        logger.info("FRSMigration.migrateAccountTable()");
        migrateAccountTable();

        logger.info("FRSMigration.migrateTransactionTable()");
        migrateTransactionTable();

        logger.info("FRSMigration.loadTopSqlQueriesList()");
        loadTopSqlQueriesList();
    }

    private void prepareData() throws Exception {
        long start = System.currentTimeMillis();
        logger.info("Starting parseAndInsertDataToTempTables file: " + file);

        connection.setAutoCommit(false);
        FRSParser frsParser = new FRSParser(connection, maxBatchSize);
        frsParser.parseAndInsertToTempTables(file);
        connection.setAutoCommit(true);

        long end = System.currentTimeMillis();
        logger.info("Time to parseAndInsertDataToTempTables: " + (end - start) + " ms");
    }

    private void createTempTables() throws Exception {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS tmp_acc CASCADE; " +
                "CREATE TABLE tmp_acc (" +
                " cia_client_id VARCHAR(50)," +
                " account_number VARCHAR(50)," +
                " registered_at TIMESTAMP)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS tmp_trans CASCADE; " +
                "CREATE TABLE tmp_trans (" +
                "status INTEGER DEFAULT 0," +
                " money DOUBLE PRECISION," +
                " finished_at TIMESTAMP," +
                " transaction_type VARCHAR(100)," +
                " account_number VARCHAR(50))");
    }

    // status 1 means transaction exists
    private void validateTableData() throws Exception {
        //language=PostgreSQL
        exec("UPDATE tmp_trans SET status = 1 " +
                "FROM client_account_transaction " +
                "JOIN client_account ON client_account.id = client_account_transaction.account " +
                "WHERE tmp_trans.status = 0 AND tmp_trans.account_number = client_account.number " +
                "AND tmp_trans.finished_at = client_account_transaction.finished_at " +
                "AND tmp_trans.money = client_account_transaction.money");
    }

    private void migrateTransactionTypeTable() throws Exception {
        //language=PostgreSQL
        exec("INSERT INTO transaction_type(name) " +
                "SELECT DISTINCT transaction_type FROM TMP_TRANS WHERE tmp_trans.status = 0 " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateAccountTable() throws Exception{
        //language=PostgreSQL
        exec("INSERT INTO charm(id, name) " +
                "VALUES (-1, 'FAKE') " +
                "ON CONFLICT (name) DO NOTHING ");

        //language=PostgreSQL
        exec("INSERT INTO client(surname, name, gender, birth_date, charm, cia_client_id, actual) " +
                "SELECT 'NULL', 'NULL', 'NULL', '2010-01-01', -1, tmp_acc.cia_client_id, FALSE " +
                "FROM tmp_acc " +
                "ON CONFLICT (cia_client_id) DO NOTHING");

        //language=PostgreSQL
        exec("CREATE INDEX indx_tmp_trans_acc_num ON tmp_trans (account_number)");
        //language=PostgreSQL
        exec("CREATE INDEX indx_tmp_acc_acc_num ON tmp_acc (account_number)");
        //language=PostgreSQL
        exec("CREATE INDEX indx_tmp_trans_money ON tmp_trans (money)");

        //language=PostgreSQL
        exec("INSERT INTO client_account(client, money, number, registered_at) " +
                "SELECT" +
                " c.id," +
                " (SELECT sum(money) FROM tmp_trans" +
                " WHERE tmp_trans.account_number = tmp_acc.account_number)," +
                " tmp_acc.account_number, tmp_acc.registered_at " +
                "FROM tmp_acc " +
                "JOIN client c ON c.cia_client_id = tmp_acc.cia_client_id " +
                "ON CONFLICT (number) DO NOTHING");
    }

    private void migrateTransactionTable() throws Exception {
        //language=PostgreSQL
        exec("INSERT INTO client_account_transaction(account, money, finished_at, type)" +
                "SELECT" +
                " ca.id," +
                " tmp_trans.money, tmp_trans.finished_at," +
                " transaction_type.id " +
                "FROM tmp_trans " +
                "JOIN client_account ca ON ca.number = tmp_trans.account_number " +
                "JOIN transaction_type ON transaction_type.name LIKE tmp_trans.transaction_type " +
                "WHERE tmp_trans.status = 0");
    }

}
