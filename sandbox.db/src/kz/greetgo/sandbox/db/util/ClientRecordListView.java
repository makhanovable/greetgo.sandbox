package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientRecordView;

import java.util.ArrayList;
import java.util.List;

public class ClientRecordListView implements ClientRecordView {
  public List<ClientRecord> list = new ArrayList<>();

  @Override
  public void start(String[] headers) throws Exception {

  }

  @Override
  public void appendRow(ClientRecord record) throws Exception {
    this.list.add(record);
  }

  @Override
  public void finish() throws Exception {

  }
}
