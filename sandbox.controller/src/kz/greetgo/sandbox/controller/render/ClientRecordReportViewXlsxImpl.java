package kz.greetgo.sandbox.controller.render;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.util.RND;

import java.io.*;
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
        try {
            sheet.skipRow();
            sheet.row().start();
            sheet.cellStr(1, "Ссылка для скачивания: " + link_to_download);
            sheet.row().finish();
            sheet.row().start();
            sheet.cellStr(1, "Отчет сформировал(-а): " + user);
            sheet.row().finish();
            sheet.row().start();
            sheet.cellStr(1, created_at.toString());
            sheet.row().finish();
            xlsx.complete(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("results/tables/simple.xlsx");
        file.getParentFile().mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            ClientRecordsReportView view = new ClientRecordReportViewXlsxImpl(outputStream);
            view.start();
            for (int i = 0; i < 100; i++) {
                ClientRecord clientRecord = new ClientRecord();
                clientRecord.name = RND.str(10) + " " + RND.str(10) + " " + RND.str(10);
                clientRecord.charm = RND.str(10);
                clientRecord.total = RND.plusInt(1000) * 1.1f;
                clientRecord.max = RND.plusInt(1000) * 1.1f;
                clientRecord.min = RND.plusInt(1000) * 1.1f;
                clientRecord.age = RND.plusInt(100);
                view.append(clientRecord);
            }
            view.finish("Маханов Мадияр", new Date(), "vk.com/btvrfedsf");
        }
    }

}
