package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.configs.DbConfig;

import java.io.OutputStream;

public abstract class Migration {

  protected DbConfig operDb;

  public int batchSize = 50_000;
  public OutputStream errorOS;

  protected Migration(DbConfig operDb) {
    this.operDb = operDb;
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

}
