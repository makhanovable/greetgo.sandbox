package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
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

  public boolean migrateOneCiaFile(InputStream is, String filename, File errorFile) {
    logger.info("Начало миграции CIA файла " + filename + ". Файл ошибки " + errorFile.getAbsolutePath());

    try (Connection connection = createConnection()) {
      long init = System.currentTimeMillis();
      MigrateOneCiaFile migrationCiaFile = new MigrateOneCiaFile();
      migrationCiaFile.inputStream = is;

      migrationCiaFile.outputErrorFile = new CommonErrorFileWriter(errorFile);
      migrationCiaFile.maxBatchSize = 100_000;
      migrationCiaFile.connection = connection;

      migrationCiaFile.migrate();
      long post = System.currentTimeMillis();

      logger.info("Конец миграции CIA файла " + filename +
        ". Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init) + " секунд");
    } catch (Exception e) {
      logger.error("Ошибка миграции CIA файла " + filename, e);
      return false;
    }

    return true;
  }

  public boolean migrateOneFrsFile(InputStream is, String filename, File errorFile) {
    logger.info("Начало миграции FRS файла " + filename + ". Файл ошибки " + errorFile.getAbsolutePath());

    try (Connection connection = createConnection()) {
      long init = System.currentTimeMillis();
      MigrateOneFrsFile migrationFrsFile = new MigrateOneFrsFile();
      migrationFrsFile.inputStream = is;

      migrationFrsFile.outputErrorFile = new CommonErrorFileWriter(errorFile);
      migrationFrsFile.maxBatchSize = 100_000;
      migrationFrsFile.connection = connection;

      migrationFrsFile.migrate();
      long post = System.currentTimeMillis();

      logger.info("Конец миграции FRS файла " + filename +
        ". Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init) + " секунд");
    } catch (Exception e) {
      logger.error("Ошибка миграции FRS файла " + filename, e);
      return false;
    }

    return true;
  }
}
