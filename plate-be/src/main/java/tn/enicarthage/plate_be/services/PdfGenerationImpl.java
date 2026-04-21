package tn.enicarthage.plate_be.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class PdfGenerationImpl implements PdfGenerationService {

    private static final DeviceRgb HEADER_BG    = new DeviceRgb(220, 220, 220);
    private static final DeviceRgb COLOR_HIGH   = new DeviceRgb(200, 50,  50);
    private static final DeviceRgb COLOR_MEDIUM = new DeviceRgb(220, 140, 0);
    private static final DeviceRgb COLOR_LOW    = new DeviceRgb(50,  150, 50);
    private static final DeviceRgb COLOR_BLUE   = new DeviceRgb(0,   80,  160);
    private static final float     BORDER       = 0.5f;
    private static final String    DATE_FORMAT  = "dd/MM/yyyy";

    // ── Fonts (loaded once per generation) ───────────────────────────────────
    private record Fonts(PdfFont bold, PdfFont regular, PdfFont italic, PdfFont boldItalic) {
        static Fonts load() throws IOException {
            return new Fonts(
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE),
                    PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE)
            );
        }
    }

    @Override
    public byte[] generateDemandePrixPdf(List<TicketResponseDTO> tickets, int dpNumber) throws IOException {
        var baos = new ByteArrayOutputStream();
        var pdf  = new PdfDocument(new PdfWriter(baos));
        var doc  = new Document(pdf, PageSize.A4);
        doc.setMargins(25, 35, 25, 35);

        var f    = Fonts.load();
        var year = String.valueOf(LocalDate.now().getYear());
        var sdf  = new SimpleDateFormat(DATE_FORMAT);

        addHeader(doc, f);
        addTitle(doc, f, dpNumber, year);
        addAddressBlock(doc, f);
        addIntroText(doc, f, dpNumber, year, computeDeadline(tickets, sdf));
        addTicketsTable(doc, f, tickets, sdf);
        addNotes(doc, f);
        addAmountLine(doc, f);
        addSignatures(doc, f);
        addFooter(doc, f);

        doc.close();
        return baos.toByteArray();
    }

    private void addHeader(Document doc, Fonts f) throws IOException {
        var table = new Table(new float[]{2.2f, 1.6f, 2.2f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        var left = noBorderCell(TextAlignment.CENTER);
        left.add(para("Ministère de l'Enseignement Supérieur", f.bold(), 7, 0));
        left.add(para("et de la Recherche Scientifique", f.bold(), 7, 4));
        left.add(para("Université de Carthage", f.bold(), 7.5f, 4));
        left.add(para("Ecole Nationale d'Ingénieurs de Carthage", f.bold(), 7, 0));
        table.addCell(left);

        var center = noBorderCell(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        center.add(para("ENICarthage", f.bold(), 13, 2).setFontColor(COLOR_BLUE));
        center.add(para("Ecole Nationale d'Ingénieurs de Carthage", f.italic(), 6, 0).setFontColor(COLOR_BLUE));
        table.addCell(center);

        var right = noBorderCell(TextAlignment.CENTER);
        right.add(para("وزارة التعليم العالي و البحث العلمي", f.bold(), 7, 0));
        right.add(para("جامعة قرطاج", f.bold(), 8, 4));
        right.add(para("المدرسة الوطنية للمهندسين بقرطاج", f.bold(), 7, 0));
        table.addCell(right);

        doc.add(table);
        doc.add(new LineSeparator(new SolidLine(BORDER)).setMarginTop(4).setMarginBottom(8));
    }

    private void addTitle(Document doc, Fonts f, int dpNumber, String year) {
        doc.add(para(String.format("DEMANDE DE PRIX N° DP-%02d/%s", dpNumber, year), f.bold(), 14, 12)
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addAddressBlock(Document doc, Fonts f) {
        var table = new Table(new float[]{3f, 2f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(BORDER))
                .setMarginBottom(10);

        var addrLeft = new Cell(4, 1).setBorder(new SolidBorder(BORDER)).setPadding(8)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        addrLeft.add(para("A l'attention de Monsieur le Gérant de la Société", f.boldItalic(), 9.5f, 8));
        addrLeft.add(para("............................................................................", f.regular(), 8, 0));
        table.addCell(addrLeft);

        for (String label : new String[]{"Matricule fiscale:", "N° Tél:", "N° Fax:", "E-Mail:"}) {
            table.addCell(new Cell().setBorder(new SolidBorder(BORDER))
                    .setPaddingLeft(6).setPaddingTop(3).setPaddingBottom(3)
                    .add(para(label + "  .........................", f.italic(), 8, 0)));
        }
        doc.add(table);
    }

    private void addIntroText(Document doc, Fonts f, int dpNumber, String year, String deadline) {
        doc.add(para(
                "Nous vous prions de bien vouloir nous communiquer votre meilleure offre de prix " +
                        "relative à la prestation de services techniques (intervention / réparation) pour les " +
                        "équipements signalés en panne ci-dessous, conformément aux caractéristiques et urgences indiquées.",
                f.italic(), 9, 6));

        doc.add(para(
                "Votre offre de prix, sous plis fermé, devra parvenir à l'Ecole Nationale d'Ingénieurs " +
                        "De Carthage « ENICarthage » – 45 rue des entrepreneurs – Charguia2 - 2035 Tunis Carthage " +
                        "et devra comprendre la mention « Ne pas Ouvrir – DP N° " + dpNumber + "/" + year +
                        " » au plus tard le " + deadline,
                f.italic(), 9, 12));
    }

    private void addTicketsTable(Document doc, Fonts f, List<TicketResponseDTO> tickets, SimpleDateFormat sdf) {
        var table = new Table(new float[]{0.7f, 2.5f, 2f, 1.1f, 1.1f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(BORDER))
                .setMarginBottom(10);

        for (String h : new String[]{"Réf.", "Désignation / Panne", "Description", "Priorité", "Date limite"}) {
            table.addHeaderCell(new Cell()
                    .setBackgroundColor(HEADER_BG).setBorder(new SolidBorder(BORDER)).setPadding(5)
                    .setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .add(new Paragraph(h).setFont(f.bold()).setFontSize(8.5f)));
        }

        if (tickets == null || tickets.isEmpty()) {
            for (String val : new String[]{"—", "Aucun ticket", "—", "—", "—"}) {
                table.addCell(cell(val, f.regular(), 8.5f, false));
            }
        } else {
            int idx = 1;
            for (TicketResponseDTO t : tickets) {
                table.addCell(cell(t.getIdTicket() != null ? t.getIdTicket() : String.valueOf(idx), f.regular(), 8, true));

                table.addCell(new Cell().setBorder(new SolidBorder(BORDER)).setPadding(5)
                        .add(new Paragraph(nvl(t.getTitre(), "N/A")).setFont(f.bold()).setFontSize(8.5f)));

                table.addCell(new Cell().setBorder(new SolidBorder(BORDER)).setPadding(5)
                        .add(new Paragraph(nvl(t.getDescription(), "—")).setFont(f.regular()).setFontSize(8f)
                                .setFontColor(ColorConstants.DARK_GRAY)));

                var prioCell = new Cell().setBorder(new SolidBorder(BORDER)).setPadding(5)
                        .setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
                prioCell.add(new Paragraph(t.getPriorite() != null ? t.getPriorite().name() : "—")
                        .setFont(f.bold()).setFontSize(8f).setFontColor(priorityColor(t.getPriorite())));
                table.addCell(prioCell);

                table.addCell(cell(t.getDateLimite() != null ? sdf.format(t.getDateLimite()) : "—", f.regular(), 8, true));
                idx++;
            }
        }
        doc.add(table);
    }

    private void addNotes(Document doc, Fonts f) {
        doc.add(para(
                "NB : Les prix doivent être indiqués en Dinars Tunisiens Hors Taxes (HT). " +
                        "La TVA applicable est de 19%. Le dépouillement se fait par article.",
                f.italic(), 8, 6));
        doc.add(para("* Le soumissionnaire doit respecter les délais d'intervention demandés, " +
                "le cas échéant son offre sera rejetée.", f.regular(), 7.5f, 1));
        doc.add(para("* Le dépouillement se fait par article.", f.regular(), 7.5f, 10));
    }

    private void addAmountLine(Document doc, Fonts f) {
        doc.add(para("Arrêté le présent devis à la somme de (en DT/TTC) : .....................................................................",
                f.bold(), 9, 4));
        doc.add(para("................................................................................................" +
                        "................................................................................................",
                f.regular(), 8, 16));
    }

    private void addSignatures(Document doc, Fonts f) {
        var table = new Table(new float[]{1f, 1f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        var left = noBorderCell(TextAlignment.CENTER);
        left.add(para("Le Fournisseur", f.bold(), 10, 2));
        left.add(para("( Cachet & Signature )", f.italic(), 9, 0));
        table.addCell(left);

        var right = noBorderCell(TextAlignment.CENTER);
        right.add(para("La Directrice", f.bold(), 10, 2));
        right.add(para("Houda BEN ATTIA SETHOM", f.bold(), 8.5f, 2));
        right.add(para("Directrice", f.regular(), 8, 2));
        right.add(para("de l'Ecole Nationale d'Ingénieurs de Carthage", f.regular(), 7.5f, 0));
        table.addCell(right);

        doc.add(table);
    }

    private void addFooter(Document doc, Fonts f) {
        doc.add(new LineSeparator(new SolidLine(BORDER)).setMarginTop(10).setMarginBottom(4));
        doc.add(para("45, rue des entrepreneurs Charguia 2, Tunis Carthage 2035. Tél/fax 71 941 579",
                f.regular(), 7.5f, 0).setTextAlignment(TextAlignment.CENTER));
    }


    private Paragraph para(String text, PdfFont font, float size, float marginBottom) {
        return new Paragraph(text).setFont(font).setFontSize(size).setMarginBottom(marginBottom);
    }

    private Cell noBorderCell(TextAlignment alignment) {
        return new Cell().setBorder(Border.NO_BORDER).setTextAlignment(alignment);
    }

    private Cell cell(String text, PdfFont font, float size, boolean centered) {
        var c = new Cell().setBorder(new SolidBorder(BORDER)).setPadding(5)
                .add(new Paragraph(text).setFont(font).setFontSize(size));
        if (centered) c.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        return c;
    }

    private String nvl(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private String computeDeadline(List<TicketResponseDTO> tickets, SimpleDateFormat sdf) {
        Date fallback = new Date(System.currentTimeMillis() + 2L * 86_400_000);
        if (tickets == null || tickets.isEmpty()) return sdf.format(fallback);
        return sdf.format(tickets.stream()
                .map(TicketResponseDTO::getDateLimite)
                .filter(d -> d != null)
                .min(Date::compareTo)
                .orElse(fallback));
    }

    private DeviceRgb priorityColor(Object priority) {
        if (priority == null) return (DeviceRgb) ColorConstants.BLACK;
        String name = priority.toString().toUpperCase();
        if (name.contains("HAUTE") || name.contains("CRITIQUE") || name.contains("HIGH")) return COLOR_HIGH;
        if (name.contains("NORMAL") || name.contains("MEDIUM") || name.contains("MOYENNE")) return COLOR_MEDIUM;
        return COLOR_LOW;
    }
}