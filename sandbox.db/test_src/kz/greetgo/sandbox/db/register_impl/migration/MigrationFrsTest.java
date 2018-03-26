package kz.greetgo.sandbox.db.register_impl.migration;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;

import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.test.dao.AccountTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.model.AccountFrs;
import kz.greetgo.sandbox.db.test.model.Transaction;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.DbUtils;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError.CLIENT_ID_NULL_ERROR;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError.TRANSACTION_ACCOUNT_NOT_EXIST_ERROR;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationFrsTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<DbConfig> dbConfig;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> clientTestDao;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<AccountTestDao> accountTestDao;
  @SuppressWarnings("WeakerAccess")
  public final Logger logger = Logger.getLogger("MIGRATION.TEST");

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
  private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");


  @Test
  void insertIntoTempTablesFrsTest() throws Exception {

    int numberOfAccounts = 10;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, 2);

    File testData = generateFrs(accounts, transactions);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      //
      //
      migration.parseFileAndUploadToTempTables();
      //
      //
    }

    List<AccountFrs> accountList = accountTestDao.get().getTempAccountList(tableNames.get(TMP_ACCOUNT));
    List<Transaction> transactionList = accountTestDao.get().getTempTransactionList(tableNames.get(TMP_TRANSACTION));

    assertThat(accountList).hasSize(accounts.size());
    assertThat(transactionList).hasSize(transactions.size());
    for (int i = 0; i < accountList.size(); i++) {
      AccountFrs target = accountList.get(i);
      AccountFrs assertion = accounts.get(i);
      assertThat(target).isEqualTo(assertion);
    }
    for (int i = 0; i < transactionList.size(); i++) {
      Transaction target = transactionList.get(i);
      Transaction assertion = transactions.get(i);
      assertThat(target).isEqualTo(assertion);
    }

    dropTempTables(tableNames);
  }


  @Test
  void markErrorsFrsTest() throws Exception {

    final int numberOfAccounts = 10;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, 1);

    Map<String, MigrationError> accountToError = new HashMap<>();
    Map<String, MigrationError> transactToError = new HashMap<>();

    AccountFrs account1 = accounts.get(1);
    account1.client_id = null;
    accountToError.put(account1.id, CLIENT_ID_NULL_ERROR);

    for (Transaction transaction : account1.transactionList) {
      transactToError.put(transaction.id, TRANSACTION_ACCOUNT_NOT_EXIST_ERROR);
    }

    AccountFrs account2 = accounts.get(2);
    account2.account_number = "brokenString";

    for (Transaction transaction : account2.transactionList) {
      transactToError.put(transaction.id, TRANSACTION_ACCOUNT_NOT_EXIST_ERROR);
    }


    MigrationConfig config = new MigrationConfig();
    config.id = idGenerator.get().newId();
    config.idGenerator = idGenerator.get();

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);

      tableNames = migration.tableNames;

      for (AccountFrs account : accounts)
        accountTestDao.get().insertIntoTempAccount(account, tableNames.get(TMP_ACCOUNT));
      for (Transaction transaction : transactions)
        accountTestDao.get().insertIntoTempTransaction(transaction, tableNames.get(TMP_TRANSACTION));

      //
      //
      migration.markErrorRows();
      //
      //
    }
    List<AccountFrs> accountList = accountTestDao.get().getTempAccountList(tableNames.get(TMP_ACCOUNT));
    List<Transaction> transactionList = accountTestDao.get().getTempTransactionList(tableNames.get(TMP_TRANSACTION));

    assertThat(accountList).hasSize(accounts.size());
    assertThat(transactionList).hasSize(transactions.size());

    for (AccountFrs target : accountList) {
      if (accountToError.containsKey(target.id)) {
        MigrationError err = accountToError.get(target.id);
        assertThat(target.error).isEqualTo(err.message);
      } else {
        assertThat(target.error).isNull();
      }
    }

    for (Transaction target : transactionList) {
      if (transactToError.containsKey(target.id)) {
        assertThat(target.error).isEqualTo(TRANSACTION_ACCOUNT_NOT_EXIST_ERROR.message);
      } else {
        assertThat(target.error).isNull();
      }

    }

    dropTempTables(tableNames);
  }

  @Test
  void insertClientAccountsTest() throws Exception {
    accountTestDao.get().clear();

    final int numberOfAccounts = 10;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);

    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      for (AccountFrs account : accounts)
        accountTestDao.get().insertIntoTempAccount(account, tableNames.get(TMP_ACCOUNT));
      //
      //
      migration.upsertIntoDbValidRows();
      //
      //
    }

    List<AccountFrs> result = accountTestDao.get().getRealAccountListOrderByNumber();
    accounts.sort(Comparator.comparing(account -> account.account_number));

    assertThat(result).hasSize(accounts.size());
    for (int i = 0; i < result.size(); i++) {
      AccountFrs target = result.get(i);
      AccountFrs assertion = accounts.get(i);

      assertThat(target.account_number).isEqualTo(assertion.account_number);
      assertThat(target.client_id).isEqualTo(assertion.client_id);
      assertThat(target.registered_at).isEqualTo(assertion.registered_at);
    }

    dropTempTables(tableNames);
  }

  @Test
  void insertTransactionWithAccountTest() throws Exception {
    accountTestDao.get().clear();

    final int numberOfAccounts = 1;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, 2);


    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;
    //noinspection Duplicates
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      for (AccountFrs account : accounts)
        accountTestDao.get().insertIntoTempAccount(account, tableNames.get(TMP_ACCOUNT));
      for (Transaction transaction : transactions)
        accountTestDao.get().insertIntoTempTransaction(transaction, tableNames.get(TMP_TRANSACTION));

      //
      //
      migration.upsertIntoDbValidRows();
      //
      //
    }

    List<Transaction> result = accountTestDao.get().getRealTransactionListOrderById();
    transactions.sort(Comparator.comparing(transaction -> transaction.id));

    assertThat(result).hasSize(transactions.size());
    //noinspection Duplicates
    for (int i = 0; i < result.size(); i++) {
      Transaction target = result.get(i);
      Transaction assertion = transactions.get(i);

      assertThat(target.transaction_type).isEqualTo(assertion.transaction_type);
      assertThat(target.finished_at).isEqualTo(assertion.finished_at);
      assertThat(target.account_number).isEqualTo(assertion.account_number);

    }

    dropTempTables(tableNames);
  }


  @Test
  void insertTransactionOnTheExistingAccountTest() throws Exception {
    accountTestDao.get().clear();

    ClientAccountDot account = new ClientAccountDot();
    account.id = idGenerator.get().newId();
    account.client = idGenerator.get().newId();
    account.number = idGenerator.get().newId();
    account.registeredAt = new Timestamp(new Date().getTime());
    account.money = 0;
    accountTestDao.get().insertAccount(account);

    List<Transaction> transactions = rndTransactions(account.number, 2);


    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      for (Transaction transaction : transactions)
        accountTestDao.get().insertIntoTempTransaction(transaction, tableNames.get(TMP_TRANSACTION));

      //
      //
      migration.upsertIntoDbValidRows();
      //
      //
    }

    List<Transaction> result = accountTestDao.get().getRealTransactionListOrderById();
    transactions.sort(Comparator.comparing(transaction -> transaction.id));

    assertThat(result).hasSize(transactions.size());
    //noinspection Duplicates
    for (int i = 0; i < result.size(); i++) {
      Transaction target = result.get(i);
      Transaction assertion = transactions.get(i);

      assertThat(target.transaction_type).isEqualTo(assertion.transaction_type);
      assertThat(target.finished_at).isEqualTo(assertion.finished_at);
      assertThat(target.account_number).isEqualTo(assertion.account_number);

    }

    dropTempTables(tableNames);
  }


  @Test
  void errorFrsRowsNotUpsertedTest() throws Exception {
    accountTestDao.get().clear();

    final int numberOfAccounts = 10;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, 2);
    Set<String> invalidRows = new HashSet<>();

    AccountFrs errorRow1 = accounts.get(2);
    AccountFrs errorRow2 = accounts.get(5);
    errorRow1.error = "error";
    errorRow2.error = "error";

    invalidRows.add(errorRow1.account_number);
    invalidRows.add(errorRow2.account_number);

    for (Transaction transaction : transactions) {
      if (invalidRows.contains(transaction.account_number)) {
        transaction.error = "error";
      }
    }
    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;
    //noinspection Duplicates
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      for (AccountFrs account : accounts)
        accountTestDao.get().insertIntoTempAccount(account, tableNames.get(TMP_ACCOUNT));
      for (Transaction transaction : transactions)
        accountTestDao.get().insertIntoTempTransaction(transaction, tableNames.get(TMP_TRANSACTION));

      //
      //
      migration.upsertIntoDbValidRows();
      //
      //
    }
    List<String> accountNumberList = accountTestDao.get().getList("clientaccount", "number");
    List<String> transactionList = accountTestDao.get().getList("clientaccounttransaction", "account");

    boolean foundErrorAccount = accountNumberList.stream().anyMatch(invalidRows::contains);
    boolean foundErrorTransaction = transactionList.stream().anyMatch(invalidRows::contains);

    assertThat(foundErrorAccount).isFalse();
    assertThat(foundErrorTransaction).isFalse();
    dropTempTables(tableNames);
  }

  @Test
  void uploadErrorsFrsTest() throws Exception {

    accountTestDao.get().clear();

    final int numberOfAccounts = 10;
    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, 2);
    Set<String> invalidRows = new HashSet<>();

    AccountFrs errorRow1 = accounts.get(2);
    AccountFrs errorRow2 = accounts.get(5);
    errorRow1.error = "error";
    errorRow2.error = "error";

    invalidRows.add(errorRow1.client_id);
    invalidRows.add(errorRow2.client_id);

    for (Transaction transaction : transactions) {
      if (invalidRows.contains(transaction.account_number)) {
        transaction.error = "error";
      }
    }

    MigrationConfig config = new MigrationConfig();
    config.error = new File(Modules.dbDir() + "/build/temp/file" + idGenerator.get().newId());

    if (!config.error.getParentFile().exists())
      if (!config.error.getParentFile().mkdirs()) {
        throw new Exception("couldn't create file " + config.error.getAbsolutePath());
      }

    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {
      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      for (AccountFrs account : accounts)
        accountTestDao.get().insertIntoTempAccount(account, tableNames.get(TMP_ACCOUNT));
      for (Transaction transaction : transactions)
        accountTestDao.get().insertIntoTempTransaction(transaction, tableNames.get(TMP_TRANSACTION));

      //
      //
      migration.loadErrorsAndWrite();
      //
      //
    }

    Pattern clientIdPattern = java.util.regex.Pattern.compile("client_id: (.+?);");

    try (FileReader reader = new FileReader(config.error);
         BufferedReader br = new BufferedReader(reader)) {
      String line;
      while ((line = br.readLine()) != null) {

        Matcher matcher = clientIdPattern.matcher(line);
        assertThat(matcher.find()).isTrue();
        String cia_id = matcher.group(1);
        assertThat(invalidRows.contains(cia_id)).isTrue();
      }
    }

    if (!config.error.delete()) {
      logger.warn("temp file not deleted" + config.error.getAbsolutePath());
    }

    dropTempTables(tableNames);
  }


  @Test
  void frsFullMigrateTest() throws Exception {
    accountTestDao.get().clear();
    final int numberOfAccounts = 10;
    final int numberOfTransactionPerAccount = 2;

    List<AccountFrs> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactionsForAccountList(accounts, numberOfTransactionPerAccount);

    File testData = generateFrs(accounts, transactions);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    config.error = new File(Modules.dbDir() + "/build/temp/file" + idGenerator.get().newId());

    if (!config.error.getParentFile().exists()) {
      if (!config.error.getParentFile().mkdirs()) {
        throw new Exception("couldn't create file " + config.error.getAbsolutePath());
      }
    }

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrs migration = new MigrationFrs(config, connection);
      tableNames = migration.tableNames;

      //
      //
      migration.migrate();
      //
      //
    }
    {
      List<AccountFrs> accountList = accountTestDao.get().getTempAccountList(tableNames.get(TMP_ACCOUNT));
      List<Transaction> transactionList = accountTestDao.get().getTempTransactionList(tableNames.get(TMP_TRANSACTION));
      assertThat(accountList).hasSize(numberOfAccounts);
      assertThat(transactionList).hasSize(numberOfAccounts * numberOfTransactionPerAccount);
    }

    List<String> accountList = accountTestDao.get().getList("clientaccount", "number");
    List<String> transactionList = accountTestDao.get().getList("clientaccounttransaction", "account");
    assertThat(accountList).hasSize(numberOfAccounts);
    assertThat(transactionList).hasSize(numberOfAccounts * numberOfTransactionPerAccount);

    dropTempTables(tableNames);
  }

