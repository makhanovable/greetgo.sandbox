package kz.greetgo.sandbox.controller.register.report.client_list;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.util.RND;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static kz.greetgo.sandbox.controller.register.report.client_list.font.FontFactory.FONT_NAME_ROBOTO;
import static kz.greetgo.sandbox.controller.register.report.client_list.font.FontFactory.getRoboto;

public class ClientListReportViewPdf extends PdfPageEventHelper implements ClientListReportView {

  private OutputStream outputStream;
  private Document document;
  private PdfPTable table;
  private Font defaultFont;
  private long rowIterator;

  private static final long TABLE_FLUSH_TIME = 1000;

  public ClientListReportViewPdf(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void start(ReportHeaderData headerData) throws Exception {
    document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, outputStream);
    writer.setPageEvent(this);
    document.open();
    BaseFont baseFont = BaseFont.createFont(FONT_NAME_ROBOTO, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true,
      IOUtils.toByteArray(getRoboto()), null);

    defaultFont = new Font(baseFont, 10);
    document.add(this.paragraphBuilder("Список клиентских записей", defaultFont, 10f));

    Font headerFont = new Font(baseFont, 10);
    float[] columnWidthWeights = {4, 4, 2, 3, 3, 3};
    table = new PdfPTable(columnWidthWeights);
    table.setWidthPercentage(100);
    table.setComplete(false);

    table.addCell(this.pdfPCellDefault(new PdfPCell(new Phrase("ФИО", headerFont))));
    table.addCell(this.pdfPCellDefault(new PdfPCell(new Phrase("Характер", headerFont))));
    table.addCell(this.pdfPCellHeaderBuilder("Возраст",
      headerData.columnSortType == ColumnSortType.AGE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Общий остаток счетов",
      headerData.columnSortType == ColumnSortType.TOTALACCOUNTBALANCE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Максимальный остаток",
      headerData.columnSortType == ColumnSortType.MAXACCOUNTBALANCE, headerFont));
    table.addCell(this.pdfPCellHeaderBuilder("Минимальный остаток",
      headerData.columnSortType == ColumnSortType.MINACCOUNTBALANCE, headerFont));

    this.pdfPCellDefault(table.getDefaultCell()).setBackgroundColor(GrayColor.GRAYWHITE);

    rowIterator = 0;
  }

  @Override
  public void append(ReportItemData itemData) throws Exception {
    table.addCell(new Phrase(itemData.fullname, defaultFont));
    table.addCell(new Phrase(itemData.charmName, defaultFont));
    table.addCell(new Phrase(String.valueOf(itemData.age), defaultFont));
    table.addCell(new Phrase(String.format(Util.floatFormat, itemData.totalAccountBalance), defaultFont));
    table.addCell(new Phrase(String.format(Util.floatFormat, itemData.maxAccountBalance), defaultFont));
    table.addCell(new Phrase(String.format(Util.floatFormat, itemData.minAccountBalance), defaultFont));

    rowIterator++;
    if (rowIterator == TABLE_FLUSH_TIME) {
      document.add(table);
      rowIterator = 0;
    }
  }

  @Override
  public void finish(ReportFooterData footerData) throws Exception {
    table.setComplete(true);
    document.add(table);
    document.add(this.paragraphBuilder("Сформирован для пользователя: " + footerData.createdBy, defaultFont, 10f));
    document.add(this.paragraphBuilder("Дата: " +
      new SimpleDateFormat(Util.reportDatePattern).format(footerData.createdAt), defaultFont, 10f));
    document.close();

  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    try {
      addFooter(writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void addFooter(PdfWriter writer) throws Exception {
    ColumnText.showTextAligned(writer.getDirectContent(),
      Element.ALIGN_CENTER, new Phrase("" + writer.getPageNumber()), 300f, 20f, 0);
  }

  private Paragraph paragraphBuilder(String text, Font font, float spaceAfter) {
    Paragraph paragraph = new Paragraph(text, font);
    paragraph.setSpacingAfter(spaceAfter);
    return paragraph;
  }

  private PdfPCell pdfPCellHeaderBuilder(String text, boolean sort, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    if (sort)
      cell.setBackgroundColor(new BaseColor(102, 144, 102));
    else
      cell.setBackgroundColor(new BaseColor(191, 191, 191));

    return this.pdfPCellDefault(cell);
  }

  private PdfPCell pdfPCellDefault(PdfPCell cell) {
    cell.setUseAscender(true);
    cell.setUseDescender(true);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    return cell;
  }

  public static void main(String[] args) throws Exception {
    File file = new File("build/report/ClientListReportViewPdf.pdf");
    file.getParentFile().mkdirs();

    long startTime = System.currentTimeMillis();

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      ClientListReportView view = new ClientListReportViewPdf(outputStream);

      ReportHeaderData headerData = new ReportHeaderData();
      headerData.columnSortType = ColumnSortType.AGE;
      view.start(headerData);

      ReportItemData reportItemData = new ReportItemData();
      for (int i = 0; i < 500; i++) {
        reportItemData.fullname = RND.str(RND.plusInt(30) + 10);
        reportItemData.age = RND.plusInt(100) + 18;
        reportItemData.charmName = RND.str(30) + 10;
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
