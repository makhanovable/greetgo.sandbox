package kz.greetgo.sandbox.controller.register.report.client_list;

import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;

public interface ClientListReportView {

  void start(ReportHeaderData headerData) throws Exception;

  void append(ReportItemData itemData) throws Exception;

  void finish(ReportFooterData footerData) throws Exception;
}
