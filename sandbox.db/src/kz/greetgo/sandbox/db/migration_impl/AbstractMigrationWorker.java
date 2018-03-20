package kz.greetgo.sandbox.db.migration_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.sandbox.db.migration_impl.report.ReportXlsx;
import kz.greetgo.sandbox.db.ssh.InputFileWorker;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.util.TimeUtils.showTime;

public abstract class AbstractMigrationWorker {

  public Connection connection;
  public InputFileWorker inputFileWorker;
  public int maxBatchSize;
  public File outErrorFile;
  public ReportXlsx reportXlsx;

  public AbstractMigrationWorker(Connection connection, InputFileWorker inputFileWorker) {
    this.connection = connection;
    this.inputFileWorker = inputFileWorker;
  }

  public void migrate() throws Exception {
    long startedAt = System.nanoTime();

    createTmpTables();

    List<String> fileNamesToLoad = prepareInFiles();

    int recordsSize = parseDataAndSaveInTmpDb(fileNamesToLoad);

    handleErrors();

    migrateFromTmp();

    {
      long now = System.nanoTime();
      info("Migration of portion " + recordsSize + " finished for " + showTime(now, startedAt));
    }
  }

  protected abstract List<String> prepareInFiles() throws IOException, SftpException;

  protected abstract void handleErrors() throws SQLException, IOException, SftpException;

  protected abstract void uploadErrors() throws SQLException, IOException, SftpException;

  protected abstract void createTmpTables() throws SQLException;

  protected abstract void migrateFromTmp() throws Exception;

  protected abstract int parseDataAndSaveInTmpDb(List<String> fileDirsToLoad) throws Exception;

  protected abstract String r(String sql);

  protected List<String> renameFiles(String ext) throws IOException, SftpException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date nowDate = new Date();

    List<String> ret = new ArrayList<>();
    List<String> fileNames = inputFileWorker.getFileNames(ext);
    String regexPattern = "^[a-zA-Z0-9-_]*" + ext + "$";
    Pattern p = Pattern.compile(regexPattern);
    String processId = RND.intStr(5);

    for (String fileName : fileNames) {
      if (p.matcher(fileName).matches()) {
        String newName = fileName + "." + processId + "_" + sdf.format(nowDate);
        inputFileWorker.renameFile(fileName, newName);
        ret.add(newName);
      }
    }

    return ret;
  }

  protected void exec(String sql) throws SQLException {
    String executingSql = r(sql);

    long startedAt = System.nanoTime();
    try (Statement statement = connection.createStatement()) {
      int updates = statement.executeUpdate(executingSql);
      info("Updated " + updates
        + " records for " + showTime(System.nanoTime(), startedAt)
        + ", EXECUTED SQL : " + executingSql);
      reportXlsx.addRow(executingSql, showTime(System.nanoTime(), startedAt));
    } catch (SQLException e) {
      info("ERROR EXECUTE SQL for " + showTime(System.nanoTime(), startedAt)
        + ", message: " + e.getMessage() + ", SQL : " + executingSql);
      throw e;
    }
  }

  protected void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}