package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.report.client_list.ClientListReportView;

public interface ReportRegister {

  void generate(ClientListReportView reportView);
}
