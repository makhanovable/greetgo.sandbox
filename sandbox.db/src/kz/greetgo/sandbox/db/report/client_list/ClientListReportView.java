package kz.greetgo.sandbox.db.report.client_list;

import kz.greetgo.sandbox.controller.report.client_list.ReportView;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportItemData;

public interface ClientListReportView extends ReportView {

  @Override
  void start(ReportHeaderData headerData);

  @Override
  void append(ReportItemData itemData);

  @Override
  void finish(ReportFooterData footerData);
}
