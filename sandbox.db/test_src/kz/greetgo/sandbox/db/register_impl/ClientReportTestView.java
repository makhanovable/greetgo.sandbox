package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientReportView;

import java.util.ArrayList;
import java.util.List;

public class ClientReportTestView implements ClientReportView {
  public String[] headers;
  public List<ClientRecord> rows = new ArrayList<>();

  @Override
  public void start() throws Exception {
    this.headers = headers;
  }

  @Override
  public void appendRow(ClientRecord record) throws Exception {
    this.rows.add(record);
  }

  @Override
  public void finish() throws Exception {

  }
}
