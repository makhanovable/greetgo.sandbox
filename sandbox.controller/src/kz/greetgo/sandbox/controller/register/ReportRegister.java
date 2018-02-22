package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.report.ClientReportView;

public interface ReportRegister {

  void generateReport(String filter, String orderBy, int order, ClientReportView view) throws Exception;
}
