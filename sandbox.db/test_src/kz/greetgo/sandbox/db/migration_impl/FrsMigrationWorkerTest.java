package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.migration_impl.model.AccountTmp;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.TransactionTmp;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.ssh.LocalFileWorker;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link FrsMigrationWorker}
 */
public class FrsMigrationWorkerTest extends ParentTestNg {
  public BeanGetter<Migration> migration;
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGen;

  public BeanGetter<MigrationTestDao> migrationTestDao;

  private Connection connection;
  private LocalFileWorker localFileWorker;

  private File outErrorFile;
  private ReportXlsx reportXlsx;

  @BeforeClass
  public void prepareResources() throws Exception {
    connection = migration.get().getConnection();
    migration.get().setSshMode(false);
    localFileWorker = (LocalFileWorker) migration.get().getInputFileWorker();
    localFileWorker.homePath = "build/out_files/";

    outErrorFile = new File(migrationConfig.get().inFilesHomePath() + migrationConfig.get().outErrorFileName());
    outErrorFile.getParentFile().mkdirs();
    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();
  }

  @AfterClass
  public void closeResources() throws Exception {
    connection.close();
    localFileWorker.close();
    connection = null;
    localFileWorker = null;
  }

  @Test
  public void test_creationOfTmpTables() throws Exception {
    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();

    //
    //
    frsMigrationWorker.createTmpTables();
    //
    //
  }

  @Test
  public void test_parsingAndInsertionIntoTmpDb() throws Exception {
    clientTestDao.get().removeAllData();

    String transactionInput = "{\"type\":\"transaction\"," +
      "\"transaction_type\":\"Списывание с регионального бюджета Алматинской области\"," +
      "\"account_number\":\"79482KZ058-28927-83285-8377209\",\"money\":\"+820_265.04\"," +
      "\"finished_at\":\"2011-03-28T10:22:57.320\"}\n";

    String accountInput = "{\"client_id\":\"2-9UB-27-AG-nkXCRqL7mL\",\"account_number\":\"79482KZ058-28927-83285-8377209\"," +
      "\"type\":\"new_account\",\"registered_at\":\"2001-03-28T10:21:21.319\"}";

    TransactionTmp expectedTransaction = new TransactionTmp();
    expectedTransaction.money = BigDecimal.valueOf(820_265.04);
    expectedTransaction.transaction_type = "Списывание с регионального бюджета Алматинской области";

    AccountTmp expectedAccount = new AccountTmp();
    expectedAccount.account_number = "79482KZ058-28927-83285-8377209";
    expectedAccount.registeredAtD = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-03-28T10:21:21.319");

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.inputStream = new ByteArrayInputStream((transactionInput + accountInput).getBytes("UTF-8"));
    frsMigrationWorker.createTmpTables();

    //
    //
    frsMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<TransactionTmp> actualTransactionTmpList = migrationTestDao.get().loadTransactionsList(frsMigrationWorker.tmpTransactionTable);
    List<AccountTmp> actualAccountTmpList = migrationTestDao.get().loadAccountsList(frsMigrationWorker.tmpAccountTable);

    assertThat(actualTransactionTmpList).isNotNull();
    assertThat(actualTransactionTmpList).hasSize(1);
    assertThatAreEqual(actualTransactionTmpList.get(0), expectedTransaction);

    assertThat(actualAccountTmpList).isNotNull();
    assertThat(actualAccountTmpList).hasSize(1);
    assertThatAreEqual(actualAccountTmpList.get(0), expectedAccount);
  }

  @Test
  public void test_parseDataAndSaveInTmpDb_on_broken_json() throws Exception {
    clientTestDao.get().removeAllData();

    String accountInput = "{\"client_id\":\"2-9UB-27-AG-nkXCRqL7mL\",\"account_number\":\"79482KZ058-28927-83285-8377209\"," +
      "\"type\":\"new_account\",\"registered_at\":\"2001-03-28T10:21:21.319\"}\n";

    String transactionInput = "{\"type\":\"transaction\"," +
      "\"transaction_type\":\"Списывание с ре" +
      "\"" + /*Adding parse error!!!!!*/
      "гионального бюджета Алматинской области\"," +
      "\"account_number\":\"79482KZ058-28927-83285-8377209\",\"money\":\"+820_265.04\"," +
      "\"finished_at\":\"2011-03-28T10:22:57.320\"}\n";

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.inputStream = new ByteArrayInputStream((
      accountInput /*line #1*/
        + transactionInput/*line #2*/
        + accountInput/*line #3*/
    ).getBytes("UTF-8"));
    ByteArrayOutputStream errorBytesOutput = new ByteArrayOutputStream();
    frsMigrationWorker.outError = errorBytesOutput;
    frsMigrationWorker.createTmpTables();

    //
    //
    frsMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<TransactionTmp> actualTransactionTmpList = migrationTestDao.get().loadTransactionsList(frsMigrationWorker.tmpTransactionTable);
    List<AccountTmp> actualAccountTmpList = migrationTestDao.get().loadAccountsList(frsMigrationWorker.tmpAccountTable);

    assertThat(actualTransactionTmpList).hasSize(0);
//    assertThat(actualAccountTmpList).hasSize(2);

    assertThat(errorBytesOutput.size()).isGreaterThan(0);

    String errorStr = errorBytesOutput.toString("UTF-8");
    assertThat(errorStr).contains("[Line #2]");
    assertThat(errorStr).contains("Unexpected character");
    assertThat(errorStr).contains("was expecting comma to separate");
  }


