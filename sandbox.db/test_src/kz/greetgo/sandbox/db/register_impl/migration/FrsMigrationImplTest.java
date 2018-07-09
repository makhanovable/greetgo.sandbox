package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientAddrType;
import kz.greetgo.sandbox.db.migration.FRSMigration;
import kz.greetgo.sandbox.db.migration.model.Account;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Transaction;
import kz.greetgo.sandbox.db.test.dao.FrsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class FrsMigrationImplTest extends ParentTestNg {

    public BeanGetter<FrsTestDao> frsTestDao;
    private int maxBatchSize = 500_000;

    @Test
    public void insert_to_tmp_acc() throws Exception {
        remove_all_data_from_tables();
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
        Account result = frsTestDao.get().getAccountById(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.account_number).isEqualTo(account_number);
        assertThat(result.registered_at).isEqualTo(registered_at);
    }

    @Test
    public void insert_to_tmp_trans() throws Exception {
        remove_all_data_from_tables();
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
    public void validate_tmp_trans() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        List<Transaction> result = frsTestDao.get().getTransactions();
        //
        //
        //
        assertThat(result).hasSize(1);
        assertThat(result.get(0).account_number).isNotNull();
    }


    @Test
    public void validate_tmp_acc() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_frs.txt";
        String cia_id = "2-T9E-FQ-IC-vomCQG1s9s";

        Connection connection = getConnection();
        //
        //
        //
        FRSMigration frsMigration = new FRSMigration(connection, file, maxBatchSize);
        frsMigration.migrate();
        connection.close();
        Account result = frsTestDao.get().getAccountById(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.account_number).isNotNull();
        assertThat(result.registered_at).isNotNull();
    }

    @Test
    public void frs_integration_test() throws Exception {
        remove_all_data_from_tables();
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

    private void remove_all_data_from_tables() {
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
