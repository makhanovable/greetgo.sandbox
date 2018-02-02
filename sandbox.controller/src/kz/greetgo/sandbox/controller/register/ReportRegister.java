package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;

public interface ReportRegister {

  /**
   * Сохраняет информацию о предстоящей сессии для загрузки отчета и возвращает идентификатор отчета
   *
   * @param reportInstance
   * @return сгенерированный идентификатор отчета
   */
  String saveClientListReportInstance(ClientListReportInstance reportInstance) throws Exception;

  ClientListReportInstance getClientListReportInstance(String reportInstanceId) throws Exception;

  void generateClientListReport(ClientListReportInstance reportInstance, ClientListReportView view) throws Exception;
}
