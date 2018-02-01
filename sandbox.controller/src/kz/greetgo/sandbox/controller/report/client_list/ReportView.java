package kz.greetgo.sandbox.controller.report.client_list;

import kz.greetgo.sandbox.controller.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportItemData;

public interface ReportView {

  void start(ReportHeaderData headerData);

  void append(ReportItemData itemData);

  void finish(ReportFooterData footerData);
}
