package kz.greetgo.sandbox.controller.render;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.io.*;
import java.util.Date;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.util.RND;

public class ClientRecordReportViewPdfImpl implements ClientRecordsReportView {

    private Font font;
    private OutputStream out;
    private PdfPTable table;
    private Document document;

    public ClientRecordReportViewPdfImpl(OutputStream out) {
        this.out = out;
    }

    @Override
    public void start() {
        try {
            BaseFont baseFont = BaseFont.createFont("c:/Windows/Fonts/arial.ttf",
                    BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            font = new Font(baseFont, 7, Font.NORMAL);
            Font font2 = new Font(baseFont, 10, Font.NORMAL);

            document = new Document();
            PdfWriter.getInstance(document, out); // TODO for test
//            PdfWriter.getInstance(document, new FileOutputStream("results/tables/simple.pdf"));
            document.open();

            Paragraph p = new Paragraph("Отчет", font2);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(10f);
            document.add(p);
            table = new PdfPTable(6);

            table.addCell(new Phrase("ФИО", font));
            table.addCell(new Phrase("Характер", font));
            table.addCell(new Phrase("Возраст", font));
            table.addCell(new Phrase("Общий остаток счетов", font));
            table.addCell(new Phrase("Максимальный остаток", font));
            table.addCell(new Phrase("Минимальный остаток", font));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void append(ClientRecord row) {
        table.addCell(new Phrase(row.name, font));
        table.addCell(new Phrase(row.charm, font));
        table.addCell(new Phrase(row.age + "", font));
        table.addCell(new Phrase(row.total + "", font));
        table.addCell(new Phrase(row.max + "", font));
        table.addCell(new Phrase(row.min + "", font));
    }

    @Override
    public void finish(String user, Date created_at, String link_to_download) {
        try {
            PdfPCell userName = new PdfPCell(new Phrase("Отчет сформировал(-а): " + user, font));
            userName.setBorder(Rectangle.NO_BORDER);
            PdfPCell date = new PdfPCell(new Phrase(created_at.toString(), font));
            date.setBorder(Rectangle.NO_BORDER);
            PdfPCell link = new PdfPCell(new Phrase("Ссылка для скачивания: " + link_to_download, font));
            link.setBorder(Rectangle.NO_BORDER);

            document.add(table);
            table = new PdfPTable(1);
            table.addCell(link);
            table.addCell(userName);
            table.addCell(date);
            document.add(table);
            out.flush();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("results/tables/simple.pdf");
        file.getParentFile().mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            ClientRecordsReportView view = new ClientRecordReportViewPdfImpl(outputStream);
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
            view.finish("Маханов Мадияр", new Date(),"vk.com/btvrfedsf");
        }
    }

}
