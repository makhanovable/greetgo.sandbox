package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;

import java.util.ArrayList;
import java.util.List;

public class ErrorFileWriterTest implements ErrorFile {
  public List<String> errorList = new ArrayList<>();

  @Override
  public void appendErrorLine(String line) {
    errorList.add(line);
  }

  @Override
  public long finish() {
    return 0;
  }
}
