package kz.greetgo.sandbox.controller.report;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientReportPDF implements ClientReport {

  private Map<String, String> charms;
  private Document document;
  private PdfPTable table;

  public ClientReportPDF(OutputStream out, String[] headers) throws Exception {
    document = new Document(PageSize.A4.rotate(), -50, -50, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(document, out);
    document.open();
    table = new PdfPTable(headers.length);
    table.setHeaderRows(1);
    table.setSplitRows(false);
    table.setComplete(false);
    for (String header : headers)
      table.addCell(header);
  }

  @Override
  public void appendRows(List<ClientRecord> records) throws Exception {

    for (ClientRecord clientRecord : records) {
      table.addCell(clientRecord.id);
      table.addCell(clientRecord.name);
      table.addCell(clientRecord.surname);
      table.addCell(clientRecord.patronymic);
      table.addCell(String.valueOf(clientRecord.age));
      table.addCell(this.charms.get(clientRecord.charm));
      table.addCell(String.valueOf(clientRecord.totalAccountBalance));
      table.addCell(String.valueOf(clientRecord.maximumBalance));
      table.addCell(String.valueOf(clientRecord.minimumBalance));
    }
    document.add(table);
  }

  @Override
  public void finish() throws Exception {
    table.setComplete(true);
    document.add(table);
    document.close();
  }

  public void setCharms(Map<String, String> charms) {
    this.charms = charms;
  }

  public static void main(String[] args) throws Exception {
    OutputStream out = new FileOutputStream("test.pdf");
    String[] headers = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};
    ClientReportPDF clientReportPDF = new ClientReportPDF(out, headers);
    Map<String, String> charms = new HashMap<>();
    charms.put("1", "lazy");
    clientReportPDF.setCharms(charms);
    List<ClientRecord> list = new ArrayList<>();
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = "myId";
    clientRecord.name = "myName";
    clientRecord.surname = "mySurname";
    clientRecord.patronymic = "myPatronymic";
    clientRecord.age = 16;
    clientRecord.charm = "1";
    clientRecord.totalAccountBalance = 100f;
    clientRecord.maximumBalance = 1100f;
    clientRecord.minimumBalance = 11100f;
    for (int i = 0; i < 1000; i++)
      list.add(clientRecord);
    clientReportPDF.appendRows(list);
    clientReportPDF.finish();
    out.close();
  }

}
