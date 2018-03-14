package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.exception.UnsupportedFileExtension;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
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
  protected Migration(MigrationConfig config, Connection connection) {
    this.config = config;
    this.connection = connection;
  }

  protected abstract void createTempTables() throws SQLException;

  protected abstract void parseFileAndUploadToTempTables() throws Exception;

  protected abstract void markErrorsAndUpsertIntoDbValidRows() throws SQLException;

  protected abstract void loadErrorsAndWrite() throws SQLException, IOException;

  public void migrate() throws Exception {
    createTempTables();
    parseFileAndUploadToTempTables();
    markErrorsAndUpsertIntoDbValidRows();
    loadErrorsAndWrite();
  }

  @SuppressWarnings("WeakerAccess")
  protected void execSql(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {

      for (String tableName : tableNames.keySet()) {
        sql = sql.replaceAll(tableName, tableNames.get(tableName));
      }
      statement.execute(sql);
    }
  }

  public static Migration getMigrationInstance(MigrationConfig config, Connection connection) throws Exception {
    Pattern cia = Pattern.compile(getCiaFileNamePattern());
    Pattern frs = Pattern.compile(getFrsFileNamePattern());

    if (cia.matcher(config.originalFileName).matches()) {
      return new MigrationCia(config, connection);
    } else if (frs.matcher(config.originalFileName).matches()) {
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
  protected static String getCurrentTimeInMillsString() {
    return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
  }

  @SuppressWarnings("WeakerAccess")
  public static int getMaxBatchSize() {
    return 5000;
  }


  @SuppressWarnings("WeakerAccess")
  protected void writeErrors(String[] columns, String tableName, FileWriter writer) throws SQLException, IOException {
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
