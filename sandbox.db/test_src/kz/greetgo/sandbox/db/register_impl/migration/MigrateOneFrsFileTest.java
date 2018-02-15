package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
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
    assertThat(clientAccountRecordList.get(0).get("client_id")).isEqualTo("4-DU8-32-H7");
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

  private void insertClientAccountTransaction(String tblName, long recordNum, String accountNum) {
    migrationTestDao.get().insertClientAccountTransaction(tblName, recordNum,
      new BigDecimal("23000000.00"), Timestamp.from(Instant.now()), RND.str(10), accountNum, 0);
  }

  private void insertClientAccountTransactionWithMoney(String tblName, long recordNum, BigDecimal money,
                                                       String accountNum) {
    migrationTestDao.get()
      .insertClientAccountTransaction(tblName, recordNum, money, Timestamp.from(Instant.now()), RND.str(10), accountNum, 0);
  }

  private String insertClientAccount(String tblName, long recordNum) {
    String accountNum = RND.str(16);
    migrationTestDao.get()
      .insertClientAccount(tblName, recordNum, RND.str(10), accountNum, Timestamp.from(Instant.now()), 0);
    return accountNum;
  }

  private String insertClientAccountWithRegistrationDate(String tblName, long recordNum, Timestamp date) {
    String accountNum = RND.str(16);
    migrationTestDao.get()
      .insertClientAccount(tblName, recordNum, RND.str(10), accountNum, date, 0);
    return accountNum;
  }
}
