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
    row.createCell(0).setCellValue(record.surname + " " + record.name + " " + record.patronymic);
    row.createCell(1).setCellValue(record.charm);
    row.createCell(2).setCellValue(record.age);
    row.createCell(3).setCellValue(record.totalAccountBalance);
    row.createCell(4).setCellValue(record.maximumBalance);
    row.createCell(5).setCellValue(record.minimumBalance);

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
    String[] headers = {"Full Name", "Charm", "Age", "Balance", "max Balance", "min Balance"};
    ClientReportViewXLSX clientReportViewXLSX = new ClientReportViewXLSX(out);
    clientReportViewXLSX.start(headers);
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
      clientReportViewXLSX.appendRow(clientRecord);
    clientReportViewXLSX.finish();
    out.close();
  }
}
