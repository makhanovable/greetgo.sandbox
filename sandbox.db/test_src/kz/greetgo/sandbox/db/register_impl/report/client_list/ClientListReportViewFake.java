package kz.greetgo.sandbox.db.register_impl.report.client_list;

import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientListReportViewFake implements ClientListReportView {

  public ReportHeaderData headerData = null;

  @Override
  public void start(ReportHeaderData headerData) throws Exception {
    assertThat(this.headerData).isNull();
    this.headerData = headerData;
  }

  public final List<ReportItemData> list = new ArrayList<>();

  @Override
  public void append(ReportItemData itemData) throws Exception {
    list.add(itemData);
  }

  public ReportFooterData footerData = null;

  @Override
  public void finish(ReportFooterData footerData) throws Exception {
    assertThat(this.footerData).isNull();
    this.footerData = footerData;
  }
}
