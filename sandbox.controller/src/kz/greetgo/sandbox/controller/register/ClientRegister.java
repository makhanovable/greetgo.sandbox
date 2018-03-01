package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.report.ClientReportView;

import java.util.List;

public interface ClientRegister {

  void addOrUpdate(ClientToSave clientDetail);

  ClientDetail getDetail(String id);

  int removeClients(List<String> id);

  List<ClientRecord> getClientRecordList(int limit, int page, String filter, String orderBy, int desc);

  int getNumberOfClients(String filter);

  void genClientRecordListReport(String filter, String orderBy, int desc, ClientReportView view) throws Exception;
}
