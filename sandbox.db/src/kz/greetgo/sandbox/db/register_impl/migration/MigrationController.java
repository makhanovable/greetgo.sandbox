package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.error.CommonErrorFileWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;

@Bean
public class MigrationController implements Job {

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

    String errorFilePath =
      migrationCiaFile.inputFile.getAbsolutePath() + "error_" + migrationCiaFile.inputFile.getName().replaceAll("xml", "txt");

    migrationCiaFile.outputErrorFile = new CommonErrorFileWriter(new File(errorFilePath));
    migrationCiaFile.maxBatchSize = 100_000;
    migrationCiaFile.connection = createConnection();

    migrationCiaFile.migrate();

    migrationCiaFile.connection.close();
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

  @Override
  public void execute(JobExecutionContext context) {
    try {
      String dirPath = System.getProperty("user.home") + "/migration";
      File dir = new File(dirPath);
      if (!dir.exists())
        dir.mkdir();

      if (dir.canRead()) {
        for (File file : dir.listFiles()) {
          if (file.isFile()) {
            String fileName = file.getName();

            switch (FilenameUtils.getExtension(fileName)) {
              case "xml": {
                migrationController.get().migrateCiaFile(file);
                break;
              }
              case "json_row": {
                migrationController.get().migrateFrsFile(file);
                break;
              }
              default: {
                logger.warn("There are redundant files in the migration directory " + dirPath);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
