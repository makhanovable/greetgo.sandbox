package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrateOneCiaFileTest extends MigrateCommonTests {

  public BeanGetter<ClientTestDao> clientTestDao;

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
    oneCiaFile.inputFile = inFile;
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
/*
  @Test
  public void processValidationErrors() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> surnameErrorCiaIdList = new ArrayList<>();
    surnameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, null, RND.str(10), RND.str(10)));
    surnameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, "", RND.str(10), RND.str(10)));
    surnameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, "    ", RND.str(10), RND.str(10)));

    List<String> nameErrorCiaIdList = new ArrayList<>();
    nameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, RND.str(10), "   ", RND.str(10)));
    nameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, RND.str(10), null, RND.str(10)));
    nameErrorCiaIdList.add(
      this.insertTmpClientWithNameError(oneCiaFile.tmpClientTableName, clientRecordNum++, RND.str(10), "", RND.str(10)));

    List<String> birthEmptyErrorCiaIdList = new ArrayList<>();
    birthEmptyErrorCiaIdList.add(this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, null));
    birthEmptyErrorCiaIdList.add(this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, ""));
    birthEmptyErrorCiaIdList.add(this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, "  "));

    List<String> birthFormatErrorCiaIdList = new ArrayList<>();
    birthFormatErrorCiaIdList.add(
      this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, RND.str(10)));
    birthFormatErrorCiaIdList.add(
      this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, "30-12-2000"));

    List<String> birthBoundErrorCiaIdList = new ArrayList<>();
    birthBoundErrorCiaIdList.add(
      this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, "1000-01-10"));
    birthBoundErrorCiaIdList.add(
      this.insertTmpClientWithBirthError(oneCiaFile.tmpClientTableName, clientRecordNum++, "2017-12-12"));

    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0);
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum, 0);

    oneCiaFile.processValidationErrors();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE error IS NOT NULL " +
        "ORDER BY record_no ASC");

    assertThat(recordList.size())
      .isEqualTo(surnameErrorCiaIdList.size() + nameErrorCiaIdList.size() + birthEmptyErrorCiaIdList.size() +
        birthFormatErrorCiaIdList.size() + birthBoundErrorCiaIdList.size());
    int rowNum = 0;
    for (String surnameErrorCiaId : surnameErrorCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(surnameErrorCiaId);
      assertThat(recordList.get(rowNum).get("error"))
        .isEqualTo("Пустое значение surname у ciaId = " + surnameErrorCiaId);
      rowNum++;
    }
    for (String nameErrorCiaId : nameErrorCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(nameErrorCiaId);
      assertThat(recordList.get(rowNum).get("error"))
        .isEqualTo("Пустое значение name у ciaId = " + nameErrorCiaId);
      rowNum++;
    }
    for (String birthEmptyErrorCiaId : birthEmptyErrorCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(birthEmptyErrorCiaId);
      assertThat(recordList.get(rowNum).get("error"))
        .isEqualTo("Пустое значение birth у ciaId = " + birthEmptyErrorCiaId);
      rowNum++;
    }
    for (String birthFormatErrorCiaId : birthFormatErrorCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(birthFormatErrorCiaId);
      assertThat(recordList.get(rowNum).get("error"))
        .isEqualTo("Неправильный формат даты у ciaId = " + birthFormatErrorCiaId +
          ". Принятый формат ГОСДЕПа YYYY-MM-DD");
      rowNum++;
    }
    for (String birthBoundErrorCiaId : birthBoundErrorCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(birthBoundErrorCiaId);
      assertThat(recordList.get(rowNum).get("error"))
        .isEqualTo("Значение birth выходит за рамки у ciaId = " + birthBoundErrorCiaId +
          ". Возраст должен быть между 3 и 1000 годами");
      rowNum++;
    }
  }
*/

  @Test
  public void processValidationErrors() throws Exception {
    String generatedId = Util.generateRandomString(16);
    File inFile = new File("build/MigrateOneCiaFileTest/cia_" + generatedId + ".xml");
    inFile.getParentFile().mkdirs();
    createCiaFileWithErrors(inFile);

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.inputFile = inFile;
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
  public void migrateData_status1() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> expectedCiaIdList = new ArrayList<>();

    expectedCiaIdList.add(this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0));
    expectedCiaIdList.add(this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 0));
    this.insertTmpClientWithError(oneCiaFile.tmpClientTableName, clientRecordNum);

    oneCiaFile.migrateData_status1();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 1 ORDER BY record_no ASC");

    assertThat(recordList.size()).isEqualTo(expectedCiaIdList.size());
    int rowNum = 0;
    for (String expectedCiaId : expectedCiaIdList) {
      assertThat(recordList.get(rowNum).get("cia_id")).isEqualTo(expectedCiaId);
      rowNum++;
    }
  }

  @Test
  public void migrateData_status2() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> expectedCiaIdList = new ArrayList<>();
    List<Long> expectedClientRecordNumList = new ArrayList<>();

    String ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 1);

    expectedClientRecordNumList.add(clientRecordNum);
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, ciaId, 1);
    expectedCiaIdList.add(ciaId);

    this.insertTmpClientWithError(oneCiaFile.tmpClientTableName, clientRecordNum++);

    expectedClientRecordNumList.add(clientRecordNum);
    ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 1);
    expectedCiaIdList.add(ciaId);

    ciaId = this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, 1);
    for (int i = 0; i < 3; i++)
      this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum++, ciaId, 1);
    expectedClientRecordNumList.add(clientRecordNum);
    this.insertTmpClient(oneCiaFile.tmpClientTableName, clientRecordNum, ciaId, 1);
    expectedCiaIdList.add(ciaId);

    oneCiaFile.migrateData_status2();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 2 ORDER BY record_no ASC");

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
  public void migrateData_finalOfCharmTable() throws Exception {
    this.resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    List<String> expectedCharmNameList = new ArrayList<>();

    String charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);
    for (int i = 0; i < 3; i++)
      this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);

    charmName = RND.str(16);
    expectedCharmNameList.add(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum, charmName, 2);

    oneCiaFile.migrateData_finalOfCharmTable();

    List<Map<String, Object>> charmRecordList = toListMap("SELECT * FROM charm ORDER BY id ASC");

    assertThat(charmRecordList.size()).isEqualTo(expectedCharmNameList.size());
    for (Map<String, Object> charmRecord : charmRecordList)
      assertThat(charmRecord.get("name")).isIn(expectedCharmNameList);
  }

  @Test
  public void migrateData_status3() throws Exception {
    this.resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0;
    String charmName = RND.str(16);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, 2);

    charmName = RND.str(16);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmName(oneCiaFile.tmpClientTableName, clientRecordNum, charmName, 2);

    oneCiaFile.migrateData_status3();

    List<Map<String, Object>> clientRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 3 ORDER BY record_no ASC");
    List<Map<String, Object>> charmRecordList = toListMap("SELECT * FROM charm ORDER BY id ASC");

    assertThat(charmRecordList.size()).isEqualTo(charmRecordList.size());
    for (int i = 0; i < charmRecordList.size(); i++) {
      assertThat(charmRecordList.get(i).get("id")).isEqualTo(clientRecordList.get(i).get("charm_id"));
      assertThat(charmRecordList.get(i).get("name")).isEqualTo(clientRecordList.get(i).get("charm_name"));
    }
  }

  @Test
  public void migrateData_finalOfClientTable() throws Exception {
    this.resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    this.createClient(null);

    long clientRecordNum = 0;
    this.createClientWithTmp(oneCiaFile.tmpClientTableName, clientRecordNum++, 3);

    String charmName = RND.str(16);
    int charmId = this.insertCharm(charmName);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, charmId, "2000-10-10", 3);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum++, charmName, charmId, "1989-05-05", 3);

    charmName = RND.str(16);
    charmId = this.insertCharm(charmName);
    this.insertCharm(charmName);
    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      oneCiaFile.tmpClientTableName, clientRecordNum, charmName, charmId, "2010-11-04", 3);

    oneCiaFile.migrateData_finalOfClientTable();

    List<Map<String, Object>> tempRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE status = 3 ORDER BY record_no ASC");
    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM client WHERE migration_cia_id IS NOT NULL ORDER BY id ASC");

    assertThat(recordList.size()).isEqualTo(tempRecordList.size());
    for (int i = 0; i < recordList.size(); i++) {
      assertThat(recordList.get(i).get("migration_cia_id")).isEqualTo(tempRecordList.get(i).get("cia_id"));
      assertThat(recordList.get(i).get("id")).isEqualTo(tempRecordList.get(i).get("client_id"));
      assertThat(recordList.get(i).get("surname")).isEqualTo(tempRecordList.get(i).get("surname"));
      assertThat(recordList.get(i).get("name")).isEqualTo(tempRecordList.get(i).get("name"));
      assertThat(recordList.get(i).get("patronymic")).isEqualTo(tempRecordList.get(i).get("patronymic"));
      assertThat(recordList.get(i).get("gender")).isEqualTo(tempRecordList.get(i).get("gender"));
      assertThat(recordList.get(i).get("birth_date").toString())
        .isEqualTo(tempRecordList.get(i).get("birth_date").toString());
      assertThat(recordList.get(i).get("charm")).isEqualTo(tempRecordList.get(i).get("charm_id"));
    }
  }

  @Test
  public void migrateData_finalOfClientAddressTable() throws Exception {
    this.resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0, clientAddressRecordNum = 0;
    int expectedAddressListSize = 0;
    long clientId = this.createClient(RND.str(16));
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.FACTUAL.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum++, clientRecordNum,
      AddressType.REGISTRATION.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum++, clientId, 3);
    clientId = this.createClient(RND.str(16));
    this.insertTmpClientAddress(oneCiaFile.tmpClientAddressTableName, clientAddressRecordNum, clientRecordNum,
      AddressType.FACTUAL.name(), RND.str(16), RND.str(16), RND.str(16));
    expectedAddressListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum, clientId, 3);

    oneCiaFile.migrateData_finalOfClientAddressTable();

    List<Map<String, Object>> tempAddressRecordList =
      toListMap("SELECT cl.client_id, adr.type, adr.street, adr.house, adr.flat " +
        "FROM " + oneCiaFile.tmpClientAddressTableName + " AS adr, " + oneCiaFile.tmpClientTableName + " AS cl " +
        "WHERE adr.client_record_no = cl.record_no AND cl.status = 3 " +
        "ORDER BY adr.record_no ASC");
    List<Map<String, Object>> addressRecordList = toListMap("SELECT * FROM client_addr ORDER BY client ASC");

    assertThat(addressRecordList.size()).isEqualTo(tempAddressRecordList.size());
    assertThat(addressRecordList.size()).isEqualTo(expectedAddressListSize);
    for (int i = 0; i < addressRecordList.size(); i++) {
      assertThat(addressRecordList.get(i).get("client")).isEqualTo(tempAddressRecordList.get(i).get("client_id"));
      assertThat(addressRecordList.get(i).get("type")).isEqualTo(tempAddressRecordList.get(i).get("type"));
      assertThat(addressRecordList.get(i).get("street")).isEqualTo(tempAddressRecordList.get(i).get("street"));
      assertThat(addressRecordList.get(i).get("house")).isEqualTo(tempAddressRecordList.get(i).get("house"));
      assertThat(addressRecordList.get(i).get("flat")).isEqualTo(tempAddressRecordList.get(i).get("flat"));
    }
  }

  @Test
  public void migrateData_finalOfClientPhoneTable() throws Exception {
    this.resetAllTables();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long clientRecordNum = 0, clientPhoneRecordNum = 0;
    int expectedPhoneListSize = 0;
    long clientId = this.createClient(RND.str(16));
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11), RND.str(10), 0);
    expectedPhoneListSize++;
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, RND.str(11), RND.str(10), 0);
    expectedPhoneListSize++;
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum++, clientId, 3);

    clientId = this.createClient(RND.str(16));
    String number = RND.str(11);
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum++, clientRecordNum, number, RND.str(11), 0);
    expectedPhoneListSize++;
    this.insertTmpClientPhone(
      oneCiaFile.tmpClientPhoneTableName, clientPhoneRecordNum, clientRecordNum, number, RND.str(11), 0);
    this.insertTmpClientWithClientId(oneCiaFile.tmpClientTableName, clientRecordNum, clientId, 3);

    oneCiaFile.migrateData_finalOfClientPhoneTable();

    List<Map<String, Object>> tempPhoneRecordList =
      toListMap("SELECT cl.client_id, ph.number, ph.type " +
        "FROM " + oneCiaFile.tmpClientPhoneTableName + " AS ph, " + oneCiaFile.tmpClientTableName + " AS cl " +
        "WHERE ph.client_record_no = cl.record_no AND cl.status = 3 AND ph.status = 1 " +
        "ORDER BY cl.client_id ASC");
    List<Map<String, Object>> phoneRecordList = toListMap("SELECT * FROM client_phone ORDER BY client ASC");

    assertThat(phoneRecordList.size()).isEqualTo(tempPhoneRecordList.size());
    assertThat(phoneRecordList.size()).isEqualTo(expectedPhoneListSize);
    for (int i = 0; i < phoneRecordList.size(); i++) {
      assertThat(phoneRecordList.get(i).get("client")).isEqualTo(tempPhoneRecordList.get(i).get("client_id"));
      assertThat(phoneRecordList.get(i).get("number")).isEqualTo(tempPhoneRecordList.get(i).get("number"));
      assertThat(phoneRecordList.get(i).get("type")).isEqualTo(tempPhoneRecordList.get(i).get("type"));
    }
  }

  @Test
  public void migrate_withoutDataUpload_CiaFile() throws Exception {
    this.resetAllTables();

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

    //oneCiaFile.processValidationErrors();
    oneCiaFile.migrateData();
    // TODO: downloadErrors();

    List<Map<String, Object>> recordList = toListMap("SELECT * FROM client");
    List<Map<String, Object>> addressRecordList = toListMap("SELECT * FROM client_addr");
    List<Map<String, Object>> phoneRecordList = toListMap("SELECT * FROM client_phone");

    assertThat(recordList.size()).isEqualTo(expectedClientCount);
    assertThat(addressRecordList.size()).isEqualTo(expectedClientAddressCount);
    assertThat(phoneRecordList.size()).isEqualTo(expectedClientPhoneCount);
  }

  private long createClient(String ciaId) {
    int charmId = clientTestDao.get().selectSeqIdNextValueTableCharm();
    clientTestDao.get().insertCharm(charmId, RND.str(16), null, null);

    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().updateClientWithCiaId(id, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Date.valueOf("2000-01-01"), charmId, ciaId);

    return id;
  }

  private long createClientWithTmp(String tblName, long clientRecordNum, int status) {
    int charmId = clientTestDao.get().selectSeqIdNextValueTableCharm();
    String charmName = RND.str(16);
    clientTestDao.get().insertCharm(charmId, charmName, null, null);

    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    String ciaId = RND.str(16);
    clientTestDao.get().updateClientWithCiaId(id, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Date.valueOf("2000-01-01"), charmId, ciaId);

    this.insertTmpClientWithCharmId_CharmName_Birthdate(
      tblName, clientRecordNum, ciaId, charmName, charmId, "1989-05-05", status);

    return id;
  }

  private String insertTmpClient(String tblName, long clientRecordNum, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, Date.valueOf("1989-10-10"),
      status, null);
    return ciaId;
  }

  private void insertTmpClient(String tblName, long clientRecordNum, String ciaId, int status) {
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, Date.valueOf("1989-10-10"), status, null);
  }

  private String insertTmpClientWithError(String tblName, long clientRecordNum) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, Date.valueOf("1989-10-10"), 0, RND.str(10));
    return ciaId;
  }

  private String insertTmpClientWithCharmName(String tblName, long clientRecordNum, String charmName, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, null, Date.valueOf("1989-10-10"), status, RND.str(10));
    return ciaId;
  }

  private String insertTmpClientWithCharmId_CharmName_Birthdate(String tblName, long clientRecordNum, String charmName,
                                                                int charmId, String birth, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, charmId, Date.valueOf(birth),
      status, RND.str(10));
    return ciaId;
  }

  private void insertTmpClientWithCharmId_CharmName_Birthdate(String tblName, long clientRecordNum, String ciaId,
                                                              String charmName, int charmId, String birth,
                                                              int status) {
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), charmName, charmId, Date.valueOf(birth),
      status, RND.str(10));
  }

  private String insertTmpClientWithClientId(String tblName, long recordNo, long clientId, int status) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, recordNo, clientId, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), null, null, null, status, RND.str(10));
    return ciaId;
  }

  private String insertTmpClientWithNameError(String tblName, long clientRecordNum, String surname, String name,
                                              String patronymic) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, surname, name, patronymic,
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, Date.valueOf("1989-10-10"), 0,
      null);
    return ciaId;
  }

  private String insertTmpClientWithBirthError(String tblName, long clientRecordNum, String birthDate) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tblName, clientRecordNum, null, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, Date.valueOf(birthDate), 0, null);
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

  private void resetAllTables() {
    this.resetClientAddrTable();
    this.resetClientPhoneTable();
    this.resetClientAccountTable();
    this.resetClientTable();
    this.resetCharmTable();
  }

  private void resetClientPhoneTable() {
    migrationTestDao.get().deleteAllTableClientPhone();
  }

  private void resetClientAddrTable() {
    migrationTestDao.get().deleteAllTableClientAddr();
  }

  private void resetClientAccountTable() {
    migrationTestDao.get().deleteAllTableClientAccount();
  }

  private void resetCharmTable() {
    migrationTestDao.get().deleteAllTableCharm();
  }

  private void resetClientTable() {
    migrationTestDao.get().deleteAllTableClient();
  }
}
