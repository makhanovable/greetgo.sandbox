package kz.greetgo.sandbox.controller.register.report.client_list;

import kz.greetgo.msoffice.xlsx.gen.Align;
import kz.greetgo.msoffice.xlsx.gen.NumFmt;
import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class ClientListReportViewXlsx implements ClientListReportView {

  private OutputStream outputStream;
  private Xlsx document;
  private Sheet sheet;

  public ClientListReportViewXlsx(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void start(ReportHeaderData headerData) throws Exception {
    document = new Xlsx();
    sheet = document.newSheet(true);

    int curCol = 1;
    sheet.setWidth(curCol++, 45f);
    sheet.setWidth(curCol++, 20f);
    sheet.setWidth(curCol++, 15f);
    sheet.setWidth(curCol++, 25f);
    sheet.setWidth(curCol++, 25f);
    sheet.setWidth(curCol, 25f);

    sheet.setDefaultRowHeight(20f);

    sheet.style().font().setSize(14);
    sheet.style().font().setItalic(true);
    sheet.row().start();
    sheet.cellStr(1, "Список клиентских записей");
    sheet.row().finish();
    sheet.style().font().setItalic(false);
    sheet.skipRow();

    sheet.style().alignment().setHorizontal(Align.center);
    sheet.style().font().setSize(12);
    sheet.style().font().setBold(true);
    curCol = 1;
    sheet.row().start();
    sheet.cellStr(curCol++, "ФИО");
    sheet.cellStr(curCol++, "Характер");
    if (headerData.columnSortType == ColumnSortType.AGE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Возраст");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Возраст");
    }
    if (headerData.columnSortType == ColumnSortType.TOTALACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Общий остаток счетов");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Общий остаток счетов");
    }
    if (headerData.columnSortType == ColumnSortType.MAXACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol++, "Максимальный остаток");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol++, "Максимальный остаток");
    }
    if (headerData.columnSortType == ColumnSortType.MINACCOUNTBALANCE) {
      sheet.style().font().setItalic(true);
      sheet.cellStr(curCol, "Минимальный остаток");
      sheet.style().font().setItalic(false);
    } else {
      sheet.cellStr(curCol, "Минимальный остаток");
    }
    sheet.row().finish();
    sheet.style().font().setBold(false);

    sheet.style().alignment().setHorizontal(Align.left);
    sheet.style().font().setSize(14);
  }

  @Override
  public void append(ReportItemData itemData) throws Exception {
    int curCol = 1;
    sheet.row().start();
    sheet.cellStr(curCol++, itemData.fullname);
    sheet.cellStr(curCol++, itemData.charmName);
    sheet.cellInt(curCol++, itemData.age);
    sheet.cellDouble(curCol++, itemData.totalAccountBalance, NumFmt.NUM_SIMPLE2);
    sheet.cellDouble(curCol++, itemData.maxAccountBalance, NumFmt.NUM_SIMPLE2);
    sheet.cellDouble(curCol, itemData.minAccountBalance, NumFmt.NUM_SIMPLE2);
    sheet.row().finish();
  }

  @Override
  public void finish(ReportFooterData footerData) throws Exception {
    sheet.style().font().setSize(14);
    sheet.row().start();
    sheet.cellStr(1, "Сформирован для пользователя: " + footerData.createdBy);
    sheet.row().finish();

    sheet.row().start();
    sheet.cellStr(1, "Дата: " + new SimpleDateFormat(Util.reportDatePattern).format(footerData.createdAt));
    sheet.row().finish();

    document.complete(outputStream);
  }

  public static void main(String[] args) throws Exception {
    File file = new File("build/report/ClientListReportViewXlsx.xlsx");
    file.getParentFile().mkdirs();

    long startTime = System.currentTimeMillis();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      ClientListReportView view = new ClientListReportViewPdf(outputStream);

      ReportHeaderData headerData = new ReportHeaderData();
      headerData.columnSortType = ColumnSortType.AGE;
      view.start(headerData);

      ReportItemData reportItemData = new ReportItemData();
      for (int i = 0; i < 500; i++) {
        reportItemData.fullname = RND.str(RND.plusInt(40) + 10);
        reportItemData.age = RND.plusInt(100) + 18;
        reportItemData.charmName = RND.str(40) + 10;
        reportItemData.totalAccountBalance = (float) RND.plusDouble(100000, Util.decimalNum) - 50000;
        reportItemData.minAccountBalance = (float) RND.plusDouble(100000, Util.decimalNum) - 50000;
        reportItemData.maxAccountBalance = (float) RND.plusDouble(100000, Util.decimalNum) - 50000;
        view.append(reportItemData);
      }

      ReportFooterData reportFooterData = new ReportFooterData();
      reportFooterData.createdBy = RND.str(30);
      reportFooterData.createdAt = RND.dateYears(1000, 3000);
      view.finish(reportFooterData);
    }

    long endTime = System.currentTimeMillis();

    System.out.println("OK " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
  }
}
