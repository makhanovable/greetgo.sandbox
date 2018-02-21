package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;

@Bean
public class MigrationController {

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<MigrationController> migrationController;

  private Logger logger = Logger.getLogger(MigrationController.class);

  public Connection createConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password());
  }

  public void migrateCiaFile(File ciaFile) throws Exception {
    logger.info("CIA file migration at " + Instant.now());

    MigrateOneCiaFile migrationCiaFile = new MigrateOneCiaFile();
    //migrationCiaFile.inputFile = new File(inFilePath);
    migrationCiaFile.inputFile = ciaFile;

    try (Connection connection = createConnection()) {
      String errorFilePath =
        migrationCiaFile.inputFile.getAbsolutePath() + "error_" + migrationCiaFile.inputFile.getName().replaceAll("xml", "txt");

      migrationCiaFile.outputErrorFile = new CommonErrorFileWriter(new File(errorFilePath));
      migrationCiaFile.maxBatchSize = 100_000;
      migrationCiaFile.connection = connection;

      migrationCiaFile.migrate();
    }
  }

  public void migrateFrsFile(File frsFile) throws Exception {
    logger.info("FRS file migration at " + Instant.now());

    MigrateOneFrsFile migrationFrsFile = new MigrateOneFrsFile();
    //migrationFrsFile.inputFile = new File(inFilePath);
    migrationFrsFile.inputFile = frsFile;

    String errorFilePath =
      migrationFrsFile.inputFile.getAbsolutePath() + "error_" + migrationFrsFile.inputFile.getName().replaceAll("json_row", "txt");

    migrationFrsFile.outputErrorFile = new CommonErrorFileWriter(new File(errorFilePath));
    migrationFrsFile.maxBatchSize = 100_000;
    migrationFrsFile.connection = createConnection();

    migrationFrsFile.migrate();

    migrationFrsFile.connection.close();
  }

  public BeanGetter<MigrationConfig> migrationConfig;

  public void migrate() {
    System.out.println("Hello from Migration " + migrationConfig.get());
    System.out.println(migrationConfig.get().ciaHost());
  }
}
