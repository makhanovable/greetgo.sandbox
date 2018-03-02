package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.configs.DbConfig;

import java.io.OutputStream;

public abstract class Migration {

  private DbConfig operDb;

  public int portionSize;
  public int uploadMaxBatchSize;
  public OutputStream errors;

  protected Migration(DbConfig operDb) {
    this.operDb = operDb;
  }

  protected abstract void createTempTables();

  protected abstract void loadFileToTempTables();

  protected abstract void updateErrorRows();

  protected abstract void uploadErrorsAndWrite();

  public void migrate() {
    createTempTables();
    loadFileToTempTables();
    updateErrorRows();
    uploadErrorsAndWrite();
  }

}
