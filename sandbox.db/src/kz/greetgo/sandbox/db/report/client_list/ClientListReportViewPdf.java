package kz.greetgo.sandbox.db.report.client_list;

import kz.greetgo.sandbox.controller.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportItemData;

import java.io.OutputStream;

public class ClientListReportViewPdf implements ClientListReportView {

  private final OutputStream outputStream;

  public ClientListReportViewPdf(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void start(ReportHeaderData headerData) {

  }

  @Override
  public void append(ReportItemData itemData) {

  }

  @Override
  public void finish(ReportFooterData footerData) {

  }

  public static void main(String args[]) {
    /*File file = new File("build/report/client_list/test.pdf");
    file.getParentFile().mkdirs();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      ClientListReportViewPdf pdfView = new ClientListReportViewPdf(outputStream);

      ClientListReportInData inData = new ClientListReportInData();

      pdfView.generate(inData);
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }
}
