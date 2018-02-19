package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrateOneFrsFileTest extends MigrateCommonTests {

  @Test
  public void prepareTmpTables() throws Exception {
    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.prepareTmpTables();
  }

  @Test
  public void uploadData() throws Exception {
    String generatedId = Util.generateRandomString(16);
    File inFile = new File("build/MigrateOneFrsFileTest/frs_" + generatedId + ".json_row");
    inFile.getParentFile().mkdirs();
    createFrsFile(inFile);
    File outputErrorFile = new File("build/MigrateOneFrsFileTest/frs_error_" + generatedId + ".txt");

    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.inputFile = inFile;
    oneFrsFile.outputErrorFile = new CommonErrorFileWriter(outputErrorFile);
    oneFrsFile.prepareTmpTables();
    oneFrsFile.uploadData();

    List<Map<String, Object>> clientAccountRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTableName + " ORDER BY record_no");
    assertThat(clientAccountRecordList).hasSize(1);
    assertThat(clientAccountRecordList.get(0).get("cia_id")).isEqualTo("4-DU8-32-H7");
    assertThat(clientAccountRecordList.get(0).get("account_number")).isEqualTo("32134KZ343-43546-535436-77656");
    assertThat(clientAccountRecordList.get(0).get("registered_at").toString()).isEqualTo("2011-01-23 23:22:11.456");

    List<Map<String, Object>> clientAccountTransactionRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTransactionTableName + " ORDER BY record_no");
    assertThat(clientAccountTransactionRecordList).hasSize(2);
    assertThat(clientAccountTransactionRecordList.get(0).get("money").toString()).isEqualTo("123000000098.13");
    assertThat(clientAccountTransactionRecordList.get(1).get("money").toString()).isEqualTo("-23000000034.17");
    assertThat(clientAccountTransactionRecordList.get(0).get("finished_at").toString())
      .isEqualTo("2010-01-23 11:56:11.987");
    assertThat(clientAccountTransactionRecordList.get(1).get("finished_at").toString())
      .isEqualTo("2010-01-23 11:56:11.987");
    assertThat(clientAccountTransactionRecordList.get(0).get("transaction_type"))
      .isEqualTo("Перечисление с госбюджета");
    assertThat(clientAccountTransactionRecordList.get(1).get("transaction_type"))
      .isEqualTo("Вывод средств в офшоры");
    assertThat(clientAccountTransactionRecordList.get(0).get("account_number"))
      .isEqualTo("32134KZ343-43546-535436-77656");
    assertThat(clientAccountTransactionRecordList.get(1).get("account_number"))
      .isEqualTo("32134KZ343-43546-535436-77656");

  }

  @Test
  public void processValidationErrors() throws Exception {
    String generatedId = Util.generateRandomString(16);
    File inFile = new File("build/MigrateOneFrsFileTest/frs_" + generatedId + ".json_row");
    inFile.getParentFile().mkdirs();
    createFrsFileWithErrors(inFile);

    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.inputFile = inFile;
    oneFrsFile.outputErrorFile = new ErrorFileWriterTest();
    oneFrsFile.prepareTmpTables();
    oneFrsFile.uploadData();

    List<String> errorList = ((ErrorFileWriterTest) oneFrsFile.outputErrorFile).errorList;

    assertThat(errorList).hasSize(10);
    assertThat(errorList.get(0)).startsWith("Неизвестная команда");
    assertThat(errorList.get(1)).startsWith("Неправильный формат денег money");
    assertThat(errorList.get(2)).startsWith("Неправильный формат денег money");
    assertThat(errorList.get(3)).startsWith("Значение finished_at выходит за рамки");
    assertThat(errorList.get(4)).startsWith("Значение finished_at выходит за рамки");
    assertThat(errorList.get(5)).startsWith("Неправильный формат временного штампа finished_at");
    assertThat(errorList.get(6)).startsWith("Неизвестная команда");
    assertThat(errorList.get(7)).startsWith("Значение registered_at выходит за рамки");
    assertThat(errorList.get(8)).startsWith("Значение registered_at выходит за рамки");
    assertThat(errorList.get(9)).startsWith("Неправильный формат временного штампа registered_at");
  }

  @Test
  public void migrateData_checkForDuplicatesOfClientAccount() throws Exception {
    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.prepareTmpTables();

    long clientAccountRecordNum = 0;
    List<String> expectedAccountNumList = new ArrayList<>();
    List<Long> expectedAccountRecordNumList = new ArrayList<>();
    String accountNum = this.insertClientAccount(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum++, 0);
    expectedAccountNumList.add(accountNum);
    expectedAccountRecordNumList.add(clientAccountRecordNum);
    this.insertClientAccount(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum++, accountNum, 0);

    expectedAccountRecordNumList.add(clientAccountRecordNum);
    expectedAccountNumList.add(
      this.insertClientAccount(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum, 0));

    oneFrsFile.migrateData_checkForDuplicatesOfTmpClientAccount();

    List<Map<String, Object>> clientAccountRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTableName + " WHERE status = 1 ORDER BY record_no");
    assertThat(clientAccountRecordList).hasSize(expectedAccountNumList.size());
    for (int i = 0; i < clientAccountRecordList.size(); i++) {
      assertThat(clientAccountRecordList.get(i).get("record_no")).
        isEqualTo(expectedAccountRecordNumList.get(i));
      assertThat(clientAccountRecordList.get(i).get("account_number"))
        .isEqualTo(expectedAccountNumList.get(i));
    }
  }

  //TODO: доделать еще

  @Test
  public void migrateData_checkForDuplicatesOfClientAccountTransaction() throws Exception {
    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.prepareTmpTables();

    long accountTransRecordNum = 0;
    List<AccountTransactionHelper> expectedAccountTransList = new ArrayList<>();
    List<Long> expectedAccountTransRecordNumList = new ArrayList<>();

    AccountTransactionHelper transHelper =
      this.insertClientAccountTransaction(oneFrsFile.tmpClientAccountTransactionTableName, accountTransRecordNum++, 0);

    expectedAccountTransRecordNumList.add(accountTransRecordNum);
    expectedAccountTransList.add(transHelper);
    this.insertClientAccountTransaction(
      oneFrsFile.tmpClientAccountTransactionTableName, accountTransRecordNum++, transHelper, 0);

    expectedAccountTransRecordNumList.add(accountTransRecordNum);
    expectedAccountTransList.add(
      this.insertClientAccountTransaction(oneFrsFile.tmpClientAccountTransactionTableName, accountTransRecordNum, 0));

    oneFrsFile.migrateData_checkForDuplicatesOfTmpClientAccountTransaction();

    List<Map<String, Object>> accountTransRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTransactionTableName +
        " WHERE status = 1 ORDER BY record_no");
    assertThat(accountTransRecordList).hasSize(expectedAccountTransList.size());
    for (int i = 0; i < accountTransRecordList.size(); i++) {
      assertThat(accountTransRecordList.get(i).get("record_no")).
        isEqualTo(expectedAccountTransRecordNumList.get(i));
      assertThat(accountTransRecordList.get(i).get("money").toString())
        .isEqualTo(expectedAccountTransList.get(i).money.toString());
      assertThat(accountTransRecordList.get(i).get("finished_at").toString())
        .isEqualTo(expectedAccountTransList.get(i).finished_at.toString());
      assertThat(accountTransRecordList.get(i).get("account_number"))
        .isEqualTo(expectedAccountTransList.get(i).accountNum);
    }
  }

  @Test
  public void migrateData_finalOfTransactionTypeTable() throws Exception {
    resetAllTables();

    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.prepareTmpTables();

    List<String> expectedTypeNameList = new ArrayList<>();

    String typeName = RND.str(16);
    expectedTypeNameList.add(typeName);
    this.insertTransactionType(typeName);

    long transRecordNum = 0;
    AccountTransactionHelper helper =
      this.insertClientAccountTransaction(oneFrsFile.tmpClientAccountTransactionTableName, transRecordNum++, 1);
    expectedTypeNameList.add(helper.transaction_type);
    this.insertClientAccountTransaction(oneFrsFile.tmpClientAccountTransactionTableName, transRecordNum++, helper, 1);

    helper =
      this.insertClientAccountTransaction(oneFrsFile.tmpClientAccountTransactionTableName, transRecordNum, 1);
    expectedTypeNameList.add(helper.transaction_type);

    oneFrsFile.migrateData_finalOfTransactionTypeTable();

    List<Map<String, Object>> transRecordList = toListMap("SELECT * FROM transaction_type ORDER BY id ASC");

    assertThat(transRecordList.size()).isEqualTo(expectedTypeNameList.size());
    for (Map<String, Object> transRecord : transRecordList)
      assertThat(transRecord.get("name")).isIn(expectedTypeNameList);
  }

  private static class AccountTransactionHelper {
    BigDecimal money;
    Timestamp finished_at;
    String transaction_type;
    String accountNum;
  }

  private AccountTransactionHelper insertClientAccountTransaction(String tblName, long recordNum, int status) {
    AccountTransactionHelper helper = new AccountTransactionHelper();
    helper.money = new BigDecimal("123456789.12");
    helper.finished_at = Timestamp.from(Instant.now());
    helper.accountNum = RND.str(16);
    helper.transaction_type = RND.str(10);

    migrationTestDao.get().insertClientAccountTransaction(tblName, recordNum,
      helper.money, helper.finished_at, helper.transaction_type, helper.accountNum, status, null);

    return helper;
  }

  private void insertClientAccountTransaction(String tblName, long recordNum, AccountTransactionHelper transHelper,
                                              int status) {
    migrationTestDao.get().insertClientAccountTransaction(tblName, recordNum, transHelper.money,
      transHelper.finished_at, transHelper.transaction_type, transHelper.accountNum, status, null);
  }

  private void insertClientAccountTransactionWithMoney(String tblName, long recordNum, String accountNum,
                                                       BigDecimal money, int status) {
    migrationTestDao.get().insertClientAccountTransaction(tblName, recordNum, money, Timestamp.from(Instant.now()),
      RND.str(10), accountNum, status, null);
  }

  private String insertClientAccount(String tblName, long recordNum, int status) {
    String accountNum = RND.str(16);
    migrationTestDao.get().insertClientAccount(tblName, recordNum, RND.str(10), new BigDecimal("0.00"), accountNum,
      Timestamp.from(Instant.now()), status, null);
    return accountNum;
  }

  private void insertClientAccount(String tblName, long recordNum, String accountNum, int status) {
    migrationTestDao.get().insertClientAccount(tblName, recordNum, RND.str(10), new BigDecimal("0.00"), accountNum,
      Timestamp.from(Instant.now()), status, null);
  }

  private void insertClientAccount(String tblName, long recordNum, String accountNum, String ciaId, int status) {
    migrationTestDao.get().insertClientAccount(tblName, recordNum, RND.str(10), new BigDecimal("0.00"), accountNum,
      Timestamp.from(Instant.now()), status, null);
  }

  private String insertClientAccountWithRegistrationDate(String tblName, long recordNum, Timestamp date, int status) {
    String accountNum = RND.str(16);
    migrationTestDao.get()
      .insertClientAccount(tblName, recordNum, RND.str(10), new BigDecimal("0.00"), accountNum, date, status, null);
    return accountNum;
  }

  private void insertTransactionType(String name) {
    int id = clientTestDao.get().selectSeqIdNextValueTableTransactionType();
    clientTestDao.get().insertTransactionType(id, null, name);
  }
}
