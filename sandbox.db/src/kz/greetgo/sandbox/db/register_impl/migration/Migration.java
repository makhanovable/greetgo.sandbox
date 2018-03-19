package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import kz.greetgo.sandbox.db.register_impl.migration.exception.UnsupportedFileExtension;
import kz.greetgo.sandbox.db.util.DateUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Migration {

  private final Logger logger = Logger.getLogger("MIGRATION");

  @SuppressWarnings("WeakerAccess")
  protected MigrationConfig config;

  protected Connection connection;

  // FIXME: 3/19/18 Закончи с кодом енума
  @SuppressWarnings("WeakerAccess")
  protected Map<TmpTableName, String> tableNames = new HashMap<>();

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

    logger.info("STARTED DATE,TIME: " + DateUtils.getDateWithTimeString(new Date()));

    String migrationStatus = "STARTED";
    try {
      createTempTablesWithLogging();
      parseFileAndUploadToTempTablesWithLogging();
      markErrorsAndUpsertIntoDbValidRowsWithLogging();
      loadErrorsAndWriteWithLogging();

      migrationStatus = "MIGRATED";
    } catch (Exception e) {
      migrationStatus = "FAILED";
      throw e;
    } finally {
      logger.info("FILE FINISHED DATE,TIME: " + DateUtils.getDateWithTimeString(new Date()));
      logger.info("FILE MIGRATION STATUS: " + migrationStatus);
      logger.info("///////////////////////////////////////////////////////////////");

    }
  }

  private void createTempTablesWithLogging() throws SQLException {
    logger.info("step1. creating temp table starting");
    Long start = System.currentTimeMillis();
    createTempTables();
    logger.info("step1. duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), start));

  }

  private void parseFileAndUploadToTempTablesWithLogging() throws Exception {
    logger.info("step2. parsing file and insert to temp tables");
    try {
      Long start = System.currentTimeMillis();
      parseFileAndUploadToTempTables();
      if (logger.isInfoEnabled())
        logger.info("step2. duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), start));

    } catch (BatchUpdateException bux) {
      logger.fatal("parseFileAndUploadToTempTablesWithLogging", bux.getNextException());
      throw bux.getNextException();
    }
  }

  private void markErrorsAndUpsertIntoDbValidRowsWithLogging() throws Exception {
    logger.info("step3. mark error and upserting valids to oper db");
    Long start = System.currentTimeMillis();
    markErrorsAndUpsertIntoDbValidRows();
    if (logger.isInfoEnabled())
      logger.info("step3. duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), start));

  }

  private void loadErrorsAndWriteWithLogging() throws Exception {
    logger.info("step4. getting and uploading errors");
    Long start = System.currentTimeMillis();
    loadErrorsAndWrite();
    if (logger.isInfoEnabled())
      logger.info("step4. duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), start));
  }

  @SuppressWarnings("WeakerAccess")
  protected void execSql(String sql) throws SQLException {

    for (Map.Entry<TmpTableName, String> tmpTableName : tableNames.entrySet()) {
      sql = sql.replace(tmpTableName.getKey().code, tmpTableName.getValue());
    }

    try (Statement statement = connection.createStatement()) {
      Long sqlStartedMils = 0L;

      if (logger.isDebugEnabled()) {
        logger.debug("executing sql query:\n" + sql);
        sqlStartedMils = System.currentTimeMillis();
      }
      statement.execute(sql);

      if (logger.isDebugEnabled())
        logger.debug("duration: " + DateUtils.getTimeDifferenceStringFormat(System.currentTimeMillis(), sqlStartedMils) + "\n");
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
    return 50000;
  }


  // FIXME: 3/19/18 проверить используется ли буфферед райтер
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
