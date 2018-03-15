package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.exception.UnsupportedFileExtension;
import kz.greetgo.sandbox.db.util.DateUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Migration {

  private final Logger logger = Logger.getLogger("MIGRATION.CIA/FRS");

  @SuppressWarnings("WeakerAccess")
  protected MigrationConfig config;

  protected Connection connection;

  @SuppressWarnings("WeakerAccess")
  protected Map<String, String> tableNames = new HashMap<>();

  @SuppressWarnings("WeakerAccess")
  protected Migration(MigrationConfig config, Connection connection) {
    this.config = config;
    this.connection = connection;
  }

  protected abstract void createTempTables() throws SQLException;

  protected abstract void parseFileAndUploadToTempTables() throws Exception;

  protected abstract void markErrorsAndUpsertIntoDbValidRows() throws SQLException;

  protected abstract void loadErrorsAndWrite() throws SQLException, IOException;

  public void migrate() throws Exception {
    Date started = new Date();
    String date = DateUtils.getDateWithTimeString(started);

    String MigrationStatus = "STARTED";

    logger.trace("///////////////////////////////////////////////////////////////");
    logger.trace("MIGRATION STARTED, ID:" + config.id);
    logger.trace("STARTED DATE,TIME: " + date);

    try {
      createTempTables();
      parseFileAndUploadToTempTables();
      markErrorsAndUpsertIntoDbValidRows();
      loadErrorsAndWrite();

      MigrationStatus = "FINISHED SUCCESSFULLY.";
    } catch (Exception e) {
      MigrationStatus = "FAILED";
      e.printStackTrace();
      throw e;

    } finally {
      Date finished = new Date();

      String durationDateFormat = DateUtils.getTimeDifferenceStringFormat(finished.getTime(), started.getTime());

      logger.trace("FINISHED DATE,TIME: " + DateUtils.getDateWithTimeString(finished));
      logger.trace("TOTAL MIGRATION DURATION: " + durationDateFormat);
      logger.trace("MIGRATION " + MigrationStatus);
      logger.trace("///////////////////////////////////////////////////////////////");
    }
  }

  @SuppressWarnings("WeakerAccess")
  protected void execSql(String sql) throws SQLException {
    for (String tableName : tableNames.keySet()) {
      sql = sql.replaceAll(tableName, tableNames.get(tableName));
    }

    try (Statement statement = connection.createStatement()) {
      Long sqlStartedMils = System.currentTimeMillis();

      statement.execute(sql);
      logger.debug("\nexecuted sql query:\n" + sql +
        "  duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), sqlStartedMils) + "\n");
    }
  }

  public static Migration getMigrationInstance(MigrationConfig config, Connection connection) throws Exception {
    Pattern cia = Pattern.compile(getCiaFileNamePattern());
    Pattern frs = Pattern.compile(getFrsFileNamePattern());

    if (cia.matcher(config.originalFileName).matches()) {
      config.id += "CIA";
      return new MigrationCia(config, connection);
    } else if (frs.matcher(config.originalFileName).matches()) {
      config.id += "FRS";
      return new MigrationFrs(config, connection);
    } else {
      throw new UnsupportedFileExtension("unsupported file extension " + config.originalFileName);
    }
  }

  public static String getCiaFileNamePattern() {
    return "from_cia_(.*).xml.tar.bz2";
  }

  public static String getFrsFileNamePattern() {
    return "from_frs_(.*).json_row.txt.tar.bz2";
  }


  @SuppressWarnings("WeakerAccess")
  public static int getMaxBatchSize() {
    return 5000;
  }


  @SuppressWarnings("WeakerAccess")
  protected void writeErrors(String[] columns, String tableName, Writer writer) throws SQLException, IOException {
    String sql = getErrorSql(columns, tableName);
    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String line = getErrorLine(columns, rs);
        writer.write(line);
      }
    }
  }

  private String getErrorLine(String[] columns, ResultSet rs) throws SQLException {
    StringBuilder sb = new StringBuilder();
    for (String column : columns) {
      sb.append(column).append(": ").append(rs.getString(column)).append(";");
    }
    sb.append("\n");
    return sb.toString();
  }

  private String getErrorSql(String[] columns, String tableName) {
    return "select " +
      String.join(", ", columns) + "\n" +
      "from " + tableName + "\n" +
      "where error notnull";
  }


}
