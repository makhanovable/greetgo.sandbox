package kz.greetgo.sandbox.controller.render;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class ClientRecordReportViewXlsxImpl implements ClientRecordsReportView {

    private OutputStream out;
    private Xlsx xlsx;
    private Sheet sheet;
    public ClientRecordReportViewXlsxImpl(OutputStream out) {
        this.out = out;
    }

    @Override
    public void start() {
        xlsx = new Xlsx();
        sheet = xlsx.newSheet(true);
        sheet.row().start();
        sheet.cellStr(1, "Отчет");
        sheet.row().finish();

        sheet.skipRow();
        sheet.row().start();
        sheet.cellStr(1, "ФИО");
        sheet.cellStr(2, "Характер");
        sheet.cellStr(3, "Возраст");
        sheet.cellStr(4, "Общий остаток счетов");
        sheet.cellStr(5, "Максимальный остаток");
        sheet.cellStr(6, "Минимальный остаток");
        sheet.row().finish();
    }

    @Override
    public void append(ClientRecord row) {
        sheet.row().start();
        sheet.cellStr(1, row.name);
        sheet.cellStr(2, row.charm);
        sheet.cellInt(3, row.age);
        sheet.cellDouble(4, row.total);
        sheet.cellDouble(5, row.max);
        sheet.cellDouble(6, row.min);
        sheet.row().finish();
    }

    @Override
    public void finish(String user, Date created_at, String link_to_download) {
        sheet.skipRow();
        sheet.row().start().height(100);
        sheet.cellStr(1, "Сформирован: " + user);
        sheet.cellStr(2, created_at.toString());
        sheet.row().finish();
        sheet.row().start();
        sheet.cellStr(1, "Ссылка на отчет: " + link_to_download);
        sheet.row().finish();

        xlsx.complete(out);
    }

    public static void main(String[] args) throws FileNotFoundException {
        OutputStream out = new FileOutputStream(new File("C:\\Users\\makhan\\Desktop\\test.xlsx"));
        ClientRecordReportViewXlsxImpl report = new ClientRecordReportViewXlsxImpl(out);
        report.start();
        for (int i = 0; i < 100; i++) {
            ClientRecord record = new ClientRecord();
            record.name = RND.str(10);
            record.age = RND.plusInt(100);
            record.charm = RND.str(10);
            record.total = RND.plusInt(1000) * 1.1f;
            record.max = RND.plusInt(1000) * 1.1f;
            record.min = RND.plusInt(1000) * 1.1f;
            report.append(record);
        }
        report.finish("Маханов Мадияр", new Date(), "vk.com/dnfjvbisdnohbvfidscvnds");
        System.out.println("Done");
    }

}
