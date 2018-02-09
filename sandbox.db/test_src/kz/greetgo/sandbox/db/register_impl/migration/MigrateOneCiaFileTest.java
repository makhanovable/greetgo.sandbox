package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
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
    File inFile = this.prepareReadyFileCommon();

    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.inputFile = inFile;
    oneCiaFile.prepareTmpTables();
    oneCiaFile.uploadData();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " ORDER BY instance_id");
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
    assertThat(phoneRecordList.get(idx++).get("number")).isEqualTo("+7-123-111-00-33 вн. 3343");

    assertThat(phoneRecordList.get(idx).get("type")).isEqualTo(PhoneType.HOME.name());
    assertThat(phoneRecordList.get(idx).get("number")).isEqualTo("+7-123-333-22-33");
  }

  @Test
  public void processErrors() throws Exception {
    MigrateOneCiaFile oneCiaFile = new MigrateOneCiaFile();
    oneCiaFile.connection = connection;
    oneCiaFile.prepareTmpTables();

    long instanceId = 0;
    List<String> surnameErrorCiaIdList = new ArrayList<>();
    surnameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, null, RND.str(10),
      RND.str(10)));
    surnameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, "", RND.str(10),
      RND.str(10)));
    surnameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, "    ",
      RND.str(10), RND.str(10)));

    List<String> nameErrorCiaIdList = new ArrayList<>();
    nameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, RND.str(10), "   ",
      RND.str(10)));
    nameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, RND.str(10), null,
      RND.str(10)));
    nameErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, RND.str(10), "",
      RND.str(10)));

    List<String> birthErrorCiaIdList = new ArrayList<>();
    birthErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, null));
    birthErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, ""));
    birthErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, "  "));
    birthErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, "1000-01-10"));
    birthErrorCiaIdList.add(this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++, "2017-12-12"));

    this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId++);
    this.insertClientForError(oneCiaFile.tmpClientTableName, instanceId);

    oneCiaFile.processErrors();

    List<Map<String, Object>> recordList =
      toListMap("SELECT * FROM " + oneCiaFile.tmpClientTableName + " WHERE error IS NOT NULL " +
        "ORDER BY instance_id ASC");

    assertThat(recordList.size())
      .isEqualTo(surnameErrorCiaIdList.size() + nameErrorCiaIdList.size() + birthErrorCiaIdList.size());
    int idx = 0;
    for (String surnameErrorCiaId : surnameErrorCiaIdList) {
      assertThat(recordList.get(idx).get("cia_id")).isEqualTo(surnameErrorCiaId);
      idx++;
    }
    for (String nameErrorCiaId : nameErrorCiaIdList) {
      assertThat(recordList.get(idx).get("cia_id")).isEqualTo(nameErrorCiaId);
      idx++;
    }
    for (String birthErrorCiaId : birthErrorCiaIdList) {
      assertThat(recordList.get(idx).get("cia_id")).isEqualTo(birthErrorCiaId);
      idx++;
    }
  }

  private File prepareReadyFileCommon() throws Exception {
    File inFile = new File("build/MigrateOneCiaFileTest/cia_" + Util.generateRandomString(16) + ".xml");
    inFile.getParentFile().mkdirs();

    createCiaFile(inFile);

    return inFile;
  }

  private String insertClientForError(String tmpTableName, long instanceId) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tmpTableName, instanceId, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, "1989-10-10", 0, null);
    return ciaId;
  }

  private String insertClientForError(String tmpTableName, long instanceId, String surname, String name,
                                      String patronymic) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tmpTableName, instanceId, ciaId, surname, name, patronymic,
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, "1989-10-10", 0, null);
    return ciaId;
  }

  private String insertClientForError(String tmpTableName, long instanceId, String birthDate) {
    String ciaId = RND.str(8);
    migrationTestDao.get().insertClient(tmpTableName, instanceId, ciaId, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), RND.str(10), null, birthDate, 0, null);
    return ciaId;
  }
}
