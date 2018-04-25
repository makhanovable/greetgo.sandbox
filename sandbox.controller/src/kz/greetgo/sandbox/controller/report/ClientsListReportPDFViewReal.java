package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.report.model.ClientListRow;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;

public class ClientsListReportPDFViewReal implements ClientsListReportView{
    private Document pdf_report;
    private PdfPTable table;
    private PdfPCell table_cell;
    BaseFont bf = null;
    Font font;

    public ClientsListReportPDFViewReal(OutputStream outf) {
        pdf_report = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(pdf_report, outf);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            bf = BaseFont.createFont("/Users/sanzharburumbay/Documents/Greetgo_Internship/greetgo.sandbox/sandbox.controller/src/kz/greetgo/sandbox/controller/report/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        font = new Font(bf, 12);
    }

    @Override
    public void start(String title) {
        pdf_report.open();

        table = new PdfPTable(1);

        table_cell = new PdfPCell(new Phrase(title, font));
        table.addCell(table_cell);

        try {
            pdf_report.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        float[] columnWidths = {2,10,10,10,10,10,10};
        table = new PdfPTable(columnWidths);

        table_cell = new PdfPCell(new Phrase("#", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("ФИО", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("Характер", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("Возраст", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("Общий остаток", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("Макс. остаток", font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase("Мин. остаток", font));
        table.addCell(table_cell);

        try {
            pdf_report.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void append(ClientListRow clientListRow) {
        float[] columnWidths = {2,10,10,10,10,10,10};
        table = new PdfPTable(columnWidths);

        table_cell = new PdfPCell(new Phrase(String.valueOf(clientListRow.no), font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(clientListRow.fio, font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(clientListRow.charm, font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(String.valueOf(clientListRow.age), font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(String.valueOf(clientListRow.totalCash), font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(String.valueOf(clientListRow.maxCash), font));
        table.addCell(table_cell);
        table_cell = new PdfPCell(new Phrase(String.valueOf(clientListRow.minCash), font));
        table.addCell(table_cell);

        try {
            pdf_report.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish(String userName) {
        table = new PdfPTable(1);

        table_cell = new PdfPCell(new Phrase(userName, font));
        table.addCell(table_cell);

        try {
            pdf_report.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        pdf_report.close();
    }

    public static void main(String args[]) throws Exception {
        OutputStream outf = new FileOutputStream(new File("/Users/sanzharburumbay/Downloads/test.pdf"));

        ClientsListReportPDFViewReal reportView = new ClientsListReportPDFViewReal(outf);

        reportView.start("Список клиентов");

        for (int i = 1; i <= 5; i++) {
            ClientListRow row = new ClientListRow();
            row.no = i;
            row.fio = "Asdas Aasdas Aasdasd " + i;
            row.age = 20 + i;
            row.charm = "charm" + i;
            row.totalCash = 30000 + i;
            row.maxCash = 40000 + i;
            row.minCash = 20000 + i;

            reportView.append(row);
        }

        reportView.finish("Бурумбай Санжар");

        System.out.println("OK");
    }
}
