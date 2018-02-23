package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import kz.greetgo.sandbox.db.register_impl.migration.report.MigrationSimpleReport;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;

@Bean
public class MigrationController {

  public BeanGetter<DbConfig> dbConfig;

  private Logger logger = Logger.getLogger(MigrationController.class);

  public Connection createConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password());
  }

  public boolean migrateOneCiaFile(InputStream is, String filename, File errorFile, File reportFile) {
    logger.info("Начало миграции CIA файла " + filename + ". Файл ошибки " + errorFile.getAbsolutePath());

    try (Connection connection = createConnection()) {
      MigrationSimpleReport migrationSimpleReport = new MigrationSimpleReport(reportFile);
      CommonErrorFileWriter commonErrorFileWriter = new CommonErrorFileWriter(errorFile);

      MigrateOneCiaFile migrationCiaFile = new MigrateOneCiaFile();
      migrationCiaFile.inputStream = is;
      migrationCiaFile.migrationSimpleReport = migrationSimpleReport;
      migrationCiaFile.outputErrorFile = commonErrorFileWriter;
      migrationCiaFile.maxBatchSize = 100_000;
      migrationCiaFile.connection = connection;

      migrationSimpleReport.start();
      long init = System.currentTimeMillis();

      migrationCiaFile.migrate();

      long post = System.currentTimeMillis();
      long errorCount = commonErrorFileWriter.finish();
      migrationSimpleReport.finish((post - init) / 1000f, errorCount, "");

      logger.info("Конец миграции CIA файла " + filename +
        ". Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init) + " секунд");
    } catch (Exception e) {
      logger.error("Ошибка миграции CIA файла " + filename, e);
      return false;
    }

    return true;
  }

  public boolean migrateOneFrsFile(InputStream is, String filename, File errorFile, File reportFile) {
    logger.info("Начало миграции FRS файла " + filename + ". Файл ошибки " + errorFile.getAbsolutePath());

    try (Connection connection = createConnection()) {
      MigrationSimpleReport migrationSimpleReport = new MigrationSimpleReport(reportFile);
      CommonErrorFileWriter commonErrorFileWriter = new CommonErrorFileWriter(errorFile);

      MigrateOneFrsFile migrationFrsFile = new MigrateOneFrsFile();
      migrationFrsFile.inputStream = is;
      migrationFrsFile.migrationSimpleReport = migrationSimpleReport;
      migrationFrsFile.outputErrorFile = commonErrorFileWriter;
      migrationFrsFile.maxBatchSize = 100_000;
      migrationFrsFile.connection = connection;

      migrationSimpleReport.start();
      long init = System.currentTimeMillis();

      migrationFrsFile.migrate();

      long post = System.currentTimeMillis();
      long errorCount = commonErrorFileWriter.finish();
      migrationSimpleReport.finish((post - init) / 1000f, errorCount, "");

      logger.info("Конец миграции FRS файла " + filename +
        ". Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init) + " секунд");
    } catch (Exception e) {
      logger.error("Ошибка миграции FRS файла " + filename, e);
      return false;
    }

    return true;
  }
}
