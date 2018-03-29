package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.input_file_generator.GenerateInputFiles;
import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneType;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static kz.greetgo.sandbox.db.migration_impl.model.ClientTmp.STATUS_DUPLICATED;
import static kz.greetgo.sandbox.db.migration_impl.model.ClientTmp.STATUS_EXISTS;
import static kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber.STATUS_DUPLICATED_PHONE_NUMBER;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link CiaMigrationWorker}
 */
public class CiaMigrationWorkerTest extends ParentTestNg {
  public BeanGetter<MigrationConfig> migrationConfig;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGen;
  public BeanGetter<DbConfig> dbConfig;

  public BeanGetter<MigrationTestDao> migrationTestDao;

  private Connection connection;
  private OutputStream outError;
  private ReportXlsx reportXlsx;

  private static final Random random = new Random();

  @BeforeClass
  public void prepareResources() throws Exception {
    connection = DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    );

    File file = new File(migrationConfig.get().sqlReportDir() + "sqlReportCia.xlsx");
    file.getParentFile().mkdirs();
    reportXlsx = new ReportXlsx(new FileOutputStream(file));
    reportXlsx.start();

    File outErrorFile = new File("build/out_files/error_report.txt");
    outErrorFile.getParentFile().mkdirs();
    outError = new FileOutputStream(outErrorFile);
  }

  @AfterClass
  public void closeResources() throws Exception {
    connection.close();
    connection = null;
  }

  @Test
  public void test_parseDataAndSaveInTmpDb_on_broken_xml() throws Exception {
    String brokenXml =
      "<cia>\n" +
        "  <client id=\"1-V9M-H6-QB-cuQh3C9hy3\"> <!-- 1 -->\n" +
        "    <mobilePhone>+7-338-770-07-55</mobilePhone>\n" +
        "    <name value=\"AлфrГсK3Тc\"/>\n" +
        "  </client>\n" +
        "  <client id=\"1-V9M-H6-QB-cuQh3C9hy3\"> <!-- 2 -->\n" +
        "    <gender value=\"MALE\"/>\n" +
        "    <birth value=\"" +
        "\"" + /*Added parse error to the second client!*/
        "1923-08-22\"/>\n" +
        "    <name value=\"с5ыsЛЧпмЗO\"/>\n" +
        "    <surname value=\"JЧчСЗгGhыд\"/>\n" +
        "  </client>\n" +
        "</cia>\n";

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();
    ciaMigrationWorker.inputStream = new ByteArrayInputStream(brokenXml.getBytes());
    ByteArrayOutputStream errorBytesOutput = new ByteArrayOutputStream();
    ciaMigrationWorker.outError = errorBytesOutput;
    ciaMigrationWorker.createTmpTables();

    //
    //
    ciaMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<ClientTmp> actualClients = migrationTestDao.get().loadClientsList(ciaMigrationWorker.tmpClientTable);

    assertThat(actualClients).isNotNull();
    assertThat(actualClients).hasSize(1);

    assertThat(errorBytesOutput.size()).isGreaterThan(0);

    String errorStr = errorBytesOutput.toString("UTF-8");
    System.out.println(errorStr);
    assertThat(errorStr).contains("lineNumber: 8");
    assertThat(errorStr).contains("Element type");
    assertThat(errorStr).contains("birth");
    assertThat(errorStr).contains("must be followed by");
  }

  @Test
  public void test_creationOfTmpTables() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    //
    //
    ciaMigrationWorker.createTmpTables();
    //
    //
  }

  @Test
  public void test_parsingAndInsertionIntoTmpDb() throws Exception {

    List<ClientTmp> expectedClients = generateGoodUniqueClients(5);
    List<Address> expectedAddresses = new ArrayList<>();
    List<PhoneNumber> expectedPhoneNumbers = new ArrayList<>();

    StringBuilder inputXmlSb = new StringBuilder();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    inputXmlSb.append("<cia>\n");
    int phoneOrdNum = 0;
    for (ClientTmp expectedClient : expectedClients) {
      inputXmlSb.append("<client id=\"").append(expectedClient.cia_id).append("\">\n");
      Address expectedAddressF = generateAddress("FACT", expectedClient.number, expectedClient.cia_id);
      Address expectedAddressR = generateAddress("REG", expectedClient.number, expectedClient.cia_id);
      expectedAddresses.add(expectedAddressF);
      expectedAddresses.add(expectedAddressR);
      inputXmlSb.append("    <address>\n");
      inputXmlSb.append("      <fact street=\"").append(expectedAddressF.street).append("\" house=\"").append(expectedAddressF.house).append("\" flat=\"").append(expectedAddressF.flat).append("\"/>\n");
      inputXmlSb.append("      <register street=\"").append(expectedAddressR.street).append("\" house=\"").append(expectedAddressR.house).append("\" flat=\"").append(expectedAddressR.flat).append("\"/>\n");
      inputXmlSb.append("    </address>\n");
      int phoneCount = 2 + random.nextInt(5);
      for (int j = 0; j < phoneCount; j++) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.number = phoneOrdNum++;
        phoneNumber.client_num = expectedClient.number;
        phoneNumber.type = PhoneType.valueOf(random.nextBoolean() ? "HOME" : random.nextBoolean() ? "MOBILE" : "WORK");
        phoneNumber.phone_number = RND.str(10);
        expectedPhoneNumbers.add(phoneNumber);
        switch (phoneNumber.type) {
          case MOBILE:
            inputXmlSb.append("    <mobilePhone>").append(phoneNumber.phone_number).append("</mobilePhone>\n");
            break;
          case WORK:
            inputXmlSb.append("    <workPhone>").append(phoneNumber.phone_number).append("</workPhone>\n");
            break;
          case HOME:
            inputXmlSb.append("    <homePhone>").append(phoneNumber.phone_number).append("</homePhone>\n");
            break;
        }
        inputXmlSb.append("    <surname value=\"").append(expectedClient.surname).append("\"/>\n");
        inputXmlSb.append("    <name value=\"").append(expectedClient.name).append("\"/>\n");
        inputXmlSb.append("    <patronymic value=\"").append(expectedClient.patronymic).append("\"/>\n");
        inputXmlSb.append("    <gender value=\"").append(expectedClient.gender).append("\"/>\n");
        inputXmlSb.append("    <charm value=\"").append(expectedClient.charm_name).append("\"/>\n");
        inputXmlSb.append("    <birth value=\"").append(sdf.format(expectedClient.birth_date)).append("\"/>\n");
      }
      inputXmlSb.append("</client>\n");
    }
    inputXmlSb.append("</cia>");

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();
    ciaMigrationWorker.inputStream = new ByteArrayInputStream(inputXmlSb.toString().getBytes());
    ciaMigrationWorker.createTmpTables();

    //
    //
    ciaMigrationWorker.parseDataAndSaveInTmpDb();
    //
    //

    List<ClientTmp> actualClients = migrationTestDao.get().loadClientsList(ciaMigrationWorker.tmpClientTable);
    List<Address> actualAddresses = migrationTestDao.get().loadAddressesList(ciaMigrationWorker.tmpAddrTable);
    List<PhoneNumber> actualPhoneNumbers = migrationTestDao.get().loadPhoneNumbersList(ciaMigrationWorker.tmpPhoneTable);

    assertThat(actualClients).isNotNull();
    assertThat(actualClients).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualClients.size(); i++) {
      assertThatAreEqual(actualClients.get(i), expectedClients.get(i));
    }

    assertThat(actualAddresses).isNotNull();
    assertThat(actualAddresses).hasSameSizeAs(expectedAddresses);
    for (int i = 0; i < actualAddresses.size(); i++) {
      assertThatAreEqual(actualAddresses.get(i), expectedAddresses.get(i));
    }

    assertThat(actualPhoneNumbers).isNotNull();
    assertThat(actualPhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    for (int i = 0; i < actualPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void validate_cia_id_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname = new ClientTmp();
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(1);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("cia_id");
  }

  @Test
  public void validate_name_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoName1 = generateGoodClient(0);
    ClientTmp clientNoName2 = generateGoodClient(1);
    clientNoName1.name = null;
    clientNoName2.name = "";
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoName1);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoName2);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("name");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientNoName1.cia_id);
    assertThat(actualList.get(0).error).doesNotContain("surname");

    assertThat(actualList.get(1).error).isNotNull();
    assertThat(actualList.get(1).error).contains("name");
    assertThat(actualList.get(1).error).contains("cia_id = ");
    assertThat(actualList.get(1).error).contains(clientNoName2.cia_id);
    assertThat(actualList.get(1).error).doesNotContain("surname");
  }

  @Test
  public void validate_charm_name_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoCharmName1 = generateGoodClient(0);
    ClientTmp clientNoCharmName2 = generateGoodClient(1);
    clientNoCharmName1.charm_name = null;
    clientNoCharmName2.charm_name = "";
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoCharmName1);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoCharmName2);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("charm is not defined");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientNoCharmName1.cia_id);
    assertThat(actualList.get(0).error).doesNotContain("surname");

    assertThat(actualList.get(1).error).isNotNull();
    assertThat(actualList.get(1).error).contains("charm is not defined");
    assertThat(actualList.get(1).error).contains("cia_id = ");
    assertThat(actualList.get(1).error).contains(clientNoCharmName2.cia_id);
    assertThat(actualList.get(1).error).doesNotContain("surname");
  }

  @Test
  public void validate_if_client_age_is_between_10_and_200() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientTooOld = generateGoodClient(0);
    ClientTmp clientTooYoung = generateGoodClient(1);
    clientTooOld.birth_date = RND.dateYears(-2000, -200);
    clientTooYoung.birth_date = RND.dateYears(-10, 0);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientTooOld);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientTooYoung);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("age");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientTooOld.cia_id);
    assertThat(actualList.get(0).error).doesNotContain("surname");

    assertThat(actualList.get(1).error).isNotNull();
    assertThat(actualList.get(1).error).contains("age");
    assertThat(actualList.get(1).error).contains("cia_id = ");
    assertThat(actualList.get(1).error).contains(clientTooYoung.cia_id);
    assertThat(actualList.get(1).error).doesNotContain("surname");
  }

  @Test
  public void validate_birth_date_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoBirthDate = generateGoodClient(0);
    ClientTmp clientGood = generateGoodClient(1);
    clientNoBirthDate.birth_date = null;
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoBirthDate);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientGood);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("birth_date");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientNoBirthDate.cia_id);
    assertThat(actualList.get(0).error).doesNotContain("surname");

    assertThat(actualList.get(1).error).isNull();
  }

  @Test
  public void validate_surname_is_absent() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname1 = generateGoodClient(0);
    ClientTmp clientNoSurname2 = generateGoodClient(1);
    clientNoSurname1.surname = null;
    clientNoSurname2.surname = "";
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname1);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname2);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("surname");
    assertThat(actualList.get(0).error).contains("cia_id = ");
    assertThat(actualList.get(0).error).contains(clientNoSurname1.cia_id);

    assertThat(actualList.get(1).error).isNotNull();
    assertThat(actualList.get(1).error).contains("surname");
    assertThat(actualList.get(1).error).contains("cia_id = ");
    assertThat(actualList.get(1).error).contains(clientNoSurname2.cia_id);
  }

  @Test
  public void validate_empty_client() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp clientNoSurname = new ClientTmp();
    clientNoSurname.cia_id = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, clientNoSurname);

    //
    //
    ciaMigrationWorker.validateErrors();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(1);
    assertThat(actualList.get(0).error).isNotNull();
    assertThat(actualList.get(0).error).contains("surname");
  }

  @Test
  public void проверка_исключения_дубликатов() throws Exception {
    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp client1 = new ClientTmp();
    client1.number = 123;
    client1.cia_id = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client1);

    ClientTmp client2 = new ClientTmp();
    client2.number = 5435;
    client2.cia_id = client1.cia_id;
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client2);

    //
    //
    ciaMigrationWorker.markDuplicateClientRecords();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);
    assertThat(actualList).hasSize(2);

    assertThat(actualList.get(0).number).isEqualTo(123);
    assertThat(actualList.get(1).number).isEqualTo(5435);

    assertThat(actualList.get(0).status).isEqualTo(STATUS_DUPLICATED);
    assertThat(actualList.get(1).status).isEqualTo(0);
  }

  @Test
  public void test_checkingForClientExistence() throws Exception {
    clientTestDao.get().removeAllData();

    List<ClientTmp> expectedClients = generateGoodUniqueClients(7);

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < expectedClients.size(); i++) {
      expectedClients.get(i).number = i;
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, expectedClients.get(i));
      if (i % 2 == 0) {
        clientTestDao.get().insertClientTmp(expectedClients.get(i));
      }
    }

    //
    //
    ciaMigrationWorker.checkForClientExistence();
    //
    //

    List<ClientTmp> actualList = migrationTestDao.get().selectAll(ciaMigrationWorker.tmpClientTable);

    assertThat(actualList).isNotNull();
    assertThat(actualList).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualList.size(); i++) {
      if (i % 2 == 0) {
        assertThat(actualList.get(i).status).isEqualTo(STATUS_EXISTS);
      } else {
        assertThat(actualList.get(i).status).isEqualTo(0);
      }
    }
  }

  @Test
  public void test_insertCharms() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    ClientTmp c1 = new ClientTmp();
    c1.number = 12;
    c1.charm_name = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, c1);
    charmTestDao.get().insertCharm(RND.str(10), c1.charm_name);

    ClientTmp c2 = new ClientTmp();
    c2.number = 13;
    c2.charm_name = RND.str(10);
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, c2);

    ClientTmp c3 = new ClientTmp();
    c3.number = 15;
    c3.charm_name = c2.charm_name;
    migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, c3);

    //
    //
    ciaMigrationWorker.insertCharms();
    //
    //

    List<String> actualCharmNames = charmTestDao.get().loadCharmNamesSet();

    assertThat(actualCharmNames).hasSize(2);
    assertThat(actualCharmNames).contains(c1.charm_name);
    assertThat(actualCharmNames).contains(c2.charm_name);
  }

  @Test
  public void test_upsertClients() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    List<ClientTmp> expectedClients = generateGoodUniqueClients(5);

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < expectedClients.size(); i++) {
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, expectedClients.get(i));
      if (i % 2 == 0) {
        clientTestDao.get().insertClientTmp(expectedClients.get(i));
      }
    }

    ciaMigrationWorker.checkForClientExistence();
    ciaMigrationWorker.insertCharms();

    //
    //
    ciaMigrationWorker.upsertClients();
    //
    //

    List<ClientTmp> actualUpsertedClients = clientTestDao.get().loadClientList();

    expectedClients.sort(Comparator.comparing(o2 -> o2.surname));
    actualUpsertedClients.sort(Comparator.comparing(o -> o.surname));

    assertThat(actualUpsertedClients).isNotNull();
    assertThat(actualUpsertedClients).hasSameSizeAs(expectedClients);
    for (int i = 0; i < actualUpsertedClients.size(); i++) {
      assertThatAreEqual(actualUpsertedClients.get(i), expectedClients.get(i));
    }
  }

  @Test
  public void test_upsertClientAddress() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    List<Address> expectedAddresses = new ArrayList<>();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    for (int i = 0; i < 8; i++) {
      ClientTmp client = generateGoodClient(i);
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client);
      clientTestDao.get().insertClientTmp(client);

      Address addressF = generateAddress("FACT", i, client.cia_id);
      Address addressR = generateAddress("REG", i, client.cia_id);

      migrationTestDao.get().insertAddress(ciaMigrationWorker.tmpAddrTable, addressF);
      migrationTestDao.get().insertAddress(ciaMigrationWorker.tmpAddrTable, addressR);
      expectedAddresses.add(addressF);
      expectedAddresses.add(addressR);
    }

    //
    //
    ciaMigrationWorker.upsertClientAddress();
    //
    //

    List<Address> actualUpsertedAddresses = clientTestDao.get().loadAddressList();

    assertThat(actualUpsertedAddresses).isNotNull();
    assertThat(actualUpsertedAddresses).hasSameSizeAs(expectedAddresses);
    for (int i = 0; i < actualUpsertedAddresses.size(); i++) {
      assertThatAreEqual(actualUpsertedAddresses.get(i), expectedAddresses.get(i));
    }
  }

  @Test
  public void test_exclusion_of_duplicate_phone_numbers() throws Exception {
    clientTestDao.get().removeAllData();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.number = 67;
    phoneNumber.client_num = 47;
    phoneNumber.type = PhoneType.MOBILE;
    phoneNumber.phone_number = RND.str(10);

    PhoneNumber phoneNumberDuplicate = new PhoneNumber();
    phoneNumberDuplicate.number = 83;
    phoneNumberDuplicate.client_num = phoneNumber.client_num;
    phoneNumberDuplicate.type = PhoneType.HOME;
    phoneNumberDuplicate.phone_number = phoneNumber.phone_number;


    migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumber);
    migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumberDuplicate);

    //
    //
    ciaMigrationWorker.markDuplicatePhoneNumbers();
    //
    //

    List<PhoneNumber> actualUniquePhoneNumbers = migrationTestDao.get().loadUniquePhoneNumbers(ciaMigrationWorker.tmpPhoneTable);

    assertThat(actualUniquePhoneNumbers).isNotNull();
    assertThat(actualUniquePhoneNumbers).hasSize(2);

    assertThat(actualUniquePhoneNumbers.get(0).number).isEqualTo(67);
    assertThat(actualUniquePhoneNumbers.get(1).number).isEqualTo(83);

    assertThat(actualUniquePhoneNumbers.get(0).status).isEqualTo(STATUS_DUPLICATED_PHONE_NUMBER);
    assertThat(actualUniquePhoneNumbers.get(1).status).isEqualTo(0);
  }

  @Test
  public void test_upsertPhoneNumbers() throws Exception {
    clientTestDao.get().removeAllData();

    List<PhoneNumber> expectedPhoneNumbers = new ArrayList<>();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();

    ciaMigrationWorker.createTmpTables();

    int phoneOrdNum = 0;
    for (int client_num = 0; client_num < 8; client_num++) {
      ClientTmp client = generateGoodClient(client_num);
      migrationTestDao.get().insertClient(ciaMigrationWorker.tmpClientTable, client);
      clientTestDao.get().insertClientTmp(client);

      int phoneCount = 2 + random.nextInt(5);
      for (int j = 0; j < phoneCount; j++) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.number = phoneOrdNum++;
        phoneNumber.client_num = client_num;
        phoneNumber.type = PhoneType.valueOf(random.nextBoolean() ? "HOME" : random.nextBoolean() ? "MOBILE" : "WORK");
        phoneNumber.phone_number = RND.str(10);
        migrationTestDao.get().insertPhoneNumber(ciaMigrationWorker.tmpPhoneTable, phoneNumber);
        expectedPhoneNumbers.add(phoneNumber);
      }
    }

    //
    //
    ciaMigrationWorker.upsertPhoneNumbers();
    //
    //

    List<PhoneNumber> actualUpsertedPhoneNumbers = clientTestDao.get().loadPhoneNumberList();

    assertThat(actualUpsertedPhoneNumbers).isNotNull();
    assertThat(actualUpsertedPhoneNumbers).hasSameSizeAs(expectedPhoneNumbers);
    for (int i = 0; i < actualUpsertedPhoneNumbers.size(); i++) {
      assertThatAreEqual(actualUpsertedPhoneNumbers.get(i), expectedPhoneNumbers.get(i));
    }
  }

  @Test
  public void test_total_cia_migration() throws Exception {
    clientTestDao.get().removeAllData();
    charmTestDao.get().removeAllData();

    GenerateInputFiles fileGenerator = new GenerateInputFiles(50, 0);
    fileGenerator.setTestMode();
    fileGenerator.archiveFiles = false;
    fileGenerator.execute();

    Set<String> expectedClientIds = fileGenerator.getGoodClientIds();

    CiaMigrationWorker ciaMigrationWorker = getCiaMigrationWorker();
    ciaMigrationWorker.inputStream = new FileInputStream(fileGenerator.getOutCiaFileName());

    //
    //
    ciaMigrationWorker.migrate();
    //
    //

    Set<String> actualClientCiaIds = clientTestDao.get().getClientCiaIdsSet();

    assertThat(actualClientCiaIds).isEqualTo(expectedClientIds);
  }

  private Address generateAddress(String type, int clientNum, String ciaId) {
    Address ret = new Address();
    ret.type = type;
    ret.cia_id = ciaId;
    ret.client_num = clientNum;
    ret.street = RND.str(20);
    ret.house = RND.str(2);
    ret.flat = RND.str(2);
    return ret;
  }

  private ClientTmp generateGoodClient(int number) {
    ClientTmp ret = new ClientTmp();
    ret.number = number;
    ret.cia_id = idGen.get().newId();
    ret.surname = RND.str(10);
    ret.name = RND.str(10);
    ret.patronymic = RND.str(10);
    ret.gender = random.nextBoolean() ? "MALE" : "FEMALE";
    ret.birth_date = RND.dateYears(-100, -18);
    ret.charm_name = RND.str(10);
    return ret;
  }

  private List<ClientTmp> generateGoodUniqueClients(int size) {
    List<ClientTmp> ret = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      ret.add(generateGoodClient(i));
    }
    return ret;
  }

  private void assertThatAreEqual(PhoneNumber actualPhoneNumber, PhoneNumber expectedPhoneNumber) {
    assertThat(actualPhoneNumber.type).isEqualTo(expectedPhoneNumber.type);
    assertThat(actualPhoneNumber.phone_number).isEqualTo(expectedPhoneNumber.phone_number);
  }

  private void assertThatAreEqual(Address actualAddress, Address expectedAddress) {
    assertThat(actualAddress.type).isEqualTo(expectedAddress.type);
    assertThat(actualAddress.street).isEqualTo(expectedAddress.street);
    assertThat(actualAddress.house).isEqualTo(expectedAddress.house);
    assertThat(actualAddress.flat).isEqualTo(expectedAddress.flat);
  }

  private void assertThatAreEqual(ClientTmp actualClient, ClientTmp expectedClient) {
    assertThat(actualClient.cia_id).isEqualTo(expectedClient.cia_id);
    assertThat(actualClient.name).isEqualTo(expectedClient.name);
    assertThat(actualClient.surname).isEqualTo(expectedClient.surname);
    assertThat(actualClient.patronymic).isEqualTo(expectedClient.patronymic);
    assertThat(actualClient.gender).isEqualTo(expectedClient.gender);
    assertThat(actualClient.charm_name).isEqualTo(expectedClient.charm_name);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(actualClient.birth_date != null ? sdf.format(actualClient.birth_date) : null)
      .isEqualTo(expectedClient.birth_date != null ? sdf.format(expectedClient.birth_date) : null);
  }

  private CiaMigrationWorker getCiaMigrationWorker() {
    CiaMigrationWorker ciaMigrationWorker = new CiaMigrationWorker(connection);
    ciaMigrationWorker.outError = outError;
    ciaMigrationWorker.reportXlsx = reportXlsx;
    ciaMigrationWorker.maxBatchSize = migrationConfig.get().maxBatchSize();
    return ciaMigrationWorker;
  }

}