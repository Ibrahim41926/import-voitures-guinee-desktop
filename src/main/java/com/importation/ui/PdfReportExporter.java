package com.importation.ui;

import com.importation.models.Voiture;
import com.importation.models.dao.controllers.VoitureController;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfReportExporter {
    private static final float PAGE_MARGIN = 32f;
    private static final float HEADER_HEIGHT = 84f;
    private static final float SUMMARY_CARD_HEIGHT = 58f;
    private static final float SUMMARY_GAP = 10f;
    private static final float TABLE_HEADER_HEIGHT = 24f;
    private static final float TABLE_ROW_HEIGHT = 22f;
    private static final int FIRST_PAGE_ROWS = 12;
    private static final int OTHER_PAGE_ROWS = 18;

    private PdfReportExporter() {
    }

    public static void export(Path outputFile, List<Voiture> voitures) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PdfFonts fonts = chargerPolices(document);
            PDImageXObject logo = chargerLogo(document);
            ResumeExportPdf resume = calculerResumeExport(voitures);
            List<List<Voiture>> pages = decouperVoituresPourRapport(voitures);
            String dateExport = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String numeroRapport = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'RPT-'yyyyMMdd'-'HHmmss"));

            for (int index = 0; index < pages.size(); index++) {
                PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
                document.addPage(page);
                dessinerPage(
                    document,
                    page,
                    fonts,
                    logo,
                    pages.get(index),
                    resume,
                    dateExport,
                    numeroRapport,
                    index + 1,
                    pages.size(),
                    index == 0
                );
            }

            Path parent = outputFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            document.save(outputFile.toFile());
        }
    }

    private static void dessinerPage(
        PDDocument document,
        PDPage page,
        PdfFonts fonts,
        PDImageXObject logo,
        List<Voiture> voitures,
        ResumeExportPdf resume,
        String dateExport,
        String numeroRapport,
        int pageCourante,
        int totalPages,
        boolean premierePage
    ) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float contentWidth = pageWidth - (2 * PAGE_MARGIN);
        float y = pageHeight - PAGE_MARGIN;

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            y = dessinerEntete(content, fonts, logo, numeroRapport, dateExport, pageCourante, totalPages, contentWidth, y);

            if (premierePage) {
                y -= 14f;
                y = dessinerResumeStrategique(content, fonts, resume, contentWidth, y);
            }

            y -= 18f;
            y = dessinerSection(content, fonts, premierePage ? "Detail des vehicules" : "Suite du detail des vehicules",
                premierePage
                    ? "Vue de lecture rapide avec les colonnes les plus utiles au pilotage."
                    : "Continuation de l inventaire exporte.",
                y
            );

            y -= 10f;
            dessinerTableau(content, fonts, voitures, contentWidth, y);
            dessinerFooter(content, fonts, pageWidth, pageCourante, totalPages);
        }
    }

    private static float dessinerEntete(
        PDPageContentStream content,
        PdfFonts fonts,
        PDImageXObject logo,
        String numeroRapport,
        String dateExport,
        int pageCourante,
        int totalPages,
        float contentWidth,
        float yTop
    ) throws IOException {
        float x = PAGE_MARGIN;
        float y = yTop - HEADER_HEIGHT;

        fillRect(content, x, y, contentWidth, HEADER_HEIGHT, new Color(18, 50, 74));
        fillRect(content, x + (contentWidth * 0.66f), y, contentWidth * 0.34f, HEADER_HEIGHT, new Color(29, 94, 116));

        float textStartX = x + 18f;
        if (logo != null) {
            float logoHeight = 42f;
            float logoWidth = logo.getWidth() * (logoHeight / logo.getHeight());
            content.drawImage(logo, x + 18f, y + 21f, logoWidth, logoHeight);
            textStartX = x + 18f + logoWidth + 16f;
        }

        drawText(content, fonts.bold(), 10f, new Color(210, 228, 238), "PORTEFEUILLE IMPORTATION", textStartX, y + 62f);
        drawText(content, fonts.bold(), 24f, Color.WHITE, "Rapport vehicules", textStartX, y + 40f);
        drawText(content, fonts.regular(), 11f, new Color(230, 240, 246), "Synthese d inventaire, couts et ventes", textStartX, y + 24f);

        drawRightText(content, fonts.bold(), 11f, Color.WHITE, "Rapport " + numeroRapport, x + contentWidth - 18f, y + 58f);
        drawRightText(content, fonts.regular(), 10f, new Color(230, 240, 246), "Genere le " + dateExport, x + contentWidth - 18f, y + 43f);
        drawRightText(content, fonts.regular(), 10f, new Color(230, 240, 246),
            "Page " + pageCourante + " / " + totalPages, x + contentWidth - 18f, y + 28f);

        return y;
    }

    private static float dessinerResumeStrategique(
        PDPageContentStream content,
        PdfFonts fonts,
        ResumeExportPdf resume,
        float contentWidth,
        float yTop
    ) throws IOException {
        List<MetricCard> cartes = List.of(
            new MetricCard("Vehicules exportes", String.valueOf(resume.totalVehicules()), "Lignes incluses dans le rapport", new Color(237, 243, 248), new Color(209, 220, 232)),
            new MetricCard("Taux de vente", formatPercent(resume.tauxVentePct()), resume.totalVendues() + " vendues sur " + resume.totalVehicules(), new Color(231, 245, 244), new Color(195, 225, 221)),
            new MetricCard("Stock actif", String.valueOf(resume.totalActives()), "Vehicules encore en portefeuille", new Color(251, 244, 223), new Color(235, 221, 176)),
            new MetricCard("Total ventes", formatGnfPdf(resume.totalVenteGnf()), "Revenus cumules sur les vehicules vendues", new Color(230, 241, 248), new Color(197, 216, 231)),
            new MetricCard("Total achat", formatCadPdf(resume.totalAchatCad()), "Valeur d acquisition cumulee", new Color(251, 237, 230), new Color(235, 199, 181)),
            new MetricCard("Cout total", formatGnfPdf(resume.totalCoutGnf()), "Cout complet converti en GNF", new Color(251, 244, 223), new Color(235, 221, 176)),
            new MetricCard("Valeur stock", formatGnfPdf(resume.valeurStockActifGnf()), "Cout total des vehicules non vendues", new Color(239, 243, 248), new Color(209, 220, 232)),
            new MetricCard("Marge realisee", formatGnfPdf(resume.margeVenduesGnf()), "Ventes vendues moins couts complets", new Color(231, 245, 244), new Color(195, 225, 221))
        );

        float cardWidth = (contentWidth - (SUMMARY_GAP * 3f)) / 4f;
        float x = PAGE_MARGIN;
        float y = yTop - SUMMARY_CARD_HEIGHT;
        for (int index = 0; index < cartes.size(); index++) {
            if (index > 0 && index % 4 == 0) {
                x = PAGE_MARGIN;
                y -= SUMMARY_CARD_HEIGHT + SUMMARY_GAP;
            }
            dessinerCarteResume(content, fonts, cartes.get(index), x, y, cardWidth);
            x += cardWidth + SUMMARY_GAP;
        }

        return y;
    }

    private static void dessinerCarteResume(
        PDPageContentStream content,
        PdfFonts fonts,
        MetricCard carte,
        float x,
        float y,
        float width
    ) throws IOException {
        fillRect(content, x, y, width, SUMMARY_CARD_HEIGHT, carte.background());
        strokeRect(content, x, y, width, SUMMARY_CARD_HEIGHT, carte.border());
        drawText(content, fonts.bold(), 9f, new Color(73, 97, 117), carte.label(), x + 10f, y + 44f);
        drawText(content, fonts.bold(), 15f, new Color(16, 38, 61), carte.value(), x + 10f, y + 25f);
        drawWrappedText(content, fonts.regular(), 8.5f, new Color(92, 115, 135), carte.detail(), x + 10f, y + 8f, width - 20f, 10f, 2);
    }

    private static float dessinerSection(
        PDPageContentStream content,
        PdfFonts fonts,
        String titre,
        String note,
        float yTop
    ) throws IOException {
        drawText(content, fonts.bold(), 14f, new Color(18, 40, 63), titre, PAGE_MARGIN, yTop);
        drawText(content, fonts.regular(), 10f, new Color(100, 116, 139), note, PAGE_MARGIN, yTop - 14f);
        return yTop - 18f;
    }

    private static void dessinerTableau(
        PDPageContentStream content,
        PdfFonts fonts,
        List<Voiture> voitures,
        float contentWidth,
        float yTop
    ) throws IOException {
        float[] largeurs = {72f, 174f, 72f, 78f, 94f, 98f, 98f, 98f};
        String[] entetes = {"Ref", "Vehicule", "Import", "Statut", "Achat CAD", "Cout GNF", "Vente GNF", "Marge GNF"};
        boolean[] alignRight = {false, false, false, false, true, true, true, true};

        float x = PAGE_MARGIN;
        float y = yTop - TABLE_HEADER_HEIGHT;

        fillRect(content, x, y, contentWidth, TABLE_HEADER_HEIGHT, new Color(18, 50, 74));
        float cursorX = x;
        for (int i = 0; i < entetes.length; i++) {
            String header = entetes[i];
            if (alignRight[i]) {
                drawRightText(content, fonts.bold(), 9.5f, Color.WHITE, header, cursorX + largeurs[i] - 8f, y + 8f);
            } else {
                drawText(content, fonts.bold(), 9.5f, Color.WHITE, header, cursorX + 8f, y + 8f);
            }
            cursorX += largeurs[i];
        }

        y -= TABLE_ROW_HEIGHT;
        for (int row = 0; row < voitures.size(); row++) {
            Voiture voiture = voitures.get(row);
            Color background = row % 2 == 0 ? Color.WHITE : new Color(245, 249, 252);
            fillRect(content, x, y, contentWidth, TABLE_ROW_HEIGHT, background);
            strokeHorizontalLine(content, new Color(232, 238, 243), x, x + contentWidth, y);

            String[] valeurs = {
                formaterReferencePdf(voiture),
                formaterVehiculePdf(voiture),
                formaterDatePdf(voiture),
                formaterStatutPdf(voiture),
                formatCadPdf(voiture.getPrixAchatCAD()),
                formatGnfPdf(VoitureController.calculerCoutTotal(voiture)),
                formaterVentePdf(voiture),
                formaterMargePdf(voiture)
            };

            cursorX = x;
            for (int i = 0; i < valeurs.length; i++) {
                Color textColor = new Color(18, 40, 63);
                if (i == 3) {
                    textColor = estStatutVendu(voiture.getStatut()) ? new Color(22, 101, 52) : new Color(154, 103, 0);
                }
                if (i == 7 && estStatutVendu(voiture.getStatut())) {
                    double marge = voiture.getPrixReventeGNF() - VoitureController.calculerCoutTotal(voiture);
                    textColor = marge >= 0 ? new Color(22, 101, 52) : new Color(180, 35, 24);
                }
                if ((i == 6 || i == 7) && !estStatutVendu(voiture.getStatut())) {
                    textColor = new Color(100, 116, 139);
                }

                String texte = tronquerTexte(fonts.regular(), 9f, valeurs[i], largeurs[i] - 12f);
                if (alignRight[i]) {
                    drawRightText(content, fonts.regular(), 9f, textColor, texte, cursorX + largeurs[i] - 6f, y + 7f);
                } else {
                    drawText(content, fonts.regular(), 9f, textColor, texte, cursorX + 6f, y + 7f);
                }
                cursorX += largeurs[i];
            }

            y -= TABLE_ROW_HEIGHT;
        }

        strokeRect(content, x, y + TABLE_ROW_HEIGHT, contentWidth, TABLE_HEADER_HEIGHT + (voitures.size() * TABLE_ROW_HEIGHT), new Color(219, 228, 236));
    }

    private static void dessinerFooter(
        PDPageContentStream content,
        PdfFonts fonts,
        float pageWidth,
        int pageCourante,
        int totalPages
    ) throws IOException {
        float y = 18f;
        strokeHorizontalLine(content, new Color(219, 228, 236), PAGE_MARGIN, pageWidth - PAGE_MARGIN, y + 10f);
        drawText(content, fonts.regular(), 9f, new Color(100, 116, 139), "Import Voitures Guinee", PAGE_MARGIN, y);
        drawText(content, fonts.regular(), 9f, new Color(100, 116, 139), "Document interne", (pageWidth / 2f) - 36f, y);
        drawRightText(content, fonts.regular(), 9f, new Color(100, 116, 139), "Page " + pageCourante + " / " + totalPages, pageWidth - PAGE_MARGIN, y);
    }

    private static ResumeExportPdf calculerResumeExport(List<Voiture> voitures) {
        long totalVendues = 0;
        long totalActives = 0;
        double totalAchatCad = 0.0;
        double totalTransportCad = 0.0;
        double totalAssuranceCad = 0.0;
        double totalFraisDiversCad = 0.0;
        double totalDedouanementGnf = 0.0;
        double totalFraisDiversGnf = 0.0;
        double totalCoutGnf = 0.0;
        double totalVenteGnf = 0.0;
        double margeVenduesGnf = 0.0;
        double valeurStockActifGnf = 0.0;

        for (Voiture voiture : voitures) {
            totalAchatCad += voiture.getPrixAchatCAD();
            totalTransportCad += voiture.getTransportCAD();
            totalAssuranceCad += voiture.getAssuranceCAD();
            totalFraisDiversCad += voiture.getFraisDiversCAD();
            totalDedouanementGnf += voiture.getDedouanementGNF();
            totalFraisDiversGnf += voiture.getFraisDiversGNF();
            double coutTotal = VoitureController.calculerCoutTotal(voiture);
            totalCoutGnf += coutTotal;
            if (estStatutVendu(voiture.getStatut())) {
                totalVendues++;
                totalVenteGnf += voiture.getPrixReventeGNF();
                margeVenduesGnf += voiture.getPrixReventeGNF() - coutTotal;
            } else {
                totalActives++;
                valeurStockActifGnf += coutTotal;
            }
        }

        double tauxVentePct = voitures.isEmpty() ? 0.0 : (totalVendues * 100.0) / voitures.size();
        double prixVenteMoyenGnf = totalVendues == 0 ? 0.0 : totalVenteGnf / totalVendues;
        double margeMoyenneGnf = totalVendues == 0 ? 0.0 : margeVenduesGnf / totalVendues;
        double coutMoyenGnf = voitures.isEmpty() ? 0.0 : totalCoutGnf / voitures.size();
        double coutMoyenStockActifGnf = totalActives == 0 ? 0.0 : valeurStockActifGnf / totalActives;
        double roiRealisePct = totalVendues == 0 ? 0.0 : (margeVenduesGnf * 100.0) / (totalVenteGnf - margeVenduesGnf);

        return new ResumeExportPdf(
            voitures.size(),
            totalVendues,
            totalActives,
            totalAchatCad,
            totalTransportCad,
            totalAssuranceCad,
            totalFraisDiversCad,
            totalDedouanementGnf,
            totalFraisDiversGnf,
            totalCoutGnf,
            totalVenteGnf,
            margeVenduesGnf,
            valeurStockActifGnf,
            tauxVentePct,
            prixVenteMoyenGnf,
            margeMoyenneGnf,
            coutMoyenGnf,
            coutMoyenStockActifGnf,
            roiRealisePct
        );
    }

    private static List<List<Voiture>> decouperVoituresPourRapport(List<Voiture> voitures) {
        List<List<Voiture>> pages = new ArrayList<>();
        int index = 0;
        boolean premierePage = true;

        while (index < voitures.size()) {
            int tailleBloc = premierePage ? FIRST_PAGE_ROWS : OTHER_PAGE_ROWS;
            int fin = Math.min(index + tailleBloc, voitures.size());
            pages.add(new ArrayList<>(voitures.subList(index, fin)));
            index = fin;
            premierePage = false;
        }

        return pages;
    }

    private static PdfFonts chargerPolices(PDDocument document) throws IOException {
        PDFont regular = chargerPolice(document,
            List.of(
                Path.of("C:\\Windows\\Fonts\\arial.ttf"),
                Path.of("C:\\Windows\\Fonts\\calibri.ttf"),
                Path.of("C:\\Windows\\Fonts\\segoeui.ttf")
            ),
            PDType1Font.HELVETICA
        );

        PDFont bold = chargerPolice(document,
            List.of(
                Path.of("C:\\Windows\\Fonts\\arialbd.ttf"),
                Path.of("C:\\Windows\\Fonts\\calibrib.ttf"),
                Path.of("C:\\Windows\\Fonts\\segoeuib.ttf")
            ),
            PDType1Font.HELVETICA_BOLD
        );

        return new PdfFonts(regular, bold);
    }

    private static PDFont chargerPolice(PDDocument document, List<Path> candidates, PDFont fallback) throws IOException {
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                try (InputStream inputStream = Files.newInputStream(candidate)) {
                    return PDType0Font.load(document, inputStream);
                } catch (IOException ignored) {
                    // Passe au candidat suivant.
                }
            }
        }
        return fallback;
    }

    private static PDImageXObject chargerLogo(PDDocument document) {
        List<Path> candidats = List.of(
            Path.of("src", "main", "images", "LOGO.png"),
            Path.of("src", "main", "images", "logo.png"),
            Path.of("images", "LOGO.png"),
            Path.of("images", "logo.png")
        );

        for (Path candidat : candidats) {
            if (Files.exists(candidat)) {
                try {
                    return PDImageXObject.createFromFileByContent(candidat.toFile(), document);
                } catch (IOException ignored) {
                    // Continue.
                }
            }
        }
        return null;
    }

    private static void fillRect(PDPageContentStream content, float x, float y, float width, float height, Color color) throws IOException {
        content.setNonStrokingColor(color);
        content.addRect(x, y, width, height);
        content.fill();
    }

    private static void strokeRect(PDPageContentStream content, float x, float y, float width, float height, Color color) throws IOException {
        content.setStrokingColor(color);
        content.addRect(x, y, width, height);
        content.stroke();
    }

    private static void strokeHorizontalLine(PDPageContentStream content, Color color, float x1, float x2, float y) throws IOException {
        content.setStrokingColor(color);
        content.moveTo(x1, y);
        content.lineTo(x2, y);
        content.stroke();
    }

    private static void drawText(PDPageContentStream content, PDFont font, float size, Color color, String text, float x, float y) throws IOException {
        String valeur = preparerTexte(font, text);
        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(valeur);
        content.endText();
    }

    private static void drawRightText(PDPageContentStream content, PDFont font, float size, Color color, String text, float rightX, float y) throws IOException {
        String valeur = preparerTexte(font, text);
        float width = font.getStringWidth(valeur) / 1000f * size;
        drawText(content, font, size, color, valeur, rightX - width, y);
    }

    private static void drawWrappedText(
        PDPageContentStream content,
        PDFont font,
        float size,
        Color color,
        String text,
        float x,
        float y,
        float maxWidth,
        float lineHeight,
        int maxLines
    ) throws IOException {
        List<String> lines = decouperTexte(font, size, text, maxWidth, maxLines);
        float currentY = y;
        for (String line : lines) {
            drawText(content, font, size, color, line, x, currentY);
            currentY -= lineHeight;
        }
    }

    private static List<String> decouperTexte(PDFont font, float size, String text, float maxWidth, int maxLines) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] mots = preparerTexte(font, text).split("\\s+");
        StringBuilder courant = new StringBuilder();

        for (String mot : mots) {
            String test = courant.length() == 0 ? mot : courant + " " + mot;
            float width = font.getStringWidth(test) / 1000f * size;
            if (width <= maxWidth) {
                courant.setLength(0);
                courant.append(test);
            } else {
                if (courant.length() > 0) {
                    lines.add(courant.toString());
                    courant.setLength(0);
                    courant.append(mot);
                } else {
                    lines.add(tronquerTexte(font, size, mot, maxWidth));
                }
                if (lines.size() == maxLines) {
                    return lines;
                }
            }
        }

        if (courant.length() > 0 && lines.size() < maxLines) {
            lines.add(courant.toString());
        }

        return lines;
    }

    private static String tronquerTexte(PDFont font, float size, String text, float maxWidth) throws IOException {
        String valeur = preparerTexte(font, text);
        if ((font.getStringWidth(valeur) / 1000f * size) <= maxWidth) {
            return valeur;
        }

        String ellipsis = "...";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < valeur.length(); i++) {
            String test = builder + String.valueOf(valeur.charAt(i)) + ellipsis;
            if ((font.getStringWidth(test) / 1000f * size) > maxWidth) {
                break;
            }
            builder.append(valeur.charAt(i));
        }
        return builder + ellipsis;
    }

    private static String preparerTexte(PDFont font, String text) {
        String valeur = text == null ? "" : text;
        if (font instanceof PDType1Font) {
            String ascii = Normalizer.normalize(valeur, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            ascii = ascii.replace('’', '\'').replace('“', '"').replace('”', '"');
            return ascii;
        }
        return valeur;
    }

    private static boolean estStatutVendu(String statut) {
        if (statut == null) {
            return false;
        }
        String normalise = statut.trim().toUpperCase(Locale.ROOT);
        return "VENDUE".equals(normalise) || "VENDU".equals(normalise);
    }

    private static String formaterReferencePdf(Voiture voiture) {
        String immatriculation = voiture.getImmatriculation();
        if (immatriculation == null || immatriculation.isBlank()) {
            return "ID-" + voiture.getId();
        }
        return immatriculation;
    }

    private static String formaterVehiculePdf(Voiture voiture) {
        return voiture.getMarque() + " " + voiture.getModele() + " (" + voiture.getAnnee() + ")";
    }

    private static String formaterDatePdf(Voiture voiture) {
        if (voiture.getDateImportation() == null) {
            return "-";
        }
        return voiture.getDateImportation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static String formaterStatutPdf(Voiture voiture) {
        if (voiture.getStatut() == null || voiture.getStatut().isBlank()) {
            return "-";
        }
        return voiture.getStatut().replace('_', ' ');
    }

    private static String formaterVentePdf(Voiture voiture) {
        if (!estStatutVendu(voiture.getStatut())) {
            return "-";
        }
        return formatGnfPdf(voiture.getPrixReventeGNF());
    }

    private static String formaterMargePdf(Voiture voiture) {
        if (!estStatutVendu(voiture.getStatut())) {
            return "-";
        }
        return formatGnfPdf(voiture.getPrixReventeGNF() - VoitureController.calculerCoutTotal(voiture));
    }

    private static String formatCadPdf(double montant) {
        return "CAD " + String.format(Locale.US, "%,.2f", montant);
    }

    private static String formatGnfPdf(double montant) {
        return "GNF " + String.format(Locale.US, "%,.0f", montant);
    }

    private static String formatPercent(double value) {
        return String.format(Locale.US, "%,.1f%%", value);
    }

    private record ResumeExportPdf(
        int totalVehicules,
        long totalVendues,
        long totalActives,
        double totalAchatCad,
        double totalTransportCad,
        double totalAssuranceCad,
        double totalFraisDiversCad,
        double totalDedouanementGnf,
        double totalFraisDiversGnf,
        double totalCoutGnf,
        double totalVenteGnf,
        double margeVenduesGnf,
        double valeurStockActifGnf,
        double tauxVentePct,
        double prixVenteMoyenGnf,
        double margeMoyenneGnf,
        double coutMoyenGnf,
        double coutMoyenStockActifGnf,
        double roiRealisePct
    ) {}

    private record PdfFonts(PDFont regular, PDFont bold) {}

    private record MetricCard(String label, String value, String detail, Color background, Color border) {}
}
