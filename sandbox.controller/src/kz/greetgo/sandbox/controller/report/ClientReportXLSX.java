package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientReportXLSX implements ClientReport {

  private OutputStream out;

  private Map<String, String> charmMap = null;
  private SXSSFWorkbook wb = new SXSSFWorkbook(-1);
  private Sheet sh = wb.createSheet();
  private int rownum = 0;
  private String[] headers;

  @SuppressWarnings("ConfusingArgumentToVarargsMethod")
  public ClientReportXLSX(OutputStream out, String[] headers) {
    this.out = out;
    this.headers = headers;
    if (headers != null)
      this.appendRow(headers);

    ((SXSSFSheet) sh).trackAllColumnsForAutoSizing();
  }

  // stores data on disk as temporary files, not memory
  @Override
  public void appendRows(List<ClientRecord> records) throws IOException {
    for (ClientRecord r : records) {
      String charmname = charmMap.get(r.charm);
      this.appendRow(r.id, r.name, r.surname, r.patronymic, r.age, charmname, r.totalAccountBalance, r.maximumBalance, r.minimumBalance);
    }

    ((SXSSFSheet) sh).flushRows();
  }

  //writes all corresponding temporary files to outputStream, then deletes temporary files
  @Override
  public void finish() throws IOException {
    for (int i = 0; i < headers.length; i++)
      sh.autoSizeColumn(i);
    wb.write(out);
    wb.dispose();
  }

  private void appendRow(Object... values) {
    Row row = sh.createRow(rownum++);
    for (int i = 0; i < values.length; i++)
      row.createCell(i).setCellValue(values[i] == null ? "" : values[i].toString());
  }

  public void setCharms(Map<String, String> charmMap) {
    this.charmMap = charmMap;
  }

  public static void main(String[] args) throws Exception {
    OutputStream out = new FileOutputStream("test.xlsx");
    String[] headers = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};

    ClientReportXLSX clientReportPDF = new ClientReportXLSX(out, headers);
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
