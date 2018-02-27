package kz.greetgo.sandbox.db.register_impl.migration.report;

import kz.greetgo.util.RND;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MigrationSimpleReport {

  private File file;
  private Sheet sheet;
  private SpreadSheet spreadSheet;

  private int lineNo = 1;

  private List<ActionInfo> actionInfoList = new ArrayList<>();

  public MigrationSimpleReport(File file) {
    this.file = file;
  }

  public void start() {
    spreadSheet = SpreadSheet.create(1, 196, 128);
    sheet = spreadSheet.getSheet(0);
  }

  public void addAction(float execTime, int updatedRecordCount, String action) {
    ActionInfo actionInfo = new ActionInfo();
    actionInfo.execTime = execTime;
    actionInfo.action = action;
    actionInfo.updatedRecordCount = updatedRecordCount;

    actionInfoList.add(actionInfo);
  }

  public void finish(float time, long errorCount) throws IOException {
    Color infoColor = new Color(228, 213, 183);
    setCurRowBgColor(32, infoColor);
    sheet.getColumn(1).setWidth(10f);
    sheet.setValueAt("#", 1, lineNo);
    sheet.getColumn(2).setWidth(48f);
    sheet.setValueAt("Время выполнения, сек", 2, lineNo);
    sheet.getColumn(3).setWidth(56);
    sheet.setValueAt("Кол-во обновленных записей", 3, lineNo);
    sheet.setValueAt("Действие", 4, lineNo++);

    actionInfoList.sort((ai1, ai2) -> Float.compare(ai2.execTime, ai1.execTime));

    for (int i = 0; i < actionInfoList.size(); i++) {
      ActionInfo actionInfo = actionInfoList.get(i);
      iterateCurRowBgColor(32);
      sheet.setValueAt(i + 1 + "", 1, lineNo);
      sheet.setValueAt(actionInfo.execTime + "", 2, lineNo);
      sheet.setValueAt(actionInfo.updatedRecordCount >= 0 ? actionInfo.updatedRecordCount + "" : "Неприменимо",
        3, lineNo);
      sheet.setValueAt(actionInfo.action, 4, lineNo++);
    }

    sheet.setValueAt("", 1, lineNo++);

    setCurRowBgColor(3, infoColor);
    sheet.setValueAt("Общее время выполнения: " + time + " секунд", 1, lineNo++);
    setCurRowBgColor(3, infoColor);
    sheet.setValueAt("Количество действий: " + actionInfoList.size(), 1, lineNo++);
    setCurRowBgColor(3, infoColor);
    sheet.setValueAt("Количество ошибок: " + errorCount, 1, lineNo++);

    sheet.setValueAt("", 1, lineNo++);

    setCurRowBgColor(3, infoColor);
    sheet.setValueAt("Дата: " + LocalDateTime.now(), 1, lineNo++);

    spreadSheet.saveAs(file);
  }

  private static final Color[] colors = {
    new Color(255, 236, 148),
    new Color(255, 174, 174),
    new Color(255, 240, 170),
    new Color(176, 229, 124),
    new Color(180, 216, 231),
    new Color(86, 186, 236)
  };

  private void iterateCurRowBgColor(int colNum) {
    setCurRowBgColor(colNum, colors[lineNo % colors.length]);
  }

  private void setCurRowBgColor(int colNum, Color color) {
    for (int i = 1; i < colNum + 1; i++)
      sheet.getCellAt(i, lineNo).setBackgroundColor(color);
  }

  public static void main(String[] args) throws IOException {
    File file = new File("build/report/SimpleMigrationReport.ods");
    file.getParentFile().mkdirs();

    MigrationSimpleReport migrationSimpleReport = new MigrationSimpleReport(file);

    migrationSimpleReport.start();
    for (int i = 0; i < 10; i++)
      migrationSimpleReport.addAction((float) RND.plusDouble(30, 3), -1, "SELECT SHMELEKT");
    migrationSimpleReport.finish(5.5f, 100);
  }
}
