package kz.greetgo.sandbox.db.register_impl.migration_test;

import kz.greetgo.sandbox.db.register_impl.migration.MigrationCia;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MigrationCiaTest extends MigrationCia {
  public MigrationCiaTest(MigrationConfig config, Connection connection) {
    super(config, connection);
  }

  @Override
  public void migrate() {
    //do nothing
  }

  public void createTempTables() throws SQLException {
    super.createTempTables();
  }

  public void validateRows() {
    throw new NotImplementedException();
  }

  public Map<TmpTableName, String> getTableNames() {
    return tableNames;
  }

  public void parseAndInsertRows() throws Exception {
    super.parseFileAndUploadToTempTables();
  }

  public void uploadErrors() throws IOException, SQLException {
    super.loadErrorsAndWrite();
  }


  public void upsertIntoTempTables() throws SQLException {
    super.upsertIntoDbValidRows();
  }
}