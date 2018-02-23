package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ClientReportViewXLSX implements ClientReportView {

  private OutputStream out;

  private SXSSFWorkbook wb = new SXSSFWorkbook(-1);
  private Sheet sh = wb.createSheet();
  private int rowNumber = 0;
  private int columnNumber = 0;

  public ClientReportViewXLSX(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String[] headers) throws Exception {
    this.columnNumber = headers.length;
    int i = 0;
    Row headerRow = sh.createRow(rowNumber++);
    for (String header : headers) {
      headerRow.createCell(i++).setCellValue(header);
    }
    ((SXSSFSheet) sh).trackAllColumnsForAutoSizing();
  }

  @Override
  public void appendRow(ClientRecord record) throws IOException {
    Row row = sh.createRow(rowNumber++);
    row.createCell(0).setCellValue(record.id);
    row.createCell(1).setCellValue(record.name);
    row.createCell(2).setCellValue(record.surname);
    row.createCell(3).setCellValue(record.patronymic);
    row.createCell(4).setCellValue(record.age);
    row.createCell(5).setCellValue(record.charm);
    row.createCell(6).setCellValue(record.totalAccountBalance);
    row.createCell(7).setCellValue(record.maximumBalance);
    row.createCell(8).setCellValue(record.minimumBalance);

    if (rowNumber % 1000 == 0)
      ((SXSSFSheet) sh).flushRows();
  }

  @Override
  public void finish() throws IOException {
    for (int i = 0; i < columnNumber; i++)
      sh.autoSizeColumn(i);

    wb.write(out);
    wb.dispose();
  }


  public static void main(String[] args) throws Exception {
    OutputStream out = new FileOutputStream("test.xlsx");
    String[] headers = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};

    ClientReportViewXLSX clientReportPDF = new ClientReportViewXLSX(out);
    clientReportPDF.start(headers);
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = "myId";
    clientRecord.name = "myName";
    clientRecord.surname = "mySurname";
    clientRecord.patronymic = "myPatronymic";
    clientRecord.age = 16;
    clientRecord.charm = "mycharm";
    clientRecord.totalAccountBalance = 100f;
    clientRecord.maximumBalance = 1100f;
    clientRecord.minimumBalance = 11100f;
    for (int i = 0; i < 1000; i++)
      clientReportPDF.appendRow(clientRecord);
    clientReportPDF.finish();
    out.close();
  }
}
