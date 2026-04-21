package tn.enicarthage.plate_be.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class pdfGenerationImpl {

    private static final DeviceRgb TABLE_HEADER_BG = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb PRIORITY_HIGH   = new DeviceRgb(200, 50,  50);
    private static final DeviceRgb PRIORITY_MEDIUM = new DeviceRgb(220, 140, 0);
    private static final DeviceRgb PRIORITY_LOW    = new DeviceRgb(50,  150, 50);
    private static final float     BORDER_WIDTH    = 0.5f;
    private static final SimpleDateFormat SDF       = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Génère le PDF "Demande de Prix / Appel d'offres" à partir d'un ou plusieurs tickets.
     *
     * @param tickets  liste des tickets en panne
     * @param dpNumber numéro du DP (ex: 5  →  DP-05/2026)
     */
    public byte[] generateDemandePrixPdf(List<TicketResponseDTO> tickets, int dpNumber) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument  pdfDoc   = new PdfDocument(new PdfWriter(baos));
        Document     document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(25, 35, 25, 35);

        PdfFont bold      = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular   = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont italic    = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        PdfFont boldItalic= PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);

        String dpYear = String.valueOf(LocalDate.now().getYear());

        // ── 1. HEADER ────────────────────────────────────────────────────────
        Table headerTable = new Table(new float[]{2.2f, 1.6f, 2.2f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        // Left – French
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        leftCell.add(new Paragraph("Ministère de l'Enseignement Supérieur")
                .setFont(bold).setFontSize(7).setMarginBottom(0));
        leftCell.add(new Paragraph("et de la Recherche Scientifique")
                .setFont(bold).setFontSize(7).setMarginBottom(4));
        leftCell.add(new Paragraph("Université de Carthage")
                .setFont(bold).setFontSize(7.5f).setMarginBottom(4));
        leftCell.add(new Paragraph("Ecole Nationale d'Ingénieurs de Carthage")
                .setFont(bold).setFontSize(7));
        headerTable.addCell(leftCell);

        // Center – Logo text
        Cell centerCell = new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        centerCell.add(new Paragraph("ENICarthage")
                .setFont(bold).setFontSize(13)
                .setFontColor(new DeviceRgb(0, 80, 160)).setMarginBottom(2));
        centerCell.add(new Paragraph("Ecole Nationale d'Ingénieurs de Carthage")
                .setFont(italic).setFontSize(6)
                .setFontColor(new DeviceRgb(0, 80, 160)));
        headerTable.addCell(centerCell);

        // Right – Arabic
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        rightCell.add(new Paragraph("وزارة التعليم العالي و البحث العلمي")
                .setFont(bold).setFontSize(7).setMarginBottom(0));
        rightCell.add(new Paragraph("جامعة قرطاج")
                .setFont(bold).setFontSize(8).setMarginBottom(4));
        rightCell.add(new Paragraph("المدرسة الوطنية للمهندسين بقرطاج")
                .setFont(bold).setFontSize(7));
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(0.5f))
                .setMarginTop(4).setMarginBottom(8));

        // ── 2. TITRE ─────────────────────────────────────────────────────────
        document.add(new Paragraph(
                String.format("DEMANDE DE PRIX N° DP-%02d/%s", dpNumber, dpYear))
                .setFont(bold).setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(12));

        // ── 3. BLOC ADRESSE + CHAMPS FISCAUX ─────────────────────────────────
        Table addrTable = new Table(new float[]{3f, 2f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(0.5f))
                .setMarginBottom(10);

        Cell addrLeft = new Cell(4, 1)
                .setBorder(new SolidBorder(0.5f)).setPadding(8)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        addrLeft.add(new Paragraph("A l'attention de Monsieur le Gérant de la Société")
                .setFont(boldItalic).setFontSize(9.5f).setMarginBottom(8));
        addrLeft.add(new Paragraph("............................................................................")
                .setFont(regular).setFontSize(8));
        addrTable.addCell(addrLeft);

        for (String label : new String[]{"Matricule fiscale:", "N° Tél:", "N° Fax:", "E-Mail:"}) {
            Cell fc = new Cell().setBorder(new SolidBorder(0.5f))
                    .setPaddingLeft(6).setPaddingTop(3).setPaddingBottom(3);
            fc.add(new Paragraph(label + "  .........................")
                    .setFont(italic).setFontSize(8));
            addrTable.addCell(fc);
        }
        document.add(addrTable);

        // ── 4. TEXTE INTRO ───────────────────────────────────────────────────
        // Calcule la date limite la plus proche parmi les tickets
        String deadlineStr = computeDeadline(tickets);

        document.add(new Paragraph(
                "Nous vous prions de bien vouloir nous communiquer votre meilleure offre de prix " +
                        "relative à la prestation de services techniques (intervention / réparation) pour les " +
                        "équipements signalés en panne ci-dessous, conformément aux caractéristiques et urgences indiquées.")
                .setFont(italic).setFontSize(9).setMarginBottom(6));

        document.add(new Paragraph(
                "Votre offre de prix, sous plis fermé, devra parvenir à l'Ecole Nationale d'Ingénieurs " +
                        "De Carthage « ENICarthage » – 45 rue des entrepreneurs – Charguia2 - 2035 Tunis Carthage " +
                        "et devra comprendre la mention « Ne pas Ouvrir – DP N° " + dpNumber + "/" + dpYear +
                        " » au plus tard le " + deadlineStr)
                .setFont(italic).setFontSize(9).setMarginBottom(12));

        // ── 5. TABLEAU DES TICKETS / PANNES ──────────────────────────────────
        Table itemTable = new Table(new float[]{0.7f, 2.5f, 2f, 1.1f, 1.1f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(0.5f))
                .setMarginBottom(10);

        // En-tête
        for (String h : new String[]{"Réf.", "Désignation / Panne", "Description", "Priorité", "Date limite"}) {
            itemTable.addHeaderCell(new Cell()
                    .setBackgroundColor(TABLE_HEADER_BG)
                    .setBorder(new SolidBorder(0.5f))
                    .setPadding(5)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .add(new Paragraph(h).setFont(bold).setFontSize(8.5f)));
        }

        // Lignes
        if (tickets == null || tickets.isEmpty()) {
            // Ligne vide de démonstration
            itemTable.addCell(buildCell("—", regular, 8.5f));
            itemTable.addCell(buildCell("Aucun ticket", italic, 8.5f));
            itemTable.addCell(buildCell("—", regular, 8.5f));
            itemTable.addCell(buildCell("—", regular, 8.5f));
            itemTable.addCell(buildCell("—", regular, 8.5f));
        } else {
            int idx = 1;
            for (TicketResponseDTO t : tickets) {

                // Réf
                itemTable.addCell(buildCellCentered(
                        t.getIdTicket() != null ? t.getIdTicket() : String.valueOf(idx),
                        regular, 8));

                // Désignation = titre du ticket
                Cell titreCell = new Cell().setBorder(new SolidBorder(BORDER_WIDTH)).setPadding(5);
                titreCell.add(new Paragraph(t.getTitre() != null ? t.getTitre() : "N/A")
                        .setFont(bold).setFontSize(8.5f));
                itemTable.addCell(titreCell);

                // Description de la panne
                Cell descCell = new Cell().setBorder(new SolidBorder(BORDER_WIDTH)).setPadding(5);
                descCell.add(new Paragraph(t.getDescription() != null ? t.getDescription() : "—")
                        .setFont(regular).setFontSize(8f)
                        .setFontColor(ColorConstants.DARK_GRAY));
                itemTable.addCell(descCell);

                // Priorité avec couleur
                Cell prioCell = new Cell().setBorder(new SolidBorder(BORDER_WIDTH)).setPadding(5)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                String prioLabel = t.getPriorite() != null ? t.getPriorite().name() : "—";
                DeviceRgb prioColor = getPriorityColor(t.getPriorite());
                prioCell.add(new Paragraph(prioLabel).setFont(bold).setFontSize(8f)
                        .setFontColor(prioColor));
                itemTable.addCell(prioCell);

                // Date limite
                String dl = t.getDateLimite() != null ? SDF.format(t.getDateLimite()) : "—";
                itemTable.addCell(buildCellCentered(dl, regular, 8));

                idx++;
            }
        }

        document.add(itemTable);

        // ── 6. LIGNE TOTAL (prestation de service, pas de prix prédéfini) ────
        document.add(new Paragraph(
                "NB : Les prix doivent être indiqués en Dinars Tunisiens Hors Taxes (HT). " +
                        "La TVA applicable est de 19%. Le dépouillement se fait par article.")
                .setFont(italic).setFontSize(8).setMarginBottom(6));
        document.add(new Paragraph(
                "* Le soumissionnaire doit respecter les délais d'intervention demandés, " +
                        "le cas échéant son offre sera rejetée.")
                .setFont(regular).setFontSize(7.5f).setMarginBottom(1));
        document.add(new Paragraph("* Le dépouillement se fait par article.")
                .setFont(regular).setFontSize(7.5f).setMarginBottom(10));

        // ── 7. MONTANT EN LETTRES ─────────────────────────────────────────────
        document.add(new Paragraph(
                "Arrêté le présent devis à la somme de (en DT/TTC) : " +
                        ".....................................................................")
                .setFont(bold).setFontSize(9).setMarginBottom(4));
        document.add(new Paragraph(
                "............................................................................" +
                        "............................................................................")
                .setFont(regular).setFontSize(8).setMarginBottom(16));

        // ── 8. SIGNATURES ────────────────────────────────────────────────────
        Table sigTable = new Table(new float[]{1f, 1f})
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        Cell sigLeft = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        sigLeft.add(new Paragraph("Le Fournisseur").setFont(bold).setFontSize(10).setMarginBottom(2));
        sigLeft.add(new Paragraph("( Cachet & Signature )").setFont(italic).setFontSize(9));
        sigTable.addCell(sigLeft);

        Cell sigRight = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        sigRight.add(new Paragraph("La Directrice").setFont(bold).setFontSize(10).setMarginBottom(2));
        sigRight.add(new Paragraph("Houda BEN ATTIA SETHOM").setFont(bold).setFontSize(8.5f).setMarginBottom(2));
        sigRight.add(new Paragraph("Directrice").setFont(regular).setFontSize(8).setMarginBottom(2));
        sigRight.add(new Paragraph("de l'Ecole Nationale d'Ingénieurs de Carthage")
                .setFont(regular).setFontSize(7.5f));
        sigTable.addCell(sigRight);

        document.add(sigTable);

        // ── 9. FOOTER ────────────────────────────────────────────────────────
        document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(0.5f))
                .setMarginTop(10).setMarginBottom(4));
        document.add(new Paragraph(
                "45, rue des entrepreneurs Charguia 2, Tunis Carthage 2035. Tél/fax 71 941 579")
                .setFont(regular).setFontSize(7.5f).setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Retourne la date limite la plus proche parmi les tickets,
     * ou aujourd'hui + 2 jours si aucune date n'est définie.
     */
    private String computeDeadline(List<TicketResponseDTO> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return SDF.format(new Date(System.currentTimeMillis() + 2L * 86_400_000));
        }
        Date earliest = tickets.stream()
                .map(TicketResponseDTO::getDateLimite)
                .filter(d -> d != null)
                .min(Date::compareTo)
                .orElse(new Date(System.currentTimeMillis() + 2L * 86_400_000));
        return SDF.format(earliest);
    }

    private DeviceRgb getPriorityColor(Object priorite) {
        if (priorite == null) return (DeviceRgb) ColorConstants.BLACK;
        String name = priorite.toString().toUpperCase();
        if (name.contains("HIGH") || name.contains("HAUTE") || name.contains("URGENTE")) return PRIORITY_HIGH;
        if (name.contains("MEDIUM") || name.contains("MOYENNE"))                         return PRIORITY_MEDIUM;
        return PRIORITY_LOW;
    }

    private Cell buildCell(String text, PdfFont font, float fontSize) {
        return new Cell()
                .setBorder(new SolidBorder(BORDER_WIDTH))
                .setPadding(5)
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize));
    }

    private Cell buildCellCentered(String text, PdfFont font, float fontSize) {
        return new Cell()
                .setBorder(new SolidBorder(BORDER_WIDTH))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize));
    }
}