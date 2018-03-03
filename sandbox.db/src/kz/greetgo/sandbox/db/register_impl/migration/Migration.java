package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.configs.DbConfig;

import java.io.OutputStream;
import java.io.File;
import java.util.IllegalFormatException;
import java.util.regex.Pattern;

public abstract class Migration {

  MigrationConfig config;
  public File toMigrate;
  public File errors;
  public int batchSize = 50_000;

  protected Migration(MigrationConfig config) {
    this.config = config;
  }

  protected abstract void createTempTables();

  protected abstract void parseFileAndUploadToTempTables();

  protected abstract void updateErrorRows();

  protected abstract void loadErrorsAndWrite();

  public void migrate() {
    createTempTables();
    parseFileAndUploadToTempTables();
    updateErrorRows();
    loadErrorsAndWrite();
  }

  public static Migration initMigration(MigrationConfig config) throws Exception {
    Pattern cia = Pattern.compile("from_cia_(.*).xml.tar.bz2");
    Pattern frs = Pattern.compile("from_frs_(.*).json_row.txt.tar.bz2");

    Migration migration = null;

    if (cia.matcher(config.originalFileName).matches()) {

      return new MigrationCia(config);
    } else if (frs.matcher(config.originalFileName).matches()) {

      return new MigrationFrs(config);
    } else {
      throw new Exception("unsupported");
    }

  }


}
