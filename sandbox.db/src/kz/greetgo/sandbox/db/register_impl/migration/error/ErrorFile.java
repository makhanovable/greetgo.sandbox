package kz.greetgo.sandbox.db.register_impl.migration.error;

public interface ErrorFile {
  void appendErrorLine(String line);

  long finish();
}
