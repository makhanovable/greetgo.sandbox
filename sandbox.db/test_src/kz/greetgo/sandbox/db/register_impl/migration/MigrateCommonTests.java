package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigrateCommonTests extends ParentTestNg {

  public BeanGetter<MigrationController> migrationController;
  public BeanGetter<MigrationTestDao> migrationTestDao;

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
}
