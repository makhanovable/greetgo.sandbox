package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrateOneCiaFileTest extends MigrateCommonTests {

  @Test
  public void prepareTmpTables() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();
  }

  @Test
  public void uploadData() throws Exception {
    String generatedId = Util.generateRandomString(16);
    File inFile = new File("build/MigrateOneCiaFileTest/cia_" + generatedId + ".xml");
    inFile.getParentFile().mkdirs();
    createCiaFile(inFile);
    File outputErrorFile = new File("build/MigrateOneCiaFileTest/cia_error_" + generatedId + ".txt");

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.inputStream = new FileInputStream(inFile);
    oneCiaFile.outputErrorFile = new CommonErrorFileWriter(outputErrorFile);
    oneCiaFile.prepareTmpTables();
    oneCiaFile.uploadData();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " ORDER BY record_no");
    assertThat(recordList).hasSize(2);
    assertThat(recordList.get(0).get("cia_id")).isEqualTo("4-DU8-32-H7");
    assertThat(recordList.get(1).get("cia_id")).isEqualTo("4-DU8-32-ss");
    assertThat(recordList.get(0).get("surname")).isEqualTo("Иванов");
    assertThat(recordList.get(1).get("surname")).isEqualTo("Петров");
    assertThat(recordList.get(0).get("name")).isEqualTo("Иван");
    assertThat(recordList.get(1).get("name")).isEqualTo("Пётр");
    assertThat(recordList.get(0).get("patronymic")).isEqualTo("Иваныч");
    assertThat(recordList.get(1).get("patronymic")).isEqualTo("Петрович");
    assertThat(recordList.get(0).get("gender")).isEqualTo("MALE");
    assertThat(recordList.get(1).get("gender")).isEqualTo("MALE");
    assertThat(recordList.get(0).get("birth_date").toString()).isEqualTo("1980-11-12");
    assertThat(recordList.get(1).get("birth_date").toString()).isEqualTo("1980-11-13");
    assertThat(recordList.get(0).get("charm_name")).isEqualTo("Уситчивый");
    assertThat(recordList.get(1).get("charm_name")).isEqualTo("Агрессивный");
    assertThat(recordList.get(0).get("charm_name")).isEqualTo("Уситчивый");
    assertThat(recordList.get(1).get("charm_name")).isEqualTo("Агрессивный");

    List<Map<String, Object>> addressRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientAddressTableName + " ORDER BY record_no");
    assertThat(addressRecordList).hasSize(4);
    int rowNum = 0;
    assertThat(addressRecordList.get(rowNum++).get("type")).isEqualTo(AddressType.FACTUAL.name());
    assertThat(addressRecordList.get(rowNum++).get("type")).isEqualTo(AddressType.REGISTRATION.name());
    assertThat(addressRecordList.get(rowNum++).get("type")).isEqualTo(AddressType.FACTUAL.name());
    assertThat(addressRecordList.get(rowNum).get("type")).isEqualTo(AddressType.REGISTRATION.name());

    rowNum = 0;
    assertThat(addressRecordList.get(rowNum++).get("street")).isEqualTo("Панфилова");
    assertThat(addressRecordList.get(rowNum++).get("street")).isEqualTo("Панфилова");
    assertThat(addressRecordList.get(rowNum++).get("street")).isEqualTo("Никонова");
    assertThat(addressRecordList.get(rowNum).get("street")).isEqualTo("Панфилова");
    rowNum = 0;
    assertThat(addressRecordList.get(rowNum++).get("house")).isEqualTo("23A");
    assertThat(addressRecordList.get(rowNum++).get("house")).isEqualTo("23A");
    assertThat(addressRecordList.get(rowNum++).get("house")).isEqualTo("6");
    assertThat(addressRecordList.get(rowNum).get("house")).isEqualTo("13A");
    rowNum = 0;
    assertThat(addressRecordList.get(rowNum++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(rowNum++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(rowNum++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(rowNum).get("flat")).isEqualTo("12");

    List<Map<String, Object>> phoneRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientPhoneTableName + " ORDER BY record_no");
    assertThat(phoneRecordList).hasSize(12);
    rowNum = 0;
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.HOME.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-22-33");
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-33-33");
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-44-33");
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-55-33");
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.WORK.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-00-33 вн. 3344");
    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.WORK.name());
    assertThat(phoneRecordList.get(rowNum++).get("number")).isEqualTo("+7-123-111-00-33 вн. 3343");

    assertThat(phoneRecordList.get(rowNum).get("type")).isEqualTo(PhoneType.HOME.name());
    assertThat(phoneRecordList.get(rowNum).get("number")).isEqualTo("+7-123-333-22-33");
  }

  @Test
  public void processValidationErrors() throws Exception {
    String generatedId = Util.generateRandomString(16);
    File inFile = new File("build/MigrateOneCiaFileTest/cia_" + generatedId + ".xml");
    inFile.getParentFile().mkdirs();
    createCiaFileWithErrors(inFile);

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.inputStream = new FileInputStream(inFile);
    oneCiaFile.outputErrorFile = new ErrorFileWriterTest();
    oneCiaFile.prepareTmpTables();
    oneCiaFile.uploadData();

    List<String> errorList = ((ErrorFileWriterTest) oneCiaFile.outputErrorFile).errorList;
    assertThat(errorList).hasSize(7);
    assertThat(errorList.get(0)).startsWith("Пустое значение surname");
    assertThat(errorList.get(1)).startsWith("Пустое значение name");
    assertThat(errorList.get(2)).startsWith("Пустое значение birth");
    assertThat(errorList.get(3)).startsWith("Неправильный формат birth");
    assertThat(errorList.get(4)).startsWith("Значение birth выходит за рамки");
    assertThat(errorList.get(5)).startsWith("Значение birth выходит за рамки");
    assertThat(errorList.get(6)).startsWith("Значение birth выходит за рамки");
  }

  @Test
  public void migrateData_checkForDuplicatesOfTmpClient() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> expectedCiaIdList = new ArrayList<>();
    List<Long> expectedClientRecordNumList = new ArrayList<>();

    String ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);

    expectedClientRecordNumList.add(clientRecordNum);
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, ciaId, 0);
    expectedCiaIdList.add(ciaId);

    this.insertTmpClientWithError(oneCiaFile.tmpClientTableName, clientRecordNum++);

    expectedClientRecordNumList.add(clientRecordNum);
    ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);
    expectedCiaIdList.add(ciaId);

    ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);
    for (int i = 0; i < 3; i++)
      this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, ciaId, 0);
    expectedClientRecordNumList.add(clientRecordNum);
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum, ciaId, 0);
    expectedCiaIdList.add(ciaId);

    oneCiaFile.migrateData_checkForDuplicatesOfTmpClient();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 1 ORDER BY record_no ASC");

    assertThat(recordList.size()).isEqualTo(expectedCiaIdList.size());
    assertThat(recordList.size()).isEqualTo(expectedClientRecordNumList.size());
    int rowNum = 0;
    for (int i = 0; i < expectedCiaIdList.size(); i++) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(expectedCiaIdList.get(i));
      assertThat(recordList.get(rowNum).get("record_no")).isEqualTo(expectedClientRecordNumList.get(i));
      rowNum++;
    }
  }

  @Test
  public void migrateData_checkForDuplicatesOfTmpClientPhoneTable() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    long clientPhoneRecordNum = 0;
    List<Long> expectedClientPhoneRecordNumList = new ArrayList<>();
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);

    expectedClientPhoneRecordNumList.add(clientPhoneRecordNum);
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11), RND.str(10), 0);

    String number = RND.str(11);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientPhone(
        oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, number, RND.str(10), 0);
    expectedClientPhoneRecordNumList.add(clientPhoneRecordNum);
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, number, RND.str(10), 0);

    number = RND.str(11);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientPhone(
        oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, number, RND.str(10), 0);
    expectedClientPhoneRecordNumList.add(clientPhoneRecordNum);
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum, clientRecordNum, number, RND.str(10), 0);

    oneCiaFile.migrateData_checkForDuplicatesOfTmpClientPhoneTable();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientPhoneTableName + " WHERE status = 1 ORDER BY record_no ASC");

    assertThat(recordList.size()).isEqualTo(expectedClientPhoneRecordNumList.size());
    int rowNum = 0;
    for (int i = 0; i < recordList.size(); i++) {
      assertThat(recordList.get(rowNum).get("record_no")).isEqualTo(expectedClientPhoneRecordNumList.get(i));
      rowNum++;
    }
  }

  @Test
  public void migrateData_finalOfCharmTable() throws Exception {
    resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> expectedCharmNameList = new ArrayList<>();

    String charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 1);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 1);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 1);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 1);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 1);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum, charmName, 1);

    oneCiaFile.migrateData_finalOfCharmTable();

    List<Map<String, Object>> charmRecordList = toListMap("SELECT * FROM charm WHERE id >= 16 ORDER BY id ASC");

    assertThat(charmRecordList.size()).isEqualTo(expectedCharmNameList.size());
    for (Map<String, Object> charmRecord : charmRecordList)
      assertThat(charmRecord.get("name")).isIn(expectedCharmNameList);
  }

  @Test
  public void migrateData_finalOfClientTable() throws Exception {
    resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    createClient(null);

    long clientRecordNum = 0;
    createClientWithTmp(oneCiaFile.tmpClientTableName, clientRecordNum++, 1);

    String charmName = RND.str(16);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, "2000-10-10", 1);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, "1989-05-05", 1);

    charmName = RND.str(16);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum, charmName, "2010-11-04", 1);

    oneCiaFile.migrateData_checkForExistingRecordsOfClientTable();
    oneCiaFile.migrateData_finalOfClientTable();
    oneCiaFile.migrateData_close();

    List<Map<String, Object>> tempRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 4 ORDER BY id ASC");
    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM client WHERE migration_cia_id IS NOT NULL ORDER BY id ASC");

    assertThat(recordList.size()).isEqualTo(tempRecordList.size());
    for (int i = 0; i < recordList.size(); i++) {
      assertThat(recordList.get(i).get("migration_cia_id")).isEqualTo(tempRecordList.get(i).get("cia_id"));
      assertThat(recordList.get(i).get("id")).isEqualTo(tempRecordList.get(i).get("id"));
      assertThat(recordList.get(i).get("surname")).isEqualTo(tempRecordList.get(i).get("surname"));
      assertThat(recordList.get(i).get("name")).isEqualTo(tempRecordList.get(i).get("name"));
      assertThat(recordList.get(i).get("patronymic")).isEqualTo(tempRecordList.get(i).get("patronymic"));
      assertThat(recordList.get(i).get("gender")).isEqualTo(tempRecordList.get(i).get("gender"));
      assertThat(recordList.get(i).get("birth_date").toString());
      assertThat((int) recordList.get(i).get("actual")).isEqualTo(1);
    }
  }

  @Test
  public void migrateData_finalOfClientAddressTable() throws Exception {
    resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0, clientAddressRecordNum = 0;
    int expectedAddressListSize = 0;
    long clientId = createClient(RND.str(16));
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.FACTUAL.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.REGISTRATION.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum++, clientId, 3);
    clientId = createClient(RND.str(16));
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum, clientRecordNum,
      AddressType.FACTUAL.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum, clientId, 3);

    oneCiaFile.migrateData_finalOfClientAddressTable();

    List<Map<String, Object>> tempAddressRecordList =
      toListMap("SELECT cl.id, adr.type, adr.street, adr.house, adr.flat " +
        "FROM " + oneCiaFile.tmpClientAddressTableName + " AS adr " +
        "JOIN " + oneCiaFile.tmpClientTableName + " AS cl ON adr.client_record_no = cl.record_no " +
        "WHERE cl.status = 3 " +
        "ORDER BY adr.record_no ASC");
    List<Map<String, Object>> addressRecordList = toListMap("SELECT * FROM client_addr ORDER BY client ASC");

    assertThat(addressRecordList.size()).isEqualTo(tempAddressRecordList.size());
    assertThat(addressRecordList.size()).isEqualTo(expectedAddressListSize);
    for (int i = 0; i < addressRecordList.size(); i++) {
      assertThat(addressRecordList.get(i).get("client")).isEqualTo(tempAddressRecordList.get(i).get("id"));
      assertThat(addressRecordList.get(i).get("type")).isEqualTo(tempAddressRecordList.get(i).get("type"));
      assertThat(addressRecordList.get(i).get("street")).isEqualTo(tempAddressRecordList.get(i).get("street"));
      assertThat(addressRecordList.get(i).get("house")).isEqualTo(tempAddressRecordList.get(i).get("house"));
      assertThat(addressRecordList.get(i).get("flat")).isEqualTo(tempAddressRecordList.get(i).get("flat"));
    }
  }

  @Test
  public void migrateData_finalOfClientPhoneTable() throws Exception {
    resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0, clientPhoneRecordNum = 0;
    int expectedPhoneListSize = 0;
    long clientId = createClient(RND.str(16));
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11), RND.str(10), 1);
    expectedPhoneListSize++;
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11), RND.str(10), 1);
    expectedPhoneListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum++, clientId, 3);

    clientId = createClient(RND.str(16));
    String number = RND.str(11);
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, number, RND.str(11), 1);
    expectedPhoneListSize++;
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum, clientRecordNum, number, RND.str(11), 0);
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum, clientId, 3);

    oneCiaFile.migrateData_finalOfClientPhoneTable();

    List<Map<String, Object>> tempPhoneRecordList =
      toListMap("SELECT cl.id, ph.number, ph.type " +
        "FROM " + oneCiaFile.tmpClientPhoneTableName + " AS ph, " + oneCiaFile.tmpClientTableName + " AS cl " +
        "WHERE ph.client_record_no = cl.record_no AND cl.status = 3 AND ph.status = 1 " +
        "ORDER BY cl.id ASC");
    List<Map<String, Object>> phoneRecordList = toListMap("SELECT * FROM client_phone ORDER BY client ASC");

    assertThat(phoneRecordList.size()).isEqualTo(tempPhoneRecordList.size());
    assertThat(phoneRecordList.size()).isEqualTo(expectedPhoneListSize);
    for (int i = 0; i < phoneRecordList.size(); i++) {
      assertThat(phoneRecordList.get(i).get("client")).isEqualTo(tempPhoneRecordList.get(i).get("id"));
      assertThat(phoneRecordList.get(i).get("number")).isEqualTo(tempPhoneRecordList.get(i).get("number"));
      assertThat(phoneRecordList.get(i).get("type")).isEqualTo(tempPhoneRecordList.get(i).get("type"));
    }
  }

  @Test
  public void migrateData_withoutDataUpload() throws Exception {
    resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0, clientAddressRecordNum = 0, clientPhoneRecordNum = 0;
    int expectedClientCount = 0, expectedClientAddressCount = 0, expectedClientPhoneCount = 0;
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.FACTUAL.name(), RND.str(10), RND.str(10), RND.str(10));
    expectedClientAddressCount++;
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.REGISTRATION.name(), RND.str(10), RND.str(10), RND.str(10));
    expectedClientAddressCount++;
    this.insertTmpClientPhone(oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11),
      PhoneType.HOME.name(), 0);
    expectedClientPhoneCount++;
    this.insertTmpClientPhone(oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11),
      PhoneType.WORK.name(), 0);
    expectedClientPhoneCount++;
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);
    expectedClientCount++;

    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum, clientRecordNum,
      AddressType.REGISTRATION.name(), RND.str(10), RND.str(10), RND.str(10));
    expectedClientAddressCount++;
    this.insertTmpClientPhone(oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11),
      PhoneType.MOBILE.name(), 0);
    expectedClientPhoneCount++;
    this.insertTmpClientPhone(oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum, clientRecordNum, RND.str(11),
      PhoneType.MOBILE.name(), 0);
    expectedClientPhoneCount++;
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum, 0);
    expectedClientCount++;

    oneCiaFile.migrateData();
    // TODO: downloadErrors();

    List<Map<String, Object>> recordList = toListMap("SELECT * FROM client");
    List<Map<String, Object>> addressRecordList = toListMap("SELECT * FROM client_addr");
    List<Map<String, Object>> phoneRecordList = toListMap("SELECT * FROM client_phone");

    assertThat(recordList.size()).isEqualTo(expectedClientCount);
    assertThat(addressRecordList.size()).isEqualTo(expectedClientAddressCount);
    assertThat(phoneRecordList.size()).isEqualTo(expectedClientPhoneCount);
  }


  @Test
  public void migrate_fromFakeDir() throws Exception {
    resetAllTables();
  }

  private long createClientWithTmp(String tblName, long clientRecordNum, int status) {
    int charmId = clientTestDao.get().selectSeqIdNextValueTableCharm();
    String charmName = RND.str(16);
    clientTestDao.get().insertCharm(charmId, charmName, null, null);

    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    String ciaId = RND.str(16);
    clientTestDao.get().insertClientWithCiaId(id, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Date.valueOf("2000-01-01"), charmId, ciaId);

    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      tblName, clientRecordNum, ciaId, charmName, "1989-05-05", status);

    return id;
  }

  private String insertTmpClient(String tblName, long clientRecordNum, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId,
      clientTestDao.get().selectSeqIdNextValueTableClient(), RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), Date.valueOf("1989-10-10"),
      status, null);
    return ciaId;
  }

  private void insertTmpClient(String tblName, long clientRecordNum, String ciaId, int status) {
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId,
      clientTestDao.get().selectSeqIdNextValueTableClient(), RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), Date.valueOf("1989-10-10"), status, null);
  }

  private String insertTmpClientWithError(String tblName, long clientRecordNum) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId,
      clientTestDao.get().selectSeqIdNextValueTableClient(), RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), Date.valueOf("1989-10-10"), 0, RND.str(10));
    return ciaId;
  }

  private String insertTmpClientWithCharmName(String tblName, long clientRecordNum, String charmName, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId,
      clientTestDao.get().selectSeqIdNextValueTableClient(), RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, Date.valueOf("1989-10-10"), status, null);
    return ciaId;
  }

  private String insertTmpClientWithCharmId_CharmName_Birthdate(String tblName, long clientRecordNum, String charmName,
                                                                String birth, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId, null, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, Date.valueOf(birth),
      status, null);
    return ciaId;
  }

  private void insertTmpClientWithCharmId_CharmName_Birthdate(String tblName, long clientRecordNum, String ciaId,
                                                              String charmName, String birth,
                                                              int status) {
    migrationTestDao.get().insertClient(tblName, clientRecordNum, ciaId,
      clientTestDao.get().selectSeqIdNextValueTableClient(), RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, Date.valueOf(birth),
      status, null);
  }

  private String insertTmpClientWithClientId(String tblName, long recordNo, long id, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, recordNo, ciaId, id, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), null, null, status, null);
    return ciaId;
  }

  private void insertTmpClientPhone(String tblName, long recordNo, long clientRecordNo, String number,
                                    String type, int status) {
    migrationTestDao.get().insertClientPhone(tblName, recordNo, clientRecordNo, number, type, status);
  }

  private int insertCharm(String charmName) {
    int id = clientTestDao.get().selectSeqIdNextValueTableCharm();
    clientTestDao.get().insertCharm(id, charmName, null, null);
    return id;
  }

  private void insertTmpClientAddress(String tblName, long recordNo, long clientRecordNo, String type, String street,
                                      String house, String flat) {
    migrationTestDao.get().insertClientAddress(tblName, recordNo, clientRecordNo, type, street, house, flat);
  }
}
