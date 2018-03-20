package kz.greetgo.sandbox.db.register_impl;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import kz.greetgo.sandbox.db.register_impl.migration_test.MigrationCiaTest;
import kz.greetgo.sandbox.db.register_impl.migration_test.MigrationFrsTest;
import kz.greetgo.sandbox.db.test.dao.AccountTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.model.Account;
import kz.greetgo.sandbox.db.test.model.Client;
import kz.greetgo.sandbox.db.test.model.Transaction;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.DbUtils;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError.*;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.*;
import static org.fest.assertions.api.Assertions.assertThat;


public class MigrationRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;
  @SuppressWarnings({"WeakerAccess", "unused"})
  public BeanGetter<MigrationRegister> migrationRegister;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<DbConfig> dbConfig;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> clientTestDao;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<AccountTestDao> accountTestDao;

  @SuppressWarnings("WeakerAccess")
  public final Logger logger = Logger.getLogger("MIGRATION.TEST");

  @Test
  void test() throws Exception {
    //TODO delete
//    this.migrationRegister.get().migrate();
  }

  @Test
  void createTempTableCiaFrsTest() throws Exception {
    dropTempTables();
    Map<TmpTableName, String> ciaTableNames;
    Map<TmpTableName, String> frsTableNames;

    MigrationConfig config = new MigrationConfig();
    config.id = idGenerator.get().newId();

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCiaTest ciaMigration = new MigrationCiaTest(config, connection);
      ciaTableNames = ciaMigration.getTableNames();
      MigrationFrsTest frsMigration = new MigrationFrsTest(config, connection);
      frsTableNames = frsMigration.getTableNames();

      //
      //
      ciaMigration.createTempTables();
      frsMigration.createTempTables();
      //
      //

    }

    for (String tableName : ciaTableNames.values()) {
      boolean exist = clientTestDao.get().isTableExist(tableName.toLowerCase());
      assertThat(exist).isTrue();
    }
    for (String tableName : frsTableNames.values()) {
      boolean exist = clientTestDao.get().isTableExist(tableName.toLowerCase());
      assertThat(exist).isTrue();
    }

  }

  @Test
  void insertIntoTempTablesCiaTest() throws Exception {
    dropTempTables();
    clientTestDao.get().clear();
    clientTestDao.get().createTempClientTable(TMP_CLIENT.name());
    clientTestDao.get().createTempAddressTable(TMP_ADDRESS.name());
    clientTestDao.get().createTempPhoneTable(TMP_PHONE.name());

    int numberOfClients = 10;
    List<Client> list = rndClients(numberOfClients);
    File testData = genereteCia(list);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();


    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCiaTest migration = new MigrationCiaTest(config, connection);
      tableNames = migration.getTableNames();
      tableNames.put(TMP_CLIENT, TMP_CLIENT.name());
      tableNames.put(TMP_ADDRESS, TMP_ADDRESS.name());
      tableNames.put(TMP_PHONE, TMP_PHONE.name());

      //
      //
      migration.parseAndInsertRows();
      //
      //
    }

    List<Client> result = clientTestDao.get().getTempClientList(tableNames.get(TMP_CLIENT));

    assertThat(result).hasSize(list.size());

    for (int i = 0; i < result.size(); i++) {
      Client target = result.get(i);
      Client assertion = list.get(i);

      assertClient(target, assertion);
      target.phoneNumbers = clientTestDao.get().getNumberList(tableNames.get(TMP_PHONE), target.id);

      assertThat(target.phoneNumbers).hasSize(assertion.phoneNumbers.size());

      for (int j = 0; j < target.phoneNumbers.size(); j++) {
        assertPhoneNumber(target.phoneNumbers.get(j), target.phoneNumbers.get(j));
      }

      List<ClientAddress> addresses = clientTestDao.get().getAddressList(tableNames.get(TMP_ADDRESS), target.id);
      assertThat(addresses).hasSize(2);
      assertThat(addresses.get(0)).isEqualTo(assertion.registerAddress);
      assertThat(addresses.get(1)).isEqualTo(assertion.actualAddress);

    }

    if (!testData.delete()) {
      logger.warn("test tmp file not deleted:" + testData.getAbsoluteFile());
    }
  }

  @Test
  void insertIntoTempTablesFrsTest() throws Exception {
    dropTempTables();
    accountTestDao.get().createTempAccountTable(TMP_ACCOUNT.name());
    accountTestDao.get().createTempTransactionTable(TMP_TRANSACTION.name());

    int numberOfAccounts = 10;
    List<Account> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactions(accounts);

    File testData = generateFrs(accounts, transactions);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;


    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationFrsTest migration = new MigrationFrsTest(config, connection);
      tableNames = migration.getTableNames();
      tableNames.put(TMP_ACCOUNT, TMP_ACCOUNT.name());
      tableNames.put(TMP_TRANSACTION, TMP_TRANSACTION.name());
      //
      //
      migration.ParseAndInsertIntoTempTables();
      //
      //
    }
    List<Account> accountList = accountTestDao.get().getTempAccountList(tableNames.get(TMP_ACCOUNT));
    List<Transaction> transactionList = accountTestDao.get().getTempTransactionList(tableNames.get(TMP_TRANSACTION));

    assertThat(accountList).hasSize(accounts.size());
    assertThat(transactionList).hasSize(transactions.size());
    for (int i = 0; i < accountList.size(); i++) {
      Account target = accountList.get(i);
      Account assertion = accounts.get(i);
      assertThat(target).isEqualTo(assertion);
    }
    for (int i = 0; i < transactionList.size(); i++) {
      Transaction target = transactionList.get(i);
      Transaction assertion = transactions.get(i);
      assertThat(target).isEqualTo(assertion);
    }

  }

  @Test
  void markErrorsCiaTest() throws Exception {
    dropTempTables();
    clientTestDao.get().createTempClientTable(TMP_CLIENT.name());
    clientTestDao.get().createTempPhoneTable(TMP_PHONE.name());
    clientTestDao.get().createTempAddressTable(TMP_ADDRESS.name());

    final int numberOfClients = 100;
    List<Client> clients = rndClients(numberOfClients);
    Map<String, List<MigrationError>> clientToError = new HashMap<>();

    final int errorRows = 30;
    for (int i = 0; i < errorRows; i++) {
      Client detail = clients.get(RND.plusInt(clients.size()));

      MigrationError err = RND.someEnum(MigrationError.values());
      switch (err) {
        case NAME_ERROR:
          detail.name = null;
          break;
        case SURNAME_ERROR:
          detail.surname = null;
          break;
        case BIRTH_NULL_ERROR:
          detail.birthDate = null;
          break;
        case AGE_ERROR:
          detail.birthDate = dateFormat.format(RND.dateYears(-1000, -200));
          break;
        case NAME_EMPTY_ERROR:
          detail.name = "   ";
          break;
        case SURNAME_EMPTY_ERROR:
          detail.surname = "      ";
          break;
        case DATE_INVALID_ERROR:
          detail.birthDate = "sdczsdcsd";
          break;
        case CHARM_ERROR:
          detail.charm = null;
          break;
        case CIA_ID_ERROR:
          detail.cia_id = null;
          break;
        default:
          err = null;
          break;
      }
      //noinspection ConstantConditions
      if (err != null) {
        if (clientToError.containsKey(detail.id)) {
          clientToError.get(detail.id).add(err);
        } else {
          List<MigrationError> list = new ArrayList<>();
          list.add(err);
          clientToError.put(detail.id, list);
        }
      }
    }

    insertClients(clients);

    MigrationConfig config = new MigrationConfig();

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCiaTest migration = new MigrationCiaTest(config, connection);
      tableNames = migration.getTableNames();
      tableNames.put(TMP_CLIENT, TMP_CLIENT.name());
      tableNames.put(TMP_ADDRESS, TMP_ADDRESS.name());
      tableNames.put(TMP_PHONE, TMP_PHONE.name());

      //
      //
      migration.validateRows();
      //
      //
    }

    List<Client> result = clientTestDao.get().getTempClientList(TMP_CLIENT.name());
    assertThat(result).hasSize(numberOfClients);
    for (Client client : result) {
      if (clientToError.containsKey(client.id)) {
        List<MigrationError> errs = clientToError.get(client.id);
        boolean match = false;
        for (MigrationError err : errs)
          match = match || err.message.equals(client.error);
        assertThat(match).isTrue();
      } else {
        assertThat(client.error).isNull();
      }
    }

  }

  @Test
  void markErrorsFrsTest() throws Exception {
    dropTempTables();
    accountTestDao.get().createTempAccountTable(TMP_ACCOUNT.name());
    accountTestDao.get().createTempTransactionTable(TMP_TRANSACTION.name());

    final int numberOfAccounts = 100;
    List<Account> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactions(accounts);

    Map<String, MigrationError> accountToError = new HashMap<>();
    Map<String, MigrationError> transactToError = new HashMap<>();


    Account account1 = accounts.get(RND.plusInt(accounts.size()));
    Account account2 = accounts.get(RND.plusInt(accounts.size()));

    transactToError.put(account1.account_number, TRANSACTION_ACCOUNT_NOT_EXIST_ERROR);
    transactToError.put(account2.account_number, TRANSACTION_ACCOUNT_NOT_EXIST_ERROR);

    account1.account_number = null;
    account2.client_id = null;
    accountToError.put(account1.id, ACCOUNT_NULL_ERROR);
    accountToError.put(account2.id, CLIENT_ID_NULL_ERROR);

    Transaction tr = transactions.get(RND.plusInt(transactions.size()));
    tr.account_number = RND.str(10);
    transactToError.put(tr.account_number, TRANSACTION_ACCOUNT_NOT_EXIST_ERROR);


    for (Account account : accounts)
      accountTestDao.get().insertIntoTempAccount(account, TMP_ACCOUNT.name());
    for (Transaction transaction : transactions)
      accountTestDao.get().insertIntoTempTransaction(transaction, TMP_TRANSACTION.name());


    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationConfig config = new MigrationConfig();
      MigrationFrsTest migration = new MigrationFrsTest(config, connection);

      tableNames = migration.getTableNames();
      tableNames.put(TMP_ACCOUNT, TMP_ACCOUNT.name());
      tableNames.put(TMP_TRANSACTION, TMP_TRANSACTION.name());

      //
      //
      migration.markErrors();
      //
      //
    }
    List<Account> accountList = accountTestDao.get().getTempAccountList(tableNames.get(TMP_ACCOUNT));
    List<Transaction> transactionList = accountTestDao.get().getTempTransactionList(tableNames.get(TMP_TRANSACTION));

    assertThat(accountList).hasSize(accounts.size());
    assertThat(transactionList).hasSize(transactions.size());

    for (Account target : accountList) {
      if (accountToError.containsKey(target.id)) {
        MigrationError err = accountToError.get(target.id);
        assertThat(target.error).isEqualTo(err.message);
      } else {
        assertThat(target.error).isNull();
      }
    }

    for (Transaction transaction : transactionList) {
      if (transactToError.containsKey(transaction.account_number)) {
        assertThat(transaction.error).isEqualTo(TRANSACTION_ACCOUNT_NOT_EXIST_ERROR.message);
      } else {
        assertThat(transaction.error).isNull();
      }

    }


  }

  @Test
  void upsertIntoDbValidRowsCiaTest() throws Exception {
    dropTempTables();
    clientTestDao.get().clear();
    clientTestDao.get().createTempClientTable(TMP_CLIENT.name());
    clientTestDao.get().createTempPhoneTable(TMP_PHONE.name());
    clientTestDao.get().createTempAddressTable(TMP_ADDRESS.name());

    final int numberOfClients = 100;
    List<Client> clients = rndClients(numberOfClients);
    Set<String> invalidRows = new HashSet<>();


    Client errorRow = clients.get(0);
    Client errorRow2 = clients.get(10);
    Client errorRow3 = clients.get(50);
    Client errorRow4 = clients.get(51);
    errorRow.error = "error";
    errorRow2.error = "error";
    errorRow3.error = "error";
    errorRow4.error = "error";
    invalidRows.add(errorRow.cia_id);
    invalidRows.add(errorRow2.cia_id);
    invalidRows.add(errorRow3.cia_id);
    invalidRows.add(errorRow4.cia_id);

    insertClients(clients);

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {
      MigrationCiaTest migration = new MigrationCiaTest(new MigrationConfig(), connection);
      Map<TmpTableName, String> tableNames;
      tableNames = migration.getTableNames();
      tableNames.put(TMP_CLIENT, TMP_CLIENT.name());
      tableNames.put(TMP_ADDRESS, TMP_ADDRESS.name());
      tableNames.put(TMP_PHONE, TMP_PHONE.name());
      //
      //
      migration.upsertIntoTempTables();
      //
      //
    }

    List<ClientDetail> result = clientTestDao.get().getClientTestList("client");


    assertThat(result).hasSize(clients.size() - invalidRows.size());
    for (ClientDetail detail : result) {
      boolean foundError = invalidRows.contains(detail.id);
      assertThat(foundError).isFalse();
    }

  }

  @Test
  void upsertIntoDbValidRowsFrsTest() throws Exception {
    dropTempTables();
    accountTestDao.get().clear();
    accountTestDao.get().createTempAccountTable(TMP_ACCOUNT.name());
    accountTestDao.get().createTempTransactionTable(TMP_TRANSACTION.name());

    final int numberOfAccounts = 10;
    List<Account> accounts = rndAccounts(numberOfAccounts);
    List<Transaction> transactions = rndTransactions(accounts);
    Set<String> invalidRows = new HashSet<>();

    Account errorRow1 = accounts.get(2);
    Account errorRow2 = accounts.get(5);
    errorRow1.error = "error";
    errorRow2.error = "error";

    invalidRows.add(errorRow1.account_number);
    invalidRows.add(errorRow2.account_number);

    for (Transaction transaction : transactions) {
      if (invalidRows.contains(transaction.account_number)) {
        transaction.error = "error";
      }
    }

    for (Account account : accounts)
      accountTestDao.get().insertIntoTempAccount(account, TMP_ACCOUNT.name());
    for (Transaction transaction : transactions)
      accountTestDao.get().insertIntoTempTransaction(transaction, TMP_TRANSACTION.name());


    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationConfig config = new MigrationConfig();
      MigrationFrsTest migration = new MigrationFrsTest(config, connection);
      Map<TmpTableName, String> tableNames;
      tableNames = migration.getTableNames();
      tableNames.put(TMP_ACCOUNT, TMP_ACCOUNT.name());
      tableNames.put(TMP_TRANSACTION, TMP_TRANSACTION.name());
      //
      //
      migration.upsertIntoTempTables();
      //
      //
    }
    List<String> accountList = accountTestDao.get().getList("clientaccount", "number");
    List<String> transactionList = accountTestDao.get().getList("clientaccounttransaction", "account");

    boolean foundErrorAccount = accountList.stream().anyMatch(invalidRows::contains);
    boolean foundErrorTransaction = transactionList.stream().anyMatch(invalidRows::contains);

    assertThat(foundErrorAccount).isFalse();
    assertThat(foundErrorTransaction).isFalse();
  }

  @Test
  void uploadErrorsCiaTest() {
    throw new NotImplementedException();
  }

  @Test
  void uploadErrorsFrsTest() {
    throw new NotImplementedException();
  }


