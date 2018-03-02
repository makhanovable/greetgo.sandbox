package kz.greetgo.sandbox.controller.report;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import kz.greetgo.sandbox.controller.util.ClientUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class ClientReportViewPDF implements ClientReportView {

  private Document document;
  private OutputStream out;
  private PdfPTable table;
  private int count = 0;

  public ClientReportViewPDF(OutputStream out) throws Exception {
    this.out = out;
  }

  @Override
  public void start() throws Exception {
    document = new Document(PageSize.A4, -30, -30, 30, 30);
    PdfWriter.getInstance(document, out);
    document.open();
    String[] headers = ClientUtils.reportHeaders;
    table = new PdfPTable(headers.length);
    table.setHeaderRows(1);
    table.setSplitRows(false);
    table.setComplete(false);
    for (String header : headers)
      table.addCell(header);
  }

  @Override
  public void appendRow(ClientRecord record) throws Exception {

    table.addCell(record.surname + " " + record.name + " " + record.patronymic);
    table.addCell(record.charm);
    table.addCell(String.valueOf(record.age));
    table.addCell(String.valueOf(record.totalAccountBalance));
    table.addCell(String.valueOf(record.maximumBalance));
    table.addCell(String.valueOf(record.minimumBalance));

    if (count++ % 1000 == 0) {
      document.add(table);
    }
  }

  @Override
  public void finish() throws Exception {
    table.setComplete(true);
    document.add(table);
    document.close();
  }


  public static void main(String[] args) throws Exception {
    OutputStream out = new FileOutputStream("test.pdf");
    ClientReportViewPDF clientReportPDF = new ClientReportViewPDF(out);
    clientReportPDF.start();
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = "вампвапвып";
    clientRecord.name = "вфыывавыа";
    clientRecord.surname = "mySurname";
    clientRecord.patronymic = "myPatronymic";
    clientRecord.age = 16;
    clientRecord.charm = "1";
    clientRecord.totalAccountBalance = 100f;
    clientRecord.maximumBalance = 1100f;
    clientRecord.minimumBalance = 11100f;
    for (int i = 0; i < 1000; i++)
      clientReportPDF.appendRow(clientRecord);
    clientReportPDF.finish();
    out.close();
  }

}
