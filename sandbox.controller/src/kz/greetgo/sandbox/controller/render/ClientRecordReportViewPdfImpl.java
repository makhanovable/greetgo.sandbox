package kz.greetgo.sandbox.controller.render;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class ClientRecordReportViewPdfImpl implements ClientRecordsReportView {

    private OutputStream out;
    public ClientRecordReportViewPdfImpl(OutputStream out) {
        this.out = out;
    }

    @Override
    public void start() {
    }

    @Override
    public void append(ClientRecord row) {
    }

    @Override
    public void finish(String user, Date created_at, String link_to_download) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        OutputStream out = new FileOutputStream(new File("C:\\Users\\makhan\\Desktop\\test.xlsx"));
        ClientRecordReportViewPdfImpl report = new ClientRecordReportViewPdfImpl(out);
        report.start();
        for (int i = 0; i < 100; i++) {
            ClientRecord record = new ClientRecord();
            record.name = RND.str(9);
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
