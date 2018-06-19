package kz.greetgo.sandbox.controller.render;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.util.RND;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ClientRecordReportViewPdfImpl implements ClientRecordsReportView {

    private OutputStream out;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public ClientRecordReportViewPdfImpl(OutputStream out) {
        this.out = out;
    }

        private String FONT_LOCATION = "c:/Windows/Fonts/arial.ttf";
    private static final String DEST = "results/tables/simple.html";
    private PdfPTable table;
    /**
     * vfdsc
     * dcsd
     * vsdsd
     * vsdvfef
     * vrevdfds
     * vewrvdsa
     * vredfsavcf
     * vfevtbe
     */
    private Document document;
    private BaseFont baseFont;
    private Font font, font2;

//    public static void main(String[] args) throws Exception{
//        File file = new File(DEST);
//        file.getParentFile().mkdirs();
//
//        try (FileOutputStream outputStream = new FileOutputStream(file)) {
//            ClientRecordReportViewPdfImpl report = new ClientRecordReportViewPdfImpl(outputStream);
//            report.start();
//        }
//    }

    @Override
    public void start() {
        try {
//            StringBuilder sb = new StringBuilder();
//            sb.append("<html>");
//            sb.append(" <body>");
//            sb.append("     <h1>My Report</h1>");
//            sb.append(" </body>");
//            sb.append("</html>");

//            File file = new File(DEST);
//            file.getParentFile().mkdirs();
            baseFont = BaseFont.createFont(FONT_LOCATION, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            font = new Font(baseFont, 7, Font.NORMAL);
            font2 = new Font(baseFont, 10, Font.NORMAL);

            document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
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

            finish(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void append(ClientRecord row) {
//        table.addCell(new Phrase(row.name, font));
//        table.addCell(new Phrase(row.charm, font));
//        table.addCell(new Phrase(row.age + "", font));
//        table.addCell(new Phrase(row.total + "", font));
//        table.addCell(new Phrase(row.max + "", font));
//        table.addCell(new Phrase(row.min + "", font));
    }

    @Override
    public void finish(String user, Date created_at, String link_to_download) {
        try {
            out.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            document.add(table);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        document.close();
    }

}
