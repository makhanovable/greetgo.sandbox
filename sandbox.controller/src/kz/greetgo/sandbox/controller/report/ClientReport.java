package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ClientReport {
  void appendData(List<ClientRecord> records) throws IOException;

  void write(OutputStream out) throws IOException;
}
