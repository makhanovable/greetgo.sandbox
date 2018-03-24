package kz.greetgo.sandbox.db.register_impl.migration;

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
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.model.ClientCia;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.util.DbUtils;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationError.*;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationCiaTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<DbConfig> dbConfig;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> clientTestDao;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<MigrationRegister> migrationRegister;


  @SuppressWarnings("WeakerAccess")
  public final Logger logger = Logger.getLogger("MIGRATION.TEST");

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

  @Test
  void main() throws Exception {
    migrationRegister.get().migrate();
  }

  @Test
  void insertIntoTempTablesCiaTest() throws Exception {

    clientTestDao.get().clear();
    int numberOfClients = 10;
    List<ClientCia> list = rndClients(numberOfClients);
    File testData = generateCia(list);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCia migration = new MigrationCia(config, connection);
      tableNames = migration.tableNames;

      //
      //
      migration.parseFileAndUploadToTempTables();
      //
      //
    }

    List<ClientCia> result = clientTestDao.get().getTempClientList(tableNames.get(TMP_CLIENT));

    assertThat(result).hasSize(list.size());
    for (int i = 0; i < result.size(); i++) {
      ClientCia target = result.get(i);
      ClientCia assertion = list.get(i);

      assertClient(target, assertion);
      target.phoneNumbers = clientTestDao.get().getNumberList(tableNames.get(TMP_PHONE), target.id);

      assertThat(target.phoneNumbers).hasSize(assertion.phoneNumbers.size());

      for (int j = 0; j < target.phoneNumbers.size(); j++) {
        assertThat(target.phoneNumbers.get(j)).isEqualTo(target.phoneNumbers.get(j));
      }

      List<ClientAddress> addresses = clientTestDao.get().getAddressList(tableNames.get(TMP_ADDRESS), target.id);
      assertThat(addresses).hasSize(2);
      assertThat(addresses.get(0)).isEqualTo(assertion.registerAddress);
      assertThat(addresses.get(1)).isEqualTo(assertion.actualAddress);

    }

    if (!testData.delete()) {
      logger.warn("test tmp file not deleted:" + testData.getAbsoluteFile());
    }
    dropTempTables(tableNames);
  }

  @Test
  void markErrorsCiaTest() throws Exception {

    clientTestDao.get().clear();

    final int numberOfClients = 100;
    List<ClientCia> clients = rndClients(numberOfClients);

    Map<String, MigrationError> clientToError = new HashMap<>();

    ClientCia errorRow1 = clients.get(1);
    ClientCia errorRow2 = clients.get(2);
    ClientCia errorRow3 = clients.get(3);
    ClientCia errorRow4 = clients.get(4);
    ClientCia errorRow5 = clients.get(5);
    ClientCia errorRow6 = clients.get(6);
    ClientCia errorRow7 = clients.get(7);
    ClientCia errorRow8 = clients.get(8);
    ClientCia errorRow9 = clients.get(9);

    errorRow1.name = null;
    clientToError.put(errorRow1.id, NAME_ERROR);
    errorRow2.surname = null;
    clientToError.put(errorRow2.id, SURNAME_ERROR);
    errorRow3.birthDate = null;
    clientToError.put(errorRow3.id, BIRTH_NULL_ERROR);
    errorRow4.birthDate = dateFormat.format(RND.dateYears(-1000, -200));
    clientToError.put(errorRow4.id, AGE_ERROR);
    errorRow5.name = "   ";
    clientToError.put(errorRow5.id, NAME_EMPTY_ERROR);
    errorRow6.surname = "      ";
    clientToError.put(errorRow6.id, SURNAME_EMPTY_ERROR);
    errorRow7.birthDate = "sdczsdcsd";
    clientToError.put(errorRow7.id, DATE_INVALID_ERROR);
    errorRow8.charm = null;
    clientToError.put(errorRow8.id, CHARM_ERROR);
    errorRow9.cia_id = null;
    clientToError.put(errorRow9.id, CIA_ID_ERROR);


    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    Map<TmpTableName, String> tableNames;

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCia migration = new MigrationCia(config, connection);
      tableNames = migration.tableNames;
      insertClients(clients, tableNames);

      //
      //
      migration.markErrorRows();
      //
      //
    }

    List<ClientCia> result = clientTestDao.get().getTempClientList(tableNames.get(TMP_CLIENT));
    assertThat(result).hasSize(numberOfClients);
    for (ClientCia client : result) {
      if (clientToError.containsKey(client.id)) {
        MigrationError err = clientToError.get(client.id);
        assertThat(client.error).isEqualTo(err.message);
      } else {
        assertThat(client.error).isNull();
      }
    }

    dropTempTables(tableNames);
  }


  @Test
  void upsertIntoDbValidRowsCiaTest() throws Exception {

    clientTestDao.get().clear();

    final int numberOfClients = 100;
    List<ClientCia> clients = rndClients(numberOfClients);
    Set<String> invalidRows = new HashSet<>();


    ClientCia errorRow = clients.get(0);
    ClientCia errorRow2 = clients.get(10);
    ClientCia errorRow3 = clients.get(50);
    ClientCia errorRow4 = clients.get(51);
    errorRow.error = "error";
    errorRow2.error = "error";
    errorRow3.error = "error";
    errorRow4.error = "error";
    invalidRows.add(errorRow.cia_id);
    invalidRows.add(errorRow2.cia_id);
    invalidRows.add(errorRow3.cia_id);
    invalidRows.add(errorRow4.cia_id);


    Map<TmpTableName, String> tableNames;
    MigrationConfig config = new MigrationConfig();
    config.id = idGenerator.get().newId();
    config.idGenerator = idGenerator.get();

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {
      MigrationCia migration = new MigrationCia(config, connection);
      tableNames = migration.tableNames;
      insertClients(clients, tableNames);

      //
      //
      migration.upsertIntoDbValidRows();
      //
      //
    }

    List<ClientDetail> result = clientTestDao.get().getClientTestList("client");

    assertThat(result).hasSize(clients.size() - invalidRows.size());
    for (ClientDetail detail : result) {
      boolean foundError = invalidRows.contains(detail.id);
      assertThat(foundError).isFalse();
    }

    dropTempTables(tableNames);
  }


  @Test
  void uploadErrorsCiaTest() throws Exception {


    clientTestDao.get().clear();

    final int numberOfClients = 100;
    List<ClientCia> clients = rndClients(numberOfClients);
    Set<String> invalidRows = new HashSet<>();

    ClientCia errorRow = clients.get(0);
    ClientCia errorRow2 = clients.get(10);
    ClientCia errorRow3 = clients.get(50);
    ClientCia errorRow4 = clients.get(51);
    errorRow.error = "error";
    errorRow2.error = "error";
    errorRow3.error = "error";
    errorRow4.error = "error";
    invalidRows.add(errorRow.cia_id);
    invalidRows.add(errorRow2.cia_id);
    invalidRows.add(errorRow3.cia_id);
    invalidRows.add(errorRow4.cia_id);


    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();

    config.error = new File(Modules.dbDir() + "/build/temp/file" + idGenerator.get().newId());
    //noinspection ResultOfMethodCallIgnored
    config.error.getParentFile().mkdirs();

    Map<TmpTableName, String> tableNames;

    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {
      MigrationCia migration = new MigrationCia(config, connection);
      tableNames = migration.tableNames;

      insertClients(clients, tableNames);
      //
      //
      migration.loadErrorsAndWrite();
      //
      //
    }

    int errorCount = 0;
    final Pattern pattern = Pattern.compile("cia_id: (.+?);");
    //noinspection Duplicates
    try (FileReader reader = new FileReader(config.error);
         BufferedReader br = new BufferedReader(reader)) {
      String line;
      while ((line = br.readLine()) != null) {
        final Matcher matcher = pattern.matcher(line);
        assertThat(matcher.find()).isTrue();
        String cia_id = matcher.group(1);
        assertThat(invalidRows.contains(cia_id)).isTrue();
        errorCount++;
      }
    }

    assertThat(errorCount).isEqualTo(invalidRows.size());

    if (!config.error.delete()) {
      logger.warn("temp file not deleted" + config.error.getAbsolutePath());
    }

    dropTempTables(tableNames);
  }


  @Test
  void ciaFullMigrateTest() throws Exception {

    clientTestDao.get().clear();
    int numberOfClients = 10;
    List<ClientCia> list = rndClients(numberOfClients);

    ClientCia errorRow = list.get(0);
    errorRow.name = null;

    File testData = generateCia(list);
    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();
    config.error = new File(Modules.dbDir() + "/build/temp/file" + idGenerator.get().newId());
    //noinspection ResultOfMethodCallIgnored
    config.error.getParentFile().mkdirs();

    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCia migration = new MigrationCia(config, connection);
      tableNames = migration.tableNames;

      //
      //
      migration.migrate();
      //
      //
    }

    List<ClientCia> result = clientTestDao.get().getTempClientListWithErrors(tableNames.get(TMP_CLIENT));
    assertThat(result).hasSize(1);
    assertThat(config.error).exists();
    try (Stream<String> stream = Files.lines(Paths.get(config.error.getAbsolutePath()))) {
      long lineCount = stream.count();
      assertThat(lineCount).isEqualTo(1);
    }

    List<ClientDetail> clientList = clientTestDao.get().getClientTestList("client");
    assertThat(clientList).hasSize(9);

    if (!config.error.delete()) {
      logger.warn("temp file not deleted" + config.error.getAbsolutePath());
    }

    dropTempTables(tableNames);

  }

  ////////////////////////////////////////////////////////////////////////////


  private void insertClients(List<ClientCia> clients, Map<TmpTableName, String> tableNames) {
    for (ClientCia detail : clients) {

      clientTestDao.get().insertClientDetail(detail, tableNames.get(TMP_CLIENT));

      for (ClientPhoneNumber number : detail.phoneNumbers) {
        clientTestDao.get().insertPhoneIntoTemp(number, tableNames.get(TMP_PHONE));
      }
      clientTestDao.get().insertAddressIntoTemp(detail.registerAddress, tableNames.get(TMP_ADDRESS));
      clientTestDao.get().insertAddressIntoTemp(detail.actualAddress, tableNames.get(TMP_ADDRESS));

    }
  }


  @SuppressWarnings("Duplicates")
  private void dropTempTables(Map<TmpTableName, String> tableNames) {
    clientTestDao.get().dropTable(tableNames.get(TMP_CLIENT));
    clientTestDao.get().dropTable(tableNames.get(TMP_ADDRESS));
    clientTestDao.get().dropTable(tableNames.get(TMP_PHONE));
  }

  @SuppressWarnings("Duplicates")
  private void assertClient(ClientCia target, ClientCia assertion) {
    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);
    assertThat(target.birthDate).isEqualTo(assertion.birthDate);
    assertThat(target.charm).isEqualTo(assertion.charm);
  }


  private List<ClientCia> rndClients(int n) {
    List<ClientCia> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      ClientCia c = new ClientCia();
      //cia_id
      c.id = idGenerator.get().newId();
      c.cia_id = idGenerator.get().newId();
      c.name = idGenerator.get().newId();
      c.surname = idGenerator.get().newId();
      c.patronymic = idGenerator.get().newId();
      c.charm = RND.str(10);
      c.gender = RND.someEnum(GenderType.values());
      c.birthDate = dateFormat.format(RND.dateYears(-50, -20));

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

  private File generateCia(List<ClientCia> list) throws Exception {

    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // root elements
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("cia");
    doc.appendChild(rootElement);

    for (ClientCia detail : list) {

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


    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    File xml = new File(Modules.dbDir() + "/build/temp/file.xml");
    //noinspection ResultOfMethodCallIgnored
    xml.getParentFile().mkdirs();
    StreamResult result = new StreamResult(xml);
    // StreamResult result = new StreamResult(System.out);

    transformer.transform(source, result);
    if (!xml.exists())
      throw new Exception("xml file not generated");

    return xml;
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


}