///////////////////////////////////////////////////////////////////////////////////////////////////////


  private File generateFrs(List<AccountFrs> accounts, List<Transaction> transactions) throws IOException {
    File file = new File(Modules.dbDir() + "/build/temp/file.json.txt");

    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();

    Gson gson = new Gson();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      for (AccountFrs account : accounts) {
        writer.write(gson.toJson(account));
        writer.newLine();
      }
      for (Transaction transaction : transactions) {
        writer.write(gson.toJson(transaction));
        writer.newLine();
      }
    }
    return file;
  }


  private List<AccountFrs> rndAccounts(int n) {
    List<AccountFrs> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      AccountFrs account = new AccountFrs();
      account.id = idGenerator.get().newId();
      account.type = "new_account";
      account.account_number = idGenerator.get().newId();
      account.client_id = idGenerator.get().newId();
      account.registered_at = getTimeStampFormat(new Date());
      list.add(account);
    }
    return list;
  }

  private List<Transaction> rndTransactionsForAccountList(List<AccountFrs> accounts, int n) {
    List<Transaction> all = new ArrayList<>();
    for (AccountFrs account : accounts) {

      List<Transaction> list = rndTransactions(account.account_number, n);

      all.addAll(list);
      account.transactionList.addAll(list);
    }
    return all;
  }

  private List<Transaction> rndTransactions(String accountNumber, int n) {
    List<Transaction> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Transaction transaction = new Transaction();
      transaction.id = idGenerator.get().newId();
      transaction.type = "transaction";
      transaction.money = String.valueOf(RND.plusDouble(100, 2));
      transaction.transaction_type = RND.str(10);
      transaction.account_number = String.valueOf(accountNumber);
      transaction.finished_at = getTimeStampFormat(new Date());
      list.add(transaction);

    }
    return list;
  }


  private String getTimeStampFormat(Date date) {
    return dateFormat.format(date) + "T" + timeFormat.format(date);
  }


  private void dropTempTables(Map<TmpTableName, String> tableNames) {
    clientTestDao.get().dropTable(tableNames.get(TMP_ACCOUNT));
    clientTestDao.get().dropTable(tableNames.get(TMP_TRANSACTION));
  }
}
