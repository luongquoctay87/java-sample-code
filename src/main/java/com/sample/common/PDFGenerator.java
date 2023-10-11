package com.sample.common;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PDFGenerator {

    private String pdfDir = "./";
    private String reportFileName = "Employee-Report";
    private String reportFileNameDateFormat = "dd_MMMM_yyyy";
    private String localDateFormat = "dd MMMM yyyy HH:mm:ss";
    private String logoImgPath = "./src/main/resources/template/icon.png";
    private Float[] logoImgScale = {50f, 50f};
    private String currencySymbol = "$";
    private int noOfColumns = 4;
    private List<String> columnNames = List.of("Emp Id", "Emp Name", "Emp Dept", "Emp Sal");

    private static Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private static Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    private static Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    public void generatePdfReport(HttpServletResponse response) {

        Document document = new Document();

        try {
            // setting for API request
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + getPdfNameWithDate();
            response.setContentType("application/octet-stream");
            response.setHeader(headerKey, headerValue);
            PdfWriter.getInstance(document, response.getOutputStream());

            // setting for locally test
            // PdfWriter.getInstance(document, new FileOutputStream(getPdfNameWithDate()));
            document.open();
            addLogo(document);
            addDocTitle(document);
            createTable(document, noOfColumns);
            addFooter(document);
            document.close();
            System.out.println("------------------Your PDF Report is ready!-------------------------");

        } catch (FileNotFoundException | DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addLogo(Document document) {
        try {
            Image img = Image.getInstance(logoImgPath);
            img.scalePercent(logoImgScale[0], logoImgScale[1]);
            img.setAlignment(Element.ALIGN_RIGHT);
            document.add(img);
        } catch (DocumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addDocTitle(Document document) throws DocumentException {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
        Paragraph p1 = new Paragraph();
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph(reportFileName, COURIER));
        p1.setAlignment(Element.ALIGN_CENTER);
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph("Report generated on " + localDateString, COURIER_SMALL));

        document.add(p1);

    }

    private void createTable(Document document, int noOfColumns) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(noOfColumns);

        for (int i = 0; i < noOfColumns; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(columnNames.get(i)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            table.addCell(cell);
        }

        table.setHeaderRows(1);
        getDbData(table);
        document.add(table);
    }

    private void getDbData(PdfPTable table) {

        for (int i = 0; i < 10; i++) {

            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell("id_" + i);
            table.addCell("name-" + i);
            table.addCell("Dept" + i);
            table.addCell(currencySymbol + 1000 * i);
        }

    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph p2 = new Paragraph();
        leaveEmptyLine(p2, 3);
        p2.setAlignment(Element.ALIGN_MIDDLE);
        p2.add(new Paragraph(
                "------------------------End Of " + reportFileName + "------------------------",
                COURIER_SMALL_FOOTER));

        document.add(p2);
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private String getPdfNameWithDate() {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(reportFileNameDateFormat));
        return pdfDir + reportFileName + "-" + localDateString + ".pdf";
    }
}
