package kz.greetgo.sandbox.db.register_impl.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Migration {

  @SuppressWarnings("WeakerAccess")
  protected MigrationConfig config;

  protected Connection connection;

  @SuppressWarnings("WeakerAccess")
  protected Map<String, String> tableNames = new HashMap<>();

  @SuppressWarnings("WeakerAccess")
  protected Migration(MigrationConfig config) {
    this.config = config;
  }

  protected abstract void createTempTables() throws SQLException;

  protected abstract void parseFileAndUploadToTempTables() throws Exception;

  protected abstract void MarkErrorsAndUpsertIntoDbValidRows() throws SQLException;

  protected abstract void loadErrorsAndWrite() throws SQLException;

  public void migrate(Connection connection) throws Exception {
    this.connection = connection;
    createTempTables();
    parseFileAndUploadToTempTables();
    MarkErrorsAndUpsertIntoDbValidRows();
//    loadErrorsAndWrite();
  }

  @SuppressWarnings("WeakerAccess")
  protected void execSql(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {

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
    return new Date().getTime() + "";
//    return new SimpleDateFormat("dd_mm_yyyy_hh_mm_ss").format(new Date());
  }

  @SuppressWarnings("WeakerAccess")
  public static int getMaxBatchSize() {
    return 5000;
  }

}
