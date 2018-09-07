package cl.cbrs.aio.certificado;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class HeaderNotasHelper extends PdfPageEventHelper {

    protected PdfTemplate tpl;
    protected BaseFont helv;

    protected PdfPTable headerTable;

    public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            // initializations
            tpl = writer.getDirectContent().createTemplate(200, 20);
            Rectangle rect = new Rectangle(0, 0, 150, 18);
            rect.setBackgroundColor(Color.GRAY);
            tpl.setBoundingBox(rect);
            tpl.rectangle(rect);
            helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            // header
            headerTable = new PdfPTable(1);
           
            //BaseFont bf = BaseFont.createFont("c:/jboss/bin/verdana.ttf",  BaseFont.IDENTITY_H, true);
            //Font font = new Font(bf, 14);
            //font.setStyle(Font.BOLD);
            Font font = new Font(Font.COURIER, 14, Font.BOLD);
            Paragraph par= new Paragraph("    Subinscripciones y Notas Marginales",font);
            par.setAlignment(Paragraph.ALIGN_CENTER);
            PdfPCell cell = new PdfPCell(par);
            //cell.setBottom(1);
            cell.setBorder(PdfCell.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingBottom(10);
            headerTable.addCell(cell);
            headerTable.setTotalWidth(document.right() - document.left());
            headerTable.setLockedWidth(true);
            headerTable.setSpacingAfter(100);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            // Header
           headerTable.writeSelectedRows(0, -1, document.leftMargin(), document.top()
                    + headerTable.getTotalHeight(), writer.getDirectContent());
            // Footer
            PdfPTable footerTable = new PdfPTable(2);
           /* PdfPCell cell1 = new PdfPCell(new Phrase("page " + writer.getPageNumber()));
            footerTable.addCell(cell1);*/
            PdfPCell cell2 = new PdfPCell(Image.getInstance(tpl));
            footerTable.addCell(cell2);
            footerTable.setTotalWidth(document.right() - document.left());
            footerTable.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),
                    writer.getDirectContent());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        tpl.beginText();
        tpl.setFontAndSize(helv, 12);
        tpl.setTextMatrix(2, 4);
        tpl.showText("Number of pages = " + (writer.getPageNumber() - 1));
        tpl.endText();
    }

}
