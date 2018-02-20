package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ClientReportXLSX implements ClientReport {

  Map<String, String> charmMap = null;
  SXSSFWorkbook wb = new SXSSFWorkbook(-1);
  Sheet sh = wb.createSheet();
  int rownum = 0;

  private ClientReportXLSX() {
    this.appendRow("id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance");
  }

  public ClientReportXLSX(Map<String, String> charms) {
    this();
    charmMap = charms;
  }

  // stores data on disk as temporary files, not memory
  @Override
  public void appendData(List<ClientRecord> records) throws IOException {
    for (ClientRecord r : records) {
      String charmname = charmMap.get(r.charm);
      this.appendRow(r.id, r.name, r.surname, r.patronymic, r.age, charmname, r.totalAccountBalance, r.maximumBalance, r.minimumBalance);
    }

    ((SXSSFSheet) sh).flushRows();
  }
  
  //writes all corresponding temporary files to outputStream, then deletes temporary files
  @Override
  public void write(OutputStream out) throws IOException {
    wb.write(out);
    wb.dispose();
  }

  private void appendRow(Object... values) {
    Row row = sh.createRow(rownum++);
    for (int i = 0; i < values.length; i++)
      if (values[i] != null)
        row.createCell(i).setCellValue(values[i].toString());
      else
        row.createCell(i).setCellValue("");

  }
}
