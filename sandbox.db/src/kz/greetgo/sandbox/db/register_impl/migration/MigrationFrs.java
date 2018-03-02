package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.configs.DbConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MigrationFrs extends Migration {
  public MigrationFrs(DbConfig operDb) {
    super(operDb);
  }

  @Override
  protected void createTempTables() {
    throw new NotImplementedException();
  }

  @Override
  protected void loadFileToTempTables() {
    throw new NotImplementedException();
  }

  @Override
  protected void updateErrorRows() {
    throw new NotImplementedException();
  }

  @Override
  protected void uploadErrorsAndWrite() {
    throw new NotImplementedException();
  }
}
