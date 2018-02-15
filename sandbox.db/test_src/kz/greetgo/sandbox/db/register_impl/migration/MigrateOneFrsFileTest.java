package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
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
    File inFile = new File("build/MigrateOneFrsFileTest/cia_" + Util.generateRandomString(16) + ".xml");
    inFile.getParentFile().mkdirs();
    createFrsFile(inFile);

    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.inputFile = inFile;
    oneFrsFile.prepareTmpTables();
    oneFrsFile.uploadData();

    List<Map<String, Object>> clientAccountRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTableName + " ORDER BY record_no");
    assertThat(clientAccountRecordList).hasSize(1);
    assertThat(clientAccountRecordList.get(0).get("client_id")).isEqualTo("4-DU8-32-H7");
    assertThat(clientAccountRecordList.get(0).get("account_number")).isEqualTo("32134KZ343-43546-535436-77656");
    assertThat(clientAccountRecordList.get(0).get("registered_at")).isEqualTo("2011-01-23T23:22:11.456");

    List<Map<String, Object>> clientAccountTransactionRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTransactionTableName + " ORDER BY record_no");
    assertThat(clientAccountTransactionRecordList).hasSize(2);
    assertThat(clientAccountTransactionRecordList.get(0).get("money")).isEqualTo("+123_000_000_098.13");
    assertThat(clientAccountTransactionRecordList.get(1).get("money")).isEqualTo("-23_000_000_034.17");
    assertThat(clientAccountTransactionRecordList.get(0).get("finished_at")).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(clientAccountTransactionRecordList.get(1).get("finished_at")).isEqualTo("2010-01-23T11:56:11.987");
    assertThat(clientAccountTransactionRecordList.get(0).get("transaction_type"))
      .isEqualTo("Перечисление с госбюджета");
    assertThat(clientAccountTransactionRecordList.get(1).get("transaction_type"))
      .isEqualTo("Вывод средств в офшоры");
    assertThat(clientAccountTransactionRecordList.get(0).get("account_number"))
      .isEqualTo("32134KZ343-43546-535436-77656");
    assertThat(clientAccountTransactionRecordList.get(1).get("account_number"))
      .isEqualTo("32134KZ343-43546-535436-77656");
  }

//TODO: тесты для проверки файловых ошибок

  @Test
  public void processValidationErrors() throws Exception {
    MigrateOneFrsFile oneFrsFile = new MigrateOneFrsFile();
    oneFrsFile.connection = connection;
    oneFrsFile.prepareTmpTables();

    long clientAccountRecordNum = 0, clientAccountTransactionRecordNum = 0;
    this.insertClientAccount(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum++);
/*
    this.insertClientAccountWithRegistrationDate(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum++,
      new Timestamp(System.currentTimeMillis() - 1100 * 1471228928).toInstant().toString());

    this.insertClientAccountWithRegistrationDate(oneFrsFile.tmpClientAccountTableName, clientAccountRecordNum++,
      new Timestamp(System.currentTimeMillis() + 1 * 1471228928).toInstant().toString());

    String accountNum = this.insertClientAccountWithRegistrationDate(oneFrsFile.tmpClientAccountTableName,
      clientAccountRecordNum++, new Timestamp(System.currentTimeMillis()).toInstant().toString());
    this.insertClientAccountTransaction(
      oneFrsFile.tmpClientAccountTransactionTableName, clientAccountTransactionRecordNum++, accountNum);
    this.insertClientAccountTransactionWithMoney(
      oneFrsFile.tmpClientAccountTransactionTableName, clientAccountTransactionRecordNum++, "1_000_000.00", accountNum);
*/
    List<Map<String, Object>> clientAccountRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTableName + " " +
        "WHERE error IS NOT NULL ORDER BY record_no");
    assertThat(clientAccountRecordList).hasSize(3);//TODO: finish like this
    assertThat(clientAccountRecordList.get(0).get("error")).isEqualTo("Неправильный формат даты registered_at у " +
      "account_number = " + clientAccountRecordList.get(0).get("account_number"));
    assertThat(clientAccountRecordList.get(1).get("error")).isEqualTo("Значение registered_at выходит за рамки у " +
      "account_number = " + clientAccountRecordList.get(1).get("account_number") +
      ". Дате регистрации должно быть не больше 1000 лет");
    assertThat(clientAccountRecordList.get(2).get("error")).isEqualTo("Значение registered_at выходит за рамки у " +
      "account_number = " + clientAccountRecordList.get(2).get("account_number") +
      ". Дата регистрации больше сегодняшней. ГОСДЕП не предоставлял ЦРУ таких секретных технологий");

    List<Map<String, Object>> clientAccountTransactionRecordList =
      toListMap("SELECT * FROM " + oneFrsFile.tmpClientAccountTransactionTableName + " " +
        "WHERE error IS NOT NULL ORDER BY record_no");
    assertThat(clientAccountTransactionRecordList).hasSize(3);
    assertThat(clientAccountTransactionRecordList.get(0).get("error")).isEqualTo("Неправильный формат даты registered_at у " +
      "account_number = " + clientAccountTransactionRecordList.get(0).get("account_number"));


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
