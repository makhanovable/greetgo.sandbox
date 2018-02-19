package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

@Bean
public class MigrationController {

  public BeanGetter<DbConfig> dbConfig;

  public Connection createConnection() throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password());
  }

  public void migrate() throws Exception {
    //... .... ...

    MigrateOneCiaFile ciaFile = new MigrateOneCiaFile();
    ciaFile.inputFile = null;///
    ciaFile.outputErrorFile = null;///
    ciaFile.maxBatchSize = 100_000;
    ciaFile.connection = null;///

    ciaFile.migrate();

    // ... .... ...

    MigrateOneFrsFile frsFile = new MigrateOneFrsFile();
    frsFile.inputFile = null;
    frsFile.outputErrorFile = null;
    frsFile.maxBatchSize = 1000;
    frsFile.connection = null;
  }

}
