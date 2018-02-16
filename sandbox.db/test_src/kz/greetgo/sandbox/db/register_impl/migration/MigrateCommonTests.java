package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigrateCommonTests extends ParentTestNg {

  public BeanGetter<MigrationController> migrationController;
  public BeanGetter<MigrationTestDao> migrationTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;

  protected Connection connection;

  @BeforeMethod
  public void createConnection() throws Exception {
    connection = migrationController.get().createConnection();
  }

  @AfterMethod
  public void closeConnection() throws Exception {
    connection.close();
    connection = null;
  }

  protected void createCiaFile(File inFile) throws Exception {
    try (PrintStream pr = new PrintStream(inFile, "UTF-8")) {
      pr.print("<cia>\n" +
        "  <client id=\"4-DU8-32-H7\"> <!-- Идентификаторы строковые, не длиннее 50 символов -->\n" +
        "    <surname value=\"Иванов\" />\n" +
        "    <name value=\"Иван\" />\n" +
        "    <patronymic value=\"Иваныч\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Уситчивый\" />\n" +
        "    <birth value=\"1980-11-12\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
        "    </address>\n" +
        "    \n" +
        "      <homePhone>+7-123-111-22-33</homePhone>\n" +
        "    <mobilePhone>+7-123-111-33-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-111-44-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-111-55-33</mobilePhone>\n" +
        "      <workPhone>+7-123-111-00-33 вн. 3344</workPhone>\n" +
        "      <workPhone>+7-123-111-00-33 вн. 3343</workPhone>\n" +
        "  </client>\n" +
        "\n" +
        "  <client id=\"4-DU8-32-ss\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"Пётр\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"1980-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "    \n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "    <mobilePhone>+7-123-333-33-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-444-44-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-555-55-33</mobilePhone>\n" +
        "      <workPhone>+7-123-666-00-33 вн. 3344</workPhone>\n" +
        "      <workPhone>+7-123-777-00-33 вн. 3343</workPhone>\n" +
        "  </client>\n" +
        "  \n" +
        "</cia>");
    }
  }

  protected void createCiaFileWithErrors(File inFile) throws Exception {
    try (PrintStream pr = new PrintStream(inFile, "UTF-8")) {
      pr.print("<cia>\n" +
        "  <client id=\"4-DU8-32-H7\">\n" +
        "    <surname value=\"\" />\n" +
        "    <name value=\"Иван\" />\n" +
        "    <patronymic value=\"Иваныч\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Уситчивый\" />\n" +
        "    <birth value=\"1980-11-12\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"23A\" flat=\"22\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-111-22-33</homePhone>\n" +
        "  </client>\n" +
        "\n" +
        "  <client id=\"4-DU8-32-ss\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"1980-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  \n" +
        "  <client id=\"4-DU8-32-GG\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"TEST\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  \n" +
        "  <client id=\"4-DU8-30-GG\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"TEST\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"11-11-2000\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  <client id=\"4-DU8-30-GG\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"TEST\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"1000-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  <client id=\"4-DU8-11-GG\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"TEST\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"2017-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  <client id=\"4-DU6-09-GG\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"TEST\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"2020-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "  </client>\n" +
        "  <client id=\"4-DA1-32-ss\">\n" +
        "    <surname value=\"Петров\" />\n" +
        "    <name value=\"Пётр\" />\n" +
        "    <patronymic value=\"Петрович\" />\n" +
        "    <gender value=\"MALE\" />\n" +
        "    <charm value=\"Агрессивный\" />\n" +
        "    <birth value=\"1980-11-13\" />\n" +
        "    <address>\n" +
        "      <fact street=\"Никонова\" house=\"6\" flat=\"22\" />\n" +
        "      <register street=\"Панфилова\" house=\"13A\" flat=\"12\" />\n" +
        "    </address>\n" +
        "    \n" +
        "      <homePhone>+7-123-333-22-33</homePhone>\n" +
        "    <mobilePhone>+7-123-333-33-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-444-44-33</mobilePhone>\n" +
        "    <mobilePhone>+7-123-555-55-33</mobilePhone>\n" +
        "      <workPhone>+7-123-666-00-33 вн. 3344</workPhone>\n" +
        "      <workPhone>+7-123-777-00-33 вн. 3343</workPhone>\n" +
        "  </client>\n" +
        "  \n" +
        "</cia>");
    }
  }

  protected void createFrsFile(File inFile) throws Exception {
    try (PrintStream pr = new PrintStream(inFile, "UTF-8")) {
      pr.print("{\"type\": \"transaction\",\"money\": \"+123_000_000_098.13\"," +
        "\"finished_at\": \"2010-01-23T11:56:11.987\",\"transaction_type\": \"Перечисление с госбюджета\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n" +
        "{\"type\": \"transaction\",\"money\": \"-23_000_000_034.17\",\"finished_at\": \"2010-01-23T11:56:11.987\"," +
        "\"transaction_type\": \"Вывод средств в офшоры\",\"account_number\": \"32134KZ343-43546-535436-77656\"}\n" +
        "{\"type\": \"new_account\",\"client_id\": \"4-DU8-32-H7\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\",\"registered_at\": \"2011-01-23T23:22:11.456\"}\n");
    }
  }

  protected void createFrsFileWithErrors(File inFile) throws Exception {
    try (PrintStream pr = new PrintStream(inFile, "UTF-8")) {
      pr.print("{\"type\": \"trannsaction\",\"money\": \"+123_000_000_09u,13\"," +
        "\"finished_at\": \"2010-01-23T11:56:11.987\",\"transaction_type\": \"Перечисление с госбюджета\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");
      pr.print("{\"type\": \"transaction\",\"money\": \"+123_000_000_098,13\"," +
        "\"finished_at\": \"2010-01-23T11:56:11.987\",\"transaction_type\": \"Перечисление с госбюджета\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");
      pr.print("{\"type\": \"transaction\",\"money\": \"abc\"," +
        "\"finished_at\": \"2010-01-23T11:56:11.987\",\"transaction_type\": \"Вывод средств в офшоры\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");
      pr.print("{\"type\": \"transaction\",\"money\": \"-123_000_000_098.13\"," +
        "\"finished_at\": \"1010-01-23T11:56:11.987\",\"transaction_type\": \"Вывод средств в офшоры\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");
      pr.print("{\"type\": \"transaction\",\"money\": \"-123_000_000_098.13\"," +
        "\"finished_at\": \"3010-01-23T11:56:11.987\",\"transaction_type\": \"Вывод средств в офшоры\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");
      pr.print("{\"type\": \"transaction\",\"money\": \"-123_000_000_098.13\"," +
        "\"finished_at\": \"abc\",\"transaction_type\": \"Вывод средств в офшоры\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\"}\n");

      pr.print("{\"type\": \"new_acccount\",\"client_id\": \"4-DU8-32-H7\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\",\"registered_at\": \"2011-01-23T23:22:11.456\"}\n");
      pr.print("{\"type\": \"new_account\",\"client_id\": \"4-DU8-32-H7\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\",\"registered_at\": \"1011-01-23T23:22:11.456\"}\n");
      pr.print("{\"type\": \"new_account\",\"client_id\": \"4-DU8-32-H7\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\",\"registered_at\": \"3011-01-23T23:22:11.456\"}\n");
      pr.print("{\"type\": \"new_account\",\"client_id\": \"4-DU8-32-H7\"," +
        "\"account_number\": \"32134KZ343-43546-535436-77656\",\"registered_at\": \"\"}\n");
    }
  }

  protected List<Map<String, Object>> toListMap(String sql) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        List<Map<String, Object>> ret = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        while (rs.next()) {
          ret.add(rsToMap(fields, rs));
        }
        return ret;
      }
    }
  }

  private Map<String, Object> rsToMap(List<String> fields, ResultSet rs) throws SQLException {
    if (fields.isEmpty()) {
      int columnCount = rs.getMetaData().getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        fields.add(rs.getMetaData().getColumnName(i));
      }
    }

    Map<String, Object> ret = new HashMap<>();
    for (String field : fields) {
      ret.put(field, rs.getObject(field));
    }
    return ret;
  }

  long createClient(String ciaId) {
    int charmId = clientTestDao.get().selectSeqIdNextValueTableCharm();
    clientTestDao.get().insertCharm(charmId, RND.str(16), null, null);

    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().updateClientWithCiaId(id, RND.str(10), RND.str(10), RND.str(10),
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Date.valueOf("2000-01-01"), charmId, ciaId);

    return id;
  }

  void resetAllTables() {
    this.resetClientAddrTable();
    this.resetClientPhoneTable();
    this.resetClientAccountTable();
    this.resetClientTable();
    this.resetCharmTable();
    this.resetTransactionTypeTable();
  }

  void resetClientPhoneTable() {
    migrationTestDao.get().deleteAllTableClientPhone();
  }

  void resetClientAddrTable() {
    migrationTestDao.get().deleteAllTableClientAddr();
  }

  void resetClientAccountTable() {
    migrationTestDao.get().deleteAllTableClientAccount();
  }

  void resetCharmTable() {
    migrationTestDao.get().deleteAllTableCharm();
  }

  void resetClientTable() {
    migrationTestDao.get().deleteAllTableClient();
  }

  void resetTransactionTypeTable() {
    migrationTestDao.get().deleteAllTableTransactionType();
  }
}
