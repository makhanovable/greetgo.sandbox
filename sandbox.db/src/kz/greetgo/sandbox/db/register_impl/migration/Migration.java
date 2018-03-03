package kz.greetgo.sandbox.db.register_impl.migration;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Migration {

  @SuppressWarnings("WeakerAccess")
  protected MigrationConfig config;

  private Connection connection;

  @SuppressWarnings("WeakerAccess")
  protected Map<String, String> tableNames = new HashMap<>();

  @SuppressWarnings("WeakerAccess")
  protected Migration(MigrationConfig config) {
    this.config = config;
  }

  protected abstract void createTempTables() throws SQLException;

  protected abstract void parseFileAndUploadToTempTables() throws IOException, SAXException;

  protected abstract void updateErrorRows();

  protected abstract void loadErrorsAndWrite();

  public void migrate(Connection connection) throws Exception {
    this.connection = connection;
    createTempTables();
    parseFileAndUploadToTempTables();
    updateErrorRows();
    loadErrorsAndWrite();
  }

  @SuppressWarnings("WeakerAccess")
  protected void execSql(StringBuilder sb) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      String sql = sb.toString();

      for (String key : tableNames.keySet()) {
        sql = sql.replaceAll(key, tableNames.get(key));
      }
      statement.execute(sql);
    }
  }

  public static Migration getMigrationInstance(MigrationConfig config) throws Exception {
    Pattern cia = Pattern.compile(getCiaFileNamePattern());
    Pattern frs = Pattern.compile(getFrsFileNamePattern());

    if (cia.matcher(config.originalFileName).matches()) {
      return new MigrationCia(config);

    } else if (frs.matcher(config.originalFileName).matches()) {
      return new MigrationFrs(config);

    } else {
      throw new Exception("unsupported");
    }
  }

  public static String getCiaFileNamePattern() {
    return "from_cia_(.*).xml.tar.bz2";
  }

  public static String getFrsFileNamePattern() {
    return "from_frs_(.*).json_row.txt.tar.bz2";
  }

  @SuppressWarnings("WeakerAccess")
  protected static String getCurrentDateString() {
    return new SimpleDateFormat("dd_mm_yyyy_hh_mm_ss").format(new Date());
  }

}
