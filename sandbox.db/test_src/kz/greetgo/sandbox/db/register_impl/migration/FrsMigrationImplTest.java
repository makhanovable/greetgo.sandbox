package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration.FRSMigration;
import kz.greetgo.sandbox.db.migration.model.Account;
import kz.greetgo.sandbox.db.migration.model.Transaction;
import kz.greetgo.sandbox.db.test.dao.FrsTestDao;
import kz.greetgo.sandbox.db.test.util.FrsGeneratorUtil;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.codehaus.jackson.JsonParseException;
import org.postgresql.util.PSQLException;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class FrsMigrationImplTest extends ParentTestNg {

    public BeanGetter<FrsTestDao> frsTestDao;
    private int maxBatchSize = 500_000;

    @Test
    public void isTempTablesCreated() throws Exception {
        TRUNCATE();

        Account account = generateRNDAccount();
        List<Transaction> transactions = generateRNDTransactions(account.account_number, 10);
        String file = FrsGeneratorUtil.generateFrsFile(account, transactions);
        Connection connection = getConnection();

        String ex_tmp_trans = "tmp_trans";
        String ex_tmp_acc = "tmp_acc";

        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        //
        //
        //
        String tmp_trans = frsTestDao.get().isTableExists(ex_tmp_trans);
        String tmp_acc = frsTestDao.get().isTableExists(ex_tmp_acc);

        assertThat(tmp_trans).isNotNull();
        assertThat(tmp_acc).isNotNull();
        assertThat(tmp_trans).isEqualTo(ex_tmp_trans);
        assertThat(tmp_acc).isEqualTo(ex_tmp_acc);

    }

    @Test
    public void insertingToTempClientAccountTable() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";
        String cia_id = "2-T9E-FQ-IC-vomCQG1s9s";
        String account_number = "84466KZ333-27891-50080-1292286";
        String registered_at = "2001-02-21 15:45:45.532";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        Account result = frsTestDao.get().getAccountById(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.account_number).isEqualTo(account_number);
        assertThat(result.registered_at).isEqualTo(registered_at);
    }

    @Test
    public void insertingToTempCliectAccountTransactionsTable() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";
        String account_number = "84466KZ333-27891-50080-1292286";
        String money = "0";
        String finished_at = "2011-02-21 15:45:51.537";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        List<Transaction> result = frsTestDao.get().getTransactions();
        //
        //
        //
        assertThat(result).hasSize(1);
        assertThat(result.get(0).account_number).isEqualTo(account_number);
        assertThat(result.get(0).money).isEqualTo(money);
        assertThat(result.get(0).finished_at).isEqualTo(finished_at);
    }

    @Test
    public void checkToErrorTempClientAccountTransactionsTable() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        List<Transaction> result = frsTestDao.get().getTransactions();
        //
        //
        //
        assertThat(result).hasSize(1);
        assertThat(result.get(0).account_number).isNotNull();
    }


    @Test
    public void checkToErrorTempClientAccountTable() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";
        String cia_id = "2-T9E-FQ-IC-vomCQG1s9s";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        Account result = frsTestDao.get().getAccountById(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.account_number).isNotNull();
        assertThat(result.registered_at).isNotNull();
    }

    @Test(expectedExceptions = PSQLException.class)
    public void migration_wrongAccountNumber() throws Exception {
        TRUNCATE();
        Account account = generateRNDAccount();
        account.account_number = null;
        List<Transaction> transactions = generateRNDTransactions(account.account_number, 10);

        String file = FrsGeneratorUtil.generateFrsFile(account, transactions);
        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void migration_wrongAccountClientid() throws Exception {
        TRUNCATE();
        Account account = generateRNDAccount();
        account.client_id = null;
        List<Transaction> transactions = generateRNDTransactions(account.account_number, 10);

        String file = FrsGeneratorUtil.generateFrsFile(account, transactions);
        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
    }

    @Test
    public void frsIntegration() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";
        String cia_id = "2-T9E-FQ-IC-vomCQG1s9s";
        String account_number = "84466KZ333-27891-50080-1292286";
        String registered_at = "2001-02-21 15:45:45.532";
        String money = "0";
        String finished_at = "2011-02-21 15:45:51.537";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
        Account result_acc = frsTestDao.get().getAccountById(cia_id);
        List<Transaction> result_tr = frsTestDao.get().getTransactions();
        //
        //
        //
        assertThat(result_acc).isNotNull();
        assertThat(result_acc.account_number).isEqualTo(account_number);
        assertThat(result_acc.registered_at).isEqualTo(registered_at);

        assertThat(result_tr).hasSize(1);
        assertThat(result_tr.get(0).account_number).isEqualTo(account_number);
        assertThat(result_tr.get(0).money).isEqualTo(money);
        assertThat(result_tr.get(0).finished_at).isEqualTo(finished_at);
    }

    @Test(expectedExceptions = JsonParseException.class)
    public void migration_WrongFrs() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/wrong_frs.txt";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        connection = null;
    }

    private Account generateRNDAccount() {
        Account clientAccount = new Account();
        clientAccount.client_id = RND.str(10);
        clientAccount.account_number = RND.str(10);
        clientAccount.registered_at = "2011-02-21T15:45:51.537";
        return clientAccount;
    }

    private List<Transaction> generateRNDTransactions(String account_number, int count) {
        List<Transaction> t = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Transaction tr = new Transaction();
            tr.money = RND.intStr(5);
            tr.finished_at = "2011-02-21T15:45:51.537";
            tr.account_number = account_number;
            tr.transaction_type = RND.str(10);
            t.add(tr);
        }
        return t;
    }

    private void TRUNCATE() {
        frsTestDao.get().TRUNCATE();
    }

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/makhan_sandbox",
                "makhan_sandbox",
                "111"
        );
    }

}