///////////////////////////////////////////////////////////////////////////////////////////////////////

  private void insertClients(List<Client> clients) {
    for (Client detail : clients) {

      clientTestDao.get().insertClientDetail(detail, TMP_CLIENT.name());
      for (ClientPhoneNumber number : detail.phoneNumbers) {
        clientTestDao.get().insertPhoneIntoTemp(number, TMP_PHONE.name());
      }
      clientTestDao.get().insertAddressIntoTemp(detail.registerAddress, TMP_ADDRESS.name());
      clientTestDao.get().insertAddressIntoTemp(detail.actualAddress, TMP_ADDRESS.name());

    }
  }

  private File generateFrs(List<Account> accounts, List<Transaction> transactions) throws IOException {
    File file = new File(Modules.dbDir() + "/build/temp/file.json.txt");

    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();

    Gson gson = new Gson();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      for (Account account : accounts) {
        writer.write(gson.toJson(account));
        writer.newLine();
      }
      for (Transaction transaction : transactions) {
        writer.write(gson.toJson(transaction));
        writer.newLine();
      }
    }

//    throw new NotImplementedException();
    return file;
  }

  private File genereteCia(List<Client> list) throws Exception {

    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // root elements
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("cia");
    doc.appendChild(rootElement);

    for (Client detail : list) {

      Element client = doc.createElement("client");
      rootElement.appendChild(client);

      Attr attr = doc.createAttribute("id");
      attr.setValue(detail.id);
      client.setAttributeNode(attr);

      client.appendChild(createElement(doc, "name", detail.name));
      client.appendChild(createElement(doc, "surname", detail.surname));
      client.appendChild(createElement(doc, "patronymic", detail.patronymic));
      client.appendChild(createElement(doc, "birth", detail.birthDate));
      client.appendChild(createElement(doc, "gender", detail.gender.toString()));
      client.appendChild(createElement(doc, "charm", detail.charm));

      Element address = doc.createElement("address");
      address.appendChild(createAddressElement(doc, "fact", detail.actualAddress.street, detail.actualAddress.house, detail.actualAddress.flat));
      address.appendChild(createAddressElement(doc, "register", detail.registerAddress.street, detail.registerAddress.house, detail.registerAddress.flat));

      client.appendChild(address);
      for (ClientPhoneNumber number : detail.phoneNumbers) {
        String type = number.type.name().toLowerCase() + "Phone";
        Element numberElem = doc.createElement(type);
        numberElem.setTextContent(number.number);
        client.appendChild(numberElem);
      }
    }

    // write the content into xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    File xml = new File(Modules.dbDir() + "/build/temp/file.xml");
    //noinspection ResultOfMethodCallIgnored
    xml.getParentFile().mkdirs();
    StreamResult result = new StreamResult(xml);

    // Output to console for testing
    // StreamResult result = new StreamResult(System.out);

    transformer.transform(source, result);

    if (!xml.exists())
      throw new Exception("xml file not generated");

    return xml;
  }

  private List<Account> rndAccounts(int n) {
    List<Account> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Account account = new Account();
      account.id = idGenerator.get().newId();
      account.type = "new_account";
      account.account_number = idGenerator.get().newId();
      account.client_id = idGenerator.get().newId();
      account.registered_at = getTimeStampFormat(new Date());
      list.add(account);
    }
    return list;
  }

  private List<Transaction> rndTransactions(List<Account> accounts) {
    List<Transaction> list = new ArrayList<>();
    for (Account account : accounts) {
      for (int i = 0; i < RND.plusInt(5); i++) {
        Transaction transaction = new Transaction();
        transaction.id = idGenerator.get().newId();
        transaction.type = "transaction";
        transaction.money = String.valueOf(RND.plusDouble(100, 2));
        transaction.transaction_type = RND.str(10);
        transaction.account_number = account.account_number;
        transaction.finished_at = getTimeStampFormat(new Date());
        list.add(transaction);
      }
    }
    return list;
  }


  private List<Client> rndClients(int n) {
    List<Client> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Client c = new Client();
      //cia_id
      c.id = idGenerator.get().newId();
      c.cia_id = idGenerator.get().newId();
      c.name = idGenerator.get().newId();
      c.surname = idGenerator.get().newId();
      c.patronymic = idGenerator.get().newId();
      c.charm = RND.str(10);
      c.gender = RND.someEnum(GenderType.values());
      c.birthDate = dateFormat.format(RND.dateYears(-100, -18));

      c.registerAddress = new ClientAddress();
      c.registerAddress.client = c.id;
      c.registerAddress.type = AddressType.REG;
      c.registerAddress.house = RND.str(10);
      c.registerAddress.flat = RND.str(10);
      c.registerAddress.street = RND.str(10);

      c.actualAddress = new ClientAddress();
      c.actualAddress.client = c.id;
      c.actualAddress.type = AddressType.FACT;
      c.actualAddress.house = RND.str(10);
      c.actualAddress.flat = RND.str(10);
      c.actualAddress.street = RND.str(10);

      c.phoneNumbers = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        ClientPhoneNumber number = new ClientPhoneNumber();
        number.client = c.id;
        number.number = RND.str(10);
        number.type = RND.someEnum(PhoneNumberType.values());
        c.phoneNumbers.add(number);
      }

      list.add(c);
    }

    return list;
  }


  private Element createElement(Document doc, String tag, String value) {
    Element elem = doc.createElement(tag);
    Attr attr = doc.createAttribute("value");
    attr.setValue(value);
    elem.setAttributeNode(attr);
    return elem;
  }

  private Element createAddressElement(Document doc, String tag, String street, String house, String flat) {
    Element elem = doc.createElement(tag);

    Attr streetAttr = doc.createAttribute("street");
    Attr houseAttr = doc.createAttribute("house");
    Attr flatAttr = doc.createAttribute("flat");

    streetAttr.setValue(street);
    houseAttr.setValue(house);
    flatAttr.setValue(flat);

    elem.setAttributeNode(streetAttr);
    elem.setAttributeNode(houseAttr);
    elem.setAttributeNode(flatAttr);
    return elem;
  }


  @SuppressWarnings("Duplicates")
  private void assertClient(Client target, Client assertion) {
    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);
    assertThat(target.birthDate).isEqualTo(assertion.birthDate);
    assertThat(target.charm).isEqualTo(assertion.charm);
  }

  private void assertPhoneNumber(ClientPhoneNumber target, ClientPhoneNumber assertion) {
    assertThat(target).isNotNull();
    assertThat(target.number).isEqualTo(assertion.number);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
  private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

  private String getTimeStampFormat(Date date) {
    return dateFormat.format(date) + "T" + timeFormat.format(date);
  }


  private void dropTempTables() {
    clientTestDao.get().dropTable(TMP_CLIENT.name());
    clientTestDao.get().dropTable(TMP_ADDRESS.name());
    clientTestDao.get().dropTable(TMP_PHONE.name());
    clientTestDao.get().dropTable(TMP_ACCOUNT.name());
    clientTestDao.get().dropTable(TMP_TRANSACTION.name());
  }
}
