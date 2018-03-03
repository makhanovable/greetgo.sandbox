package kz.greetgo.sandbox.db.register_impl.migration;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MigrationCia extends Migration {


  public MigrationCia(MigrationConfig config) {
    super(config);
  }

  @Override
  protected void createTempTables() {
    throw new NotImplementedException();
  }

  @Override
  protected void parseFileAndUploadToTempTables() {
    throw new NotImplementedException();
  }

  @Override
  protected void updateErrorRows() {
    throw new NotImplementedException();
  }

  @Override
  protected void loadErrorsAndWrite() {
    throw new NotImplementedException();
  }

}
