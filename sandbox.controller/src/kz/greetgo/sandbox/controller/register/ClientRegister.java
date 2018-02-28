package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.report.ClientReportView;

import java.util.List;

public interface ClientRegister {
  // FIXME: 2/28/18 Переименуй метод с соответствии с возвращаемым типом
  List<ClientRecord> getClientInfoList(int limit, int page, String filter, String orderBy, int desc);

  // FIXME: 2/28/18 get размер клиентов?
  int getClientsSize(String filter);

  int remove(List<String> id);

  // FIXME: 2/28/18 может быть getDetail?

  ClientDetail detail(String id);

  void addOrUpdate(ClientToSave clientDetail);

  // FIXME: 2/28/18 Все еще не понятно какой отчет
  void generateClientReport(String filter, String orderBy, int order, ClientReportView view) throws Exception;

}
