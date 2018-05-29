package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration.core.FromJSONParser;
import kz.greetgo.sandbox.db.migration.core.FromXMLParser;
import kz.greetgo.sandbox.db.migration.core.Migration;
import kz.greetgo.sandbox.db.migration.model.AccountJSONRecord;
import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;
import kz.greetgo.sandbox.db.migration.model.TransactionJSONRecord;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationTest extends ParentTestNg{
    public BeanGetter<MigrationTestDao> migrationTestDao;

    Connection connection;
    Migration migration;


    @BeforeTest
    private void createConnection() throws Exception{
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/s_sandbox",
                "s_sandbox",
                "password"
        );

        migration = new Migration(connection);
    }

    @Test
    public void TestTableCreation() throws Exception {
        this.deleteTables();

        //
        //
            migration.migrate();
        //
        //

        List<String> tableNames = migrationTestDao.get().getCiaTableNames();
        assertThat(tableNames).hasSize(4);

        int cnt = 0;
        for(String table : tableNames) {
            if (table.contains("cia_migration_client")) cnt++;
            if (table.contains("cia_migration_phone")) cnt++;
            if (table.contains("cia_migration_account")) cnt++;
            if (table.contains("cia_migration_transaction")) cnt++;
        }
        assertThat(cnt).isEqualTo(4);
    }

    @Test
    public void TestDownloadFromCIA() throws Exception {

        this.clearTables();

        int expectedRecordsCount = 0;
        List<ClientXMLRecord> clientXMLRecords = null;

        try {
            File inputFile = new File("build/out_files/from_cia_2018-05-24-095644-2-3000.xml");

            FromXMLParser fromXMLParser = new FromXMLParser();
            fromXMLParser.execute(connection, null, null, 0);
            expectedRecordsCount = fromXMLParser.parseRecordData(String.valueOf(inputFile));
            clientXMLRecords = fromXMLParser.getClientXMLRecords();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        //
            int recordsCount = migration.downloadFromCIA();
        //
        //

        assertThat(recordsCount).isEqualTo(expectedRecordsCount);
        for(ClientXMLRecord clientXMLRecord : clientXMLRecords) {
            int clientID = migrationTestDao.get().getCiaClient(clientXMLRecord);

            int numberCount = 0;
            for(String number : clientXMLRecord.mobilePhones) {
                if (migrationTestDao.get().getCiaPhone(clientXMLRecord.id, number, "MOBILE") != null) {
                    numberCount++;
                }
            }
            for(String number : clientXMLRecord.homePhones) {
                if (migrationTestDao.get().getCiaPhone(clientXMLRecord.id, number, "HOME") != null) {
                    numberCount++;
                }
            }
            for(String number : clientXMLRecord.workPhones) {
                if (migrationTestDao.get().getCiaPhone(clientXMLRecord.id, number, "WORK") != null) {
                    numberCount++;
                }
            }

            int expectedNumberCount = clientXMLRecord.mobilePhones.size() + clientXMLRecord.workPhones.size() + clientXMLRecord.homePhones.size();

            assertThat(clientID).isNotNull();
            assertThat(numberCount).isEqualTo(expectedNumberCount);
        }
    }

    @Test
    public void TestDownloadFromFRS() throws Exception{

        this.clearTables();

        int expectedRecordsCount = 0;
        List<TransactionJSONRecord> transactionJSONRecords = null;
        List<AccountJSONRecord> accountJSONRecords = null;
        try {
            File inputFile = new File("build/out_files/from_frs_2018-05-24-095714-1-30005.json_row.txt");

            FromJSONParser fromJSONParser = new FromJSONParser();
            fromJSONParser.execute(connection, null, null, 0);
            expectedRecordsCount = fromJSONParser.parseRecordData(inputFile);

            transactionJSONRecords = fromJSONParser.getTransactionJSONRecords();
            accountJSONRecords = fromJSONParser.getAccountJSONRecords();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        //
            int recordsCount = migration.downloadFromFRS();
        //
        //

        assertThat(recordsCount).isEqualTo(expectedRecordsCount);
        for(TransactionJSONRecord transactionJSONRecord : transactionJSONRecords) {
            Long transaction = migrationTestDao.get().getCiaTransaction(transactionJSONRecord);

            assertThat(transaction).isNotZero();
        }
        for(AccountJSONRecord accountJSONRecord : accountJSONRecords) {
            Long account = migrationTestDao.get().getCiaAccount(accountJSONRecord);

            assertThat(account).isNotZero();
        }

    }

    @Test
    public void TestMigrateFromTmp() throws Exception {

        List<TransactionJSONRecord> transactionJSONRecords = null;
        List<AccountJSONRecord> accountJSONRecords = null;
        try {
            File inputFile = new File("build/out_files/from_frs_2018-05-24-095714-1-30005.json_row.txt");

            FromJSONParser fromJSONParser = new FromJSONParser();
            fromJSONParser.execute(connection, null, null, 0);
            fromJSONParser.parseRecordData(inputFile);

            transactionJSONRecords = fromJSONParser.getTransactionJSONRecords();
            accountJSONRecords = fromJSONParser.getAccountJSONRecords();

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ClientXMLRecord> clientXMLRecords = null;

        try {
            File inputFile = new File("build/out_files/from_cia_2018-05-24-095644-2-3000.xml");

            FromXMLParser fromXMLParser = new FromXMLParser();
            fromXMLParser.execute(connection, null, null, 0);
            fromXMLParser.parseRecordData(String.valueOf(inputFile));
            clientXMLRecords = fromXMLParser.getClientXMLRecords();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        //
        migration.migrateFromTmp();
        //
        //

        for (ClientXMLRecord clientXMLRecord : clientXMLRecords) {
            if (clientXMLRecord.name == null || clientXMLRecord.surname == null || clientXMLRecord.birthDate == null ||
                    clientXMLRecord.gender == null || clientXMLRecord.charm == null) {
                Integer status = migrationTestDao.get().getCiaClientStatus(clientXMLRecord);
                assertThat(status).isEqualTo(1);

                checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.mobilePhones, "MOBILE");
                checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.homePhones, "HOME");
                checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.workPhones, "WORK");

                continue;
            }

            checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.mobilePhones, "MOBILE");
            checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.homePhones, "HOME");
            checkPhonesForErrors(clientXMLRecord.id, clientXMLRecord.workPhones, "WORK");
        }

        for (TransactionJSONRecord transactionJSONRecord : transactionJSONRecords) {
            if (transactionJSONRecord.account_number == null || transactionJSONRecord.transaction_type == null) {
                Integer status = migrationTestDao.get().getCiaTransactionStatus(transactionJSONRecord);
                assertThat(status).isEqualTo(1);
            }
        }

        for (AccountJSONRecord accountJSONRecord : accountJSONRecords) {
            if (accountJSONRecord.client_id == null || accountJSONRecord.account_number == null) {
                Integer status = migrationTestDao.get().getCiaAccountStatus(accountJSONRecord);
                assertThat(status).isEqualTo(1);
            }
        }
    }
    private void checkPhonesForErrors(String clientID, List<String> phones, String phoneType) {
        for (String number : phones) {
            if (number == null) {
                Integer status = migrationTestDao.get().getCiaPhoneStatus(clientID, number, phoneType);
                assertThat(status).isEqualTo(1);
            }
        }
    }

    private void clearTables() throws Exception{
        List<String> tableNames = migrationTestDao.get().getCiaTableNames();
        for (String table : tableNames) {
            if (table.contains("cia_migration_client") || table.contains("cia_migration_phone")
                    || table.contains("cia_migration_account") || table.contains("cia_migration_transaction")) {
                String sql = "truncate " + table + " cascade";
                try (Statement st = connection.createStatement()) {
                    st.execute(sql);
                }
            }
        }
    }
    private void deleteTables() throws Exception{
        String sql = "CREATE OR REPLACE FUNCTION removeTables()\n" +
                "  RETURNS void\n" +
                "LANGUAGE plpgsql AS\n" +
                "$$\n" +
                "DECLARE row  record;\n" +
                "BEGIN\n" +
                "  FOR row IN\n" +
                "  SELECT\n" +
                "    table_schema,\n" +
                "    table_name\n" +
                "  FROM\n" +
                "    information_schema.tables\n" +
                "  WHERE\n" +
                "    table_name LIKE ('cia_migration%')\n" +
                "  LOOP\n" +
                "    EXECUTE 'DROP TABLE ' || quote_ident(row.table_schema) || '.' || quote_ident(row.table_name);\n" +
                "  END LOOP;\n" +
                "END;\n" +
                "$$;\n" +
                "\n" +
                "select removeTables()";

        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    @AfterTest
    private void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.connection = null;
        }
    }
}
