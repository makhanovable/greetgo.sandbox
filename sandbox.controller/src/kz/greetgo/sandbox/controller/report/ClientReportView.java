package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ClientReportView {

  void start() throws Exception;

  void appendRow(ClientRecord record) throws Exception;

  void finish() throws Exception;
}