  @Test
  public void test_creation_if_idle_clients_if_not_exist() throws Exception {
    clientTestDao.get().removeAllData();

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.createTmpTables();

    AccountTmp account1 = new AccountTmp();
    account1.clientId = idGen.get().newId();
    account1.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account1.registeredAtD = RND.dateDays(-20, 0);

    AccountTmp account2 = new AccountTmp();
    account2.clientId = idGen.get().newId();
    account2.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account2.registeredAtD = RND.dateDays(-20, 0);

    migrationTestDao.get().insertClientAccount(frsMigrationWorker.tmpAccountTable, account1);
    migrationTestDao.get().insertClientAccount(frsMigrationWorker.tmpAccountTable, account2);

    //
    //
    frsMigrationWorker.createIdleClientsIfNotExist();
    //
    //

    List<ClientTmp> actualList = clientTestDao.get().loadClientList();

    assertThat(actualList).isNotNull();
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).cia_id).isNotNull();
    assertThat(actualList.get(1).cia_id).isNotNull();
  }

  @Test
  public void test_insertion_of_client_accounts() throws Exception {
    clientTestDao.get().removeAllData();

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.createTmpTables();

    ClientTmp clientTmp1 = new ClientTmp();
    clientTmp1.number = 0;
    clientTmp1.cia_id = idGen.get().newId();

    ClientTmp clientTmp2 = new ClientTmp();
    clientTmp2.number = 1;
    clientTmp2.cia_id = idGen.get().newId();

    clientTestDao.get().insertClientTmp(clientTmp1);
    clientTestDao.get().insertClientTmp(clientTmp2);

    AccountTmp account1 = new AccountTmp();
    account1.clientId = clientTmp1.cia_id;
    account1.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account1.registeredAtD = RND.dateDays(-20, -9);

    TransactionTmp transaction1 = new TransactionTmp();
    transaction1.account_number = account1.account_number;
    transaction1.money = BigDecimal.valueOf(0);
    transaction1.finishedAtD = RND.dateDays(-20, 0);
    migrationTestDao.get().insertAccountTransaction(frsMigrationWorker.tmpTransactionTable, transaction1);

    AccountTmp account2 = new AccountTmp();
    account2.clientId = clientTmp2.cia_id;
    account2.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account2.registeredAtD = RND.dateDays(-10, 0);

    TransactionTmp transaction2 = new TransactionTmp();
    transaction2.account_number = account2.account_number;
    transaction2.money = BigDecimal.valueOf(0);
    transaction2.finishedAtD = RND.dateDays(-20, 0);
    migrationTestDao.get().insertAccountTransaction(frsMigrationWorker.tmpTransactionTable, transaction2);

    migrationTestDao.get().insertClientAccount(frsMigrationWorker.tmpAccountTable, account1);
    migrationTestDao.get().insertClientAccount(frsMigrationWorker.tmpAccountTable, account2);

    //
    //
    frsMigrationWorker.insertClientAccounts();
    //
    //

    List<AccountTmp> actualAccountList = clientTestDao.get().loadAccountList();
    assertThat(actualAccountList).isNotNull();
    assertThat(actualAccountList).hasSize(2);
    assertThatAreEqual(actualAccountList.get(0), account1);
    assertThatAreEqual(actualAccountList.get(1), account2);
  }

  @Test
  public void test_insertion_of_account_transactions() throws Exception {
    clientTestDao.get().removeAllData();

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.createTmpTables();

    ClientTmp clientTmp1 = new ClientTmp();
    clientTmp1.number = 0;
    clientTmp1.cia_id = idGen.get().newId();

    ClientTmp clientTmp2 = new ClientTmp();
    clientTmp2.number = 1;
    clientTmp2.cia_id = idGen.get().newId();

    clientTestDao.get().insertClientTmp(clientTmp1);
    clientTestDao.get().insertClientTmp(clientTmp2);

    AccountTmp account1 = new AccountTmp();
    account1.number = clientTmp1.number;
    account1.clientId = String.valueOf(clientTmp1.number);
    account1.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account1.registeredAtD = RND.dateDays(-20, -9);

    TransactionTmp transaction1 = new TransactionTmp();
    transaction1.account_number = account1.account_number;
    transaction1.money = BigDecimal.valueOf(0);
    transaction1.finishedAtD = RND.dateDays(-20, -9);
    transaction1.transaction_type = "Transfer";
    migrationTestDao.get().insertAccountTransaction(frsMigrationWorker.tmpTransactionTable, transaction1);

    AccountTmp account2 = new AccountTmp();
    account2.number = clientTmp2.number;
    account2.clientId = String.valueOf(clientTmp2.number);
    account2.account_number = RND.intStr(5) + "KZ" + RND.intStr(3) + "-" + RND.intStr(5) + "-" + RND.intStr(5) + "-" + RND.intStr(7);
    account2.registeredAtD = RND.dateDays(-10, 0);

    TransactionTmp transaction2 = new TransactionTmp();
    transaction2.account_number = account2.account_number;
    transaction2.money = BigDecimal.valueOf(0);
    transaction2.finishedAtD = RND.dateDays(-10, 0);
    transaction2.transaction_type = "Offshore";
    migrationTestDao.get().insertAccountTransaction(frsMigrationWorker.tmpTransactionTable, transaction2);

    clientTestDao.get().insertAccountTmp(account1);
    clientTestDao.get().insertAccountTmp(account2);

    //
    //
    frsMigrationWorker.insertAccountTransactions();
    //
    //

    List<TransactionTmp> actualTransactionList = clientTestDao.get().loadTransactionList();
    assertThat(actualTransactionList).isNotNull();
    assertThat(actualTransactionList).hasSize(2);
    assertThatAreEqual(actualTransactionList.get(0), transaction1);
    assertThatAreEqual(actualTransactionList.get(1), transaction2);
  }

  @Test
  public void test_all_frs_migration() throws Exception {
    clientTestDao.get().removeAllData();

    String transactionInput = "{\"transaction_type\":\"Отмывание на компьютерной технике\"," +
      "\"finished_at\":\"2011-03-28T10:21:05.320\",\"money\":\"-500.89\"," +
      "\"account_number\":\"09256KZ233-44751-42570-8507017\",\"type\":\"transaction\"}\n";

    String accountInput = "{\"type\":\"new_account\",\"account_number\":\"09256KZ233-44751-42570-8507017\"," +
      "\"client_id\":\"2-9UB-27-AG-nkXCRqL7mL\",\"registered_at\":\"2001-03-28T10:21:09.319\"}";

    TransactionTmp expectedTransaction = new TransactionTmp();
    expectedTransaction.money = BigDecimal.valueOf(-500.89);
    expectedTransaction.transaction_type = "Отмывание на компьютерной технике";

    AccountTmp expectedAccount = new AccountTmp();
    expectedAccount.account_number = "09256KZ233-44751-42570-8507017";
    expectedAccount.registeredAtD = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-03-28T10:21:09.319");

    FrsMigrationWorker frsMigrationWorker = getFrsMigrationWorker();
    frsMigrationWorker.inputStream = new ByteArrayInputStream((transactionInput + accountInput).getBytes());

    //
    //
    frsMigrationWorker.migrate();
    //
    //

    List<TransactionTmp> actualTransactionTmpList = clientTestDao.get().loadTransactionList();
    List<AccountTmp> actualAccountTmpList = clientTestDao.get().loadAccountList();

    assertThat(actualTransactionTmpList).isNotNull();
    assertThat(actualTransactionTmpList).hasSize(1);
    assertThatAreEqual(actualTransactionTmpList.get(0), expectedTransaction);

    assertThat(actualAccountTmpList).isNotNull();
    assertThat(actualAccountTmpList).hasSize(1);
    assertThatAreEqual(actualAccountTmpList.get(0), expectedAccount);
  }

  private void assertThatAreEqual(AccountTmp actual, AccountTmp expected) {
    assertThat(actual.account_number).isEqualTo(expected.account_number);
    assertThat(actual.registeredAtD).isEqualTo(expected.registeredAtD);
  }

  private FrsMigrationWorker getFrsMigrationWorker() {
    FrsMigrationWorker frsMigrationWorker = new FrsMigrationWorker(connection);
//    frsMigrationWorker.outError = outError;
    frsMigrationWorker.reportXlsx = reportXlsx;
    frsMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return frsMigrationWorker;
  }

  private void assertThatAreEqual(TransactionTmp actual, TransactionTmp expected) throws ParseException {
    assertThat(actual.money.compareTo(expected.money)).isEqualTo(0);
    assertThat(actual.transaction_type).isEqualTo(String.valueOf(expected.transaction_type));
  }

}