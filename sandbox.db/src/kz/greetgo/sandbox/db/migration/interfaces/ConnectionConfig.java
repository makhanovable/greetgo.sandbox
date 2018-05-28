package kz.greetgo.sandbox.db.migration.interfaces;

public interface ConnectionConfig {
  String url();

  String user();

  String password();
}
