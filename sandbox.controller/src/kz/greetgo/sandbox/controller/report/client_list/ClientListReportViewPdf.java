package kz.greetgo.sandbox.controller.report.client_list;

import kz.greetgo.sandbox.controller.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.report.client_list.model.ReportItemData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ClientListReportViewPdf implements ClientListReportView {
  private FileOutputStream outputStream;

  public ClientListReportViewPdf(FileOutputStream outputStream) {
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

  public static void main(String[] args) throws Exception {
    File file = new File("build/reports/ClientListReportViewPdf.pdf");
    file.getParentFile().mkdirs();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {

      ClientListReportView view = new ClientListReportViewPdf(outputStream);

      {
        ReportHeaderData h = new ReportHeaderData();
        h.title = "Привет";
        view.start(h);
      }

      for (int i = 0; i < 10; i++) {
        ReportItemData row = new ReportItemData();
        if (i == 2) {
          row.surname = "Фамилия влдоыиа лвыа выфа вфыраи вфыа вфыа вфыа вфыа выфа выфауцйа цуа ц" + i;
        } else {
          row.surname = "Фамилия " + i;
        }

        view.append(row);
      }

      {
        ReportFooterData f = new ReportFooterData();
        f.createdBy = "Иванов И.И.";
        f.createdAt = new Date();
        view.finish(f);
      }

    }

    System.out.println("OK");
  }
}
