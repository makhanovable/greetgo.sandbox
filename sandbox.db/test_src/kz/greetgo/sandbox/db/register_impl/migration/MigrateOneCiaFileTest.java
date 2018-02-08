package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.util.Util;
import org.testng.annotations.Test;

import java.io.File;
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
    File inFile = new File("build/MigrateOneCiaFileTest/cia_" + Util.generateRandomString(16) + ".xml");
    inFile.getParentFile().mkdirs();

    createCiaFile(inFile);

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.inputFile = inFile;
    oneCiaFile.prepareTmpTables();
    oneCiaFile.uploadData();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " ORDER BY instance_id");
    assertThat(recordList).hasSize(2);
    assertThat(recordList.get(0).get("surname")).isEqualTo("Иванов");
    assertThat(recordList.get(1).get("surname")).isEqualTo("Петров");
    assertThat(recordList.get(0).get("name")).isEqualTo("Иван");
    assertThat(recordList.get(1).get("name")).isEqualTo("Пётр");
    assertThat(recordList.get(0).get("patronymic")).isEqualTo("Иваныч");
    assertThat(recordList.get(1).get("patronymic")).isEqualTo("Петрович");
    assertThat(recordList.get(0).get("gender")).isEqualTo("MALE");
    assertThat(recordList.get(1).get("gender")).isEqualTo("MALE");
    assertThat(recordList.get(0).get("birth_date")).isEqualTo("1980-11-12");
    assertThat(recordList.get(1).get("birth_date")).isEqualTo("1980-11-13");
    assertThat(recordList.get(0).get("charm_name")).isEqualTo("Уситчивый");
    assertThat(recordList.get(1).get("charm_name")).isEqualTo("Агрессивный");
    assertThat(recordList.get(0).get("charm_name")).isEqualTo("Уситчивый");
    assertThat(recordList.get(1).get("charm_name")).isEqualTo("Агрессивный");

    List<Map<String, Object>> addressRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientAddressTableName + " ORDER BY instance_id");
    int idx = 0;
    assertThat(addressRecordList.get(idx++).get("type")).isEqualTo(AddressType.FACTUAL.name());
    assertThat(addressRecordList.get(idx++).get("type")).isEqualTo(AddressType.REGISTRATION.name());
    assertThat(addressRecordList.get(idx++).get("type")).isEqualTo(AddressType.FACTUAL.name());
    assertThat(addressRecordList.get(idx).get("type")).isEqualTo(AddressType.REGISTRATION.name());

    idx = 0;
    assertThat(addressRecordList.get(idx++).get("street")).isEqualTo("Панфилова");
    assertThat(addressRecordList.get(idx++).get("street")).isEqualTo("Панфилова");
    assertThat(addressRecordList.get(idx++).get("street")).isEqualTo("Никонова");
    assertThat(addressRecordList.get(idx).get("street")).isEqualTo("Панфилова");
    idx = 0;
    assertThat(addressRecordList.get(idx++).get("house")).isEqualTo("23A");
    assertThat(addressRecordList.get(idx++).get("house")).isEqualTo("23A");
    assertThat(addressRecordList.get(idx++).get("house")).isEqualTo("6");
    assertThat(addressRecordList.get(idx).get("house")).isEqualTo("13A");
    idx = 0;
    assertThat(addressRecordList.get(idx++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(idx++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(idx++).get("flat")).isEqualTo("22");
    assertThat(addressRecordList.get(idx).get("flat")).isEqualTo("12");

    List<Map<String, Object>> phoneRecordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientPhoneTableName + " ORDER BY instance_id");
    idx = 0;
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.HOME.name());
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-22-33");
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-33-33");
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-44-33");
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.MOBILE.name());
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-55-33");
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.WORK.name());
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-00-33 вн. 3344");
    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.WORK.name());
    assertThat(phoneRecordList.get(idx).get("number")).isEqualTo("+7-123-111-00-33 вн. 3343");
  }
}
