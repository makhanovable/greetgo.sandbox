package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.IdGenerator;

import java.io.File;

public class MigrationConfig {
  public boolean ready = false;
  public String id;
  public File toMigrate;
  public File error;
  public String originalFileName;
  public String afterRenameFileName;
  public int batchSize = 50_000;
  public IdGenerator idGenerator;

}
