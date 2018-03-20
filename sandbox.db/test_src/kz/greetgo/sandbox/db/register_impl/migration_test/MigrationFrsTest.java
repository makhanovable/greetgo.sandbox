package kz.greetgo.sandbox.db.register_impl.migration_test;

import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationFrs;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MigrationFrsTest extends MigrationFrs {
  public MigrationFrsTest(MigrationConfig config, Connection connection) {
    super(config, connection);
  }


  @Override
  public void migrate() {
    //do nothing
  }

  public void ParseAndInsertIntoTempTables() throws Exception {
    super.parseFileAndUploadToTempTables();
  }

  public void createTempTables() throws SQLException {
    super.createTempTables();
  }


  public Map<TmpTableName, String> getTableNames() {
    return tableNames;
  }

  public void markErrors() throws SQLException {
    super.markErrorRows();
  }

  public void upsertIntoTempTables() throws SQLException {
    super.upsertIntoDbValidRows();
  }
}
