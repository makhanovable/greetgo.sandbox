package kz.greetgo.sandbox.db.register_impl;

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
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import kz.greetgo.sandbox.db.register_impl.migration_test.MigrationCiaTest;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
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
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_ADDRESS;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_CLIENT;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_PHONE;
import static org.fest.assertions.api.Assertions.assertThat;


public class MigrationRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<MigrationRegister> migrationRegister;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<DbConfig> dbConfig;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> clientTestDao;
  @SuppressWarnings("WeakerAccess")
  public final Logger logger = Logger.getLogger("MIGRATION.TEST");

  @Test
  void test() throws Exception {
    //TODO delete
    this.migrationRegister.get().migrate();
  }

  @Test
  void insertToTempTablesCiaTest() throws Exception {
    clientTestDao.get().clear();
    int numberOfClients = 10;
    List<ClientDetail> list = rndClientDetails(numberOfClients);
    File testData = genereateCia(list);

    MigrationConfig config = new MigrationConfig();
    config.toMigrate = testData;
    config.idGenerator = idGenerator.get();
    config.id = idGenerator.get().newId();


    Map<TmpTableName, String> tableNames;
    try (Connection connection = DbUtils.getPostgresConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {

      MigrationCiaTest migration = new MigrationCiaTest(config, connection);
      migration.createTempTables();
      tableNames = migration.getTableNames();
      //
      //
      migration.parseAndInsertRows();
      //
      //
    }

    List<ClientDetail> result = clientTestDao.get().getDetailList(tableNames.get(TMP_CLIENT));

    for (int i = 0; i < result.size(); i++) {
      ClientDetail target = result.get(i);
      ClientDetail assertion = list.get(i);

      assertClientDetail(target, assertion);
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

//    for (String tableName : tableNames.values())
//      clientTestDao.get().dropTable(tableName);
    if (!testData.delete()) {
      logger.warn("test tmp file not deleted:" + testData.getAbsoluteFile());
    }

  }

  @Test
  void insertToTempTablesFrsTest() {

    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  void makeInvalidRows(List<ClientDetail> list, int invalidRows) {
    if (invalidRows > list.size())
      throw new IllegalArgumentException();

    //TODO make invalid
    throw new NotImplementedException();
  }


  private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


  private File genereateCia(List<ClientDetail> list) throws Exception {

    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // root elements
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("cia");
    doc.appendChild(rootElement);

    for (ClientDetail detail : list) {

      Element client = doc.createElement("client");
      rootElement.appendChild(client);

      Attr attr = doc.createAttribute("id");
      attr.setValue(detail.id);
      client.setAttributeNode(attr);

      client.appendChild(createElement(doc, "name", detail.name));
      client.appendChild(createElement(doc, "surname", detail.surname));
      client.appendChild(createElement(doc, "patronymic", detail.patronymic));
      client.appendChild(createElement(doc, "birth", dateFormatter.format(detail.birthDate)));
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
    File xml = new File(Modules.dbDir() + "build/temp/file.xml");
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

  @SuppressWarnings("unused")
  File genereateFrs(int validRows, int invalidRows) {
    throw new NotImplementedException();
  }

  private List<ClientDetail> rndClientDetails(int n) {
    List<ClientDetail> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      ClientDetail c = new ClientDetail();
      //cia_id
      c.id = idGenerator.get().newId();
      c.name = idGenerator.get().newId();
      c.surname = idGenerator.get().newId();
      c.patronymic = idGenerator.get().newId();
      c.charm = RND.str(10);
      c.gender = RND.someEnum(GenderType.values());
      c.birthDate = RND.dateYears(-100, -18);

      c.registerAddress = new ClientAddress();
      c.registerAddress.type = AddressType.REG;
      c.registerAddress.house = RND.str(10);
      c.registerAddress.flat = RND.str(10);
      c.registerAddress.street = RND.str(10);

      c.actualAddress = new ClientAddress();
      c.actualAddress.type = AddressType.FACT;
      c.actualAddress.house = RND.str(10);
      c.actualAddress.flat = RND.str(10);
      c.actualAddress.street = RND.str(10);

      c.phoneNumbers = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        ClientPhoneNumber number = new ClientPhoneNumber();
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
  private void assertClientDetail(ClientDetail target, ClientDetail assertion) {
    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    assertThat(sdf.format(target.birthDate)).isEqualTo(sdf.format(assertion.birthDate));
    assertThat(target.charm).isEqualTo(assertion.charm);
  }

  private void assertPhoneNumber(ClientPhoneNumber target, ClientPhoneNumber assertion) {
    assertThat(target).isNotNull();
    assertThat(target.number).isEqualTo(assertion.number);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  @SuppressWarnings("Duplicates")
  private void assertClientAddress(ClientAddress target, ClientAddressDot assertion) {
    assertThat(target).isNotNull();
    assertThat(target.street).isEqualTo(assertion.street);
    assertThat(target.house).isEqualTo(assertion.house);
    assertThat(target.flat).isEqualTo(assertion.flat);
    assertThat(target.type).isEqualTo(assertion.type);
  }


}
