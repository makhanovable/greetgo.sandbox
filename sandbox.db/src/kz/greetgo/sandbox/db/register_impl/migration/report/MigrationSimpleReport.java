package kz.greetgo.sandbox.db.register_impl.migration.report;

import kz.greetgo.util.RND;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.io.File;
import java.time.LocalDateTime;

public class MigrationSimpleReport {

  private File file;
  private Sheet sheet;
  SpreadSheet spreadSheet;

  private int lineNo = 1;
  private int queryCount = 0;

  public MigrationSimpleReport(File file) {
    this.file = file;
  }

  public void start() {
    spreadSheet = SpreadSheet.create(1, 4, 64);
    sheet = spreadSheet.getSheet(0);

    sheet.setValueAt("Время выполнения", 1, lineNo);
    sheet.setValueAt("Действие", 2, lineNo++);
  }

  public void appendParseInfo(float time) throws Exception {
    sheet.setValueAt(time, 1, lineNo);
    sheet.setValueAt("Парсинг", 2, lineNo++);
    sheet.setValueAt("", 1, lineNo++);
  }

  public void append(float time, String sqlQuery) throws Exception {
    sheet.setValueAt(time, 1, lineNo);
    sheet.setValueAt(sqlQuery, 2, lineNo++);
    sheet.setValueAt("", 1, lineNo++);
    queryCount++;
  }

  public void finish(float time, long errorCount, String endTitle) throws Exception {
    sheet.setValueAt("", 1, lineNo++);

    sheet.setValueAt("Общее время выполнения: " + time + " секунд", 1, lineNo++);
    sheet.setValueAt("Количество запросов: " + queryCount, 1, lineNo++);
    sheet.setValueAt("Количество ошибок: " + errorCount, 1, lineNo++);

    sheet.setValueAt("", 1, lineNo++);

    sheet.setValueAt(endTitle, 1, lineNo++);
    sheet.setValueAt("Дата: " + LocalDateTime.now(), 1, lineNo++);

    spreadSheet.saveAs(file);
  }

  public static void main(String[] args) throws Exception {
    File file = new File("build/report/SimpleMigrationReport.ods");
    file.getParentFile().mkdirs();

    MigrationSimpleReport migrationSimpleReport = new MigrationSimpleReport(file);

    migrationSimpleReport.start();
    for (int i = 0; i < 10; i++)
      migrationSimpleReport.append((float) RND.plusDouble(30, 3), "SELECT SHMELEKT");
    migrationSimpleReport.finish(5.5f, 100, "");
  }
}
