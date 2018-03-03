package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.configs.DbConfig;

import java.io.File;

public class MigrationConfig {
  public boolean ready = false;
  public String id;
  public File toMigrate;
  public File error;
  public DbConfig dbConfig;
  public String originalFileName;
  public String afterRenameFileName;

}
