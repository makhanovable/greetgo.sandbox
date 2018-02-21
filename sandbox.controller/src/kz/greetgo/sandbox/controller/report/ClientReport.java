package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ClientReport {
  void appendRows(List<ClientRecord> records) throws Exception;

  void finish() throws Exception;
}
