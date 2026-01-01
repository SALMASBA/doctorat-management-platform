package ma.enset.documentservice.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ma.enset.documentservice.dto.DocumentRequestDTO;
import ma.enset.documentservice.dto.ProcesVerbalDTO;
import ma.enset.documentservice.dto.SoutenanceAuthorizationDTO;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    // Couleurs officielles ENSET Mohammedia / Université Hassan II
    public static final Color PRIMARY_COLOR = new Color(0, 51, 102); // Bleu foncé
    public static final Color SECONDARY_COLOR = new Color(139, 0, 0); // Rouge bordeaux
    public static final Color HEADER_BG = new Color(240, 240, 240);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    // ==================== ATTESTATION D'INSCRIPTION ====================
    public byte[] generateEnrollmentCertificate(DocumentRequestDTO request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addOfficialHeader(document);
            addDocumentTitle(document, "ATTESTATION D'INSCRIPTION");
            addEnrollmentContent(document, request);
            addSignatureBlock(document, "Le Directeur de l'ECOLE MAROCAINE DES SCIENCES DE LINGENIEUR DE CASABLANCA");
            addOfficialFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // ==================== AUTORISATION DE SOUTENANCE ====================
    public byte[] generateSoutenanceAuthorization(SoutenanceAuthorizationDTO request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addOfficialHeader(document);
            addDocumentTitle(document, "AUTORISATION DE SOUTENANCE DE THÈSE");
            addSoutenanceAuthorizationContent(document, request);
            addJuryTable(document, request);
            addSignatureBlock(document, "Le Directeur de l'ECOLE MAROCAINE DES SCIENCES DE LINGENIEUR DE CASABLANCA");
            addOfficialFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // ==================== PROCÈS-VERBAL DE SOUTENANCE ====================
    public byte[] generateProcesVerbal(ProcesVerbalDTO request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addOfficialHeader(document);
            addDocumentTitle(document, "PROCÈS-VERBAL DE SOUTENANCE DE THÈSE DE DOCTORAT");
            addPVContent(document, request);
            addPVJuryTable(document, request);
            addDecisionBlock(document, request);
            addOfficialFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // ==================== DEMANDE MANUSCRITE ====================
    public byte[] generateHandwrittenRequest(DocumentRequestDTO request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            Paragraph locationDate = new Paragraph("Casablanca, le " + LocalDate.now().format(DATE_FORMATTER),
                    contentFont);
            locationDate.setAlignment(Element.ALIGN_RIGHT);
            document.add(locationDate);

            Paragraph sender = new Paragraph();
            sender.add(new Chunk("De : " + request.getFirstName() + " " + request.getLastName() + "\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            sender.add(new Chunk("Doctorant(e) en " + request.getFormation() + "\n", contentFont));
            sender.add(new Chunk("CIN : " + request.getCin() + " | CNE : " + request.getCne() + "\n", contentFont));
            sender.setSpacingBefore(20);
            document.add(sender);

            Paragraph recipient = new Paragraph(
                    "A Monsieur le Directeur de l'ECOLE MAROCAINE DES SCIENCES DE LINGENIEUR DE CASABLANCA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            recipient.setAlignment(Element.ALIGN_RIGHT); // Or standard letter format
            recipient.setSpacingBefore(20);
            document.add(recipient);

            Paragraph subject = new Paragraph("Objet : Demande de soutenance de thèse",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            subject.setSpacingBefore(30);
            subject.setSpacingAfter(20);
            document.add(subject);

            Paragraph body = new Paragraph("Monsieur le Directeur,\n\n" +
                    "J'ai l'honneur de solliciter votre bienveillance pour m'autoriser à soutenir ma thèse de doctorat intitulée :\n"
                    +
                    "\"" + request.getThesisTitle() + "\"\n\n" +
                    "Préparée sous la direction de "
                    + (request.getDirectorName() != null ? request.getDirectorName() : "mon directeur de thèse")
                    + ".\n\n" +
                    "Je joins à cette demande tous les documents requis pour la constitution du dossier de soutenance tel que stipulé par le règlement doctoral.\n\n"
                    +
                    "Dans l'attente d'une suite favorable, veuillez agréer, Monsieur le Directeur, l'expression de mes salutations respectueuses.",
                    contentFont);
            body.setLeading(18);
            document.add(body);

            Paragraph signature = new Paragraph("Signature de l'intéressé(e)",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11));
            signature.setAlignment(Element.ALIGN_RIGHT);
            signature.setSpacingBefore(50);
            signature.setIndentationRight(50);
            document.add(signature);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    // ==================== COPIES ATTESTATIONS FORMATIONS (Listing)
    // ====================
    public byte[] generateTrainingCertificateCopies(DocumentRequestDTO request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addOfficialHeader(document);
            addDocumentTitle(document, "RELEVÉ DES ATTESTATIONS DE FORMATION");

            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph p = new Paragraph("Le Directeur de l'établissement atteste que le doctorant :\n\n", contentFont);
            p.add(new Chunk(request.getFirstName() + " " + request.getLastName() + "\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            p.add(new Chunk("CIN : " + request.getCin() + "\n\n", contentFont));
            p.add(new Chunk("A validé les formations doctorales suivantes requises pour la soutenance :\n\n",
                    contentFont));
            document.add(p);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4, 1 });

            PdfPCell c1 = new PdfPCell(new Phrase("Intitulé de la Formation",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
            c1.setBackgroundColor(PRIMARY_COLOR);
            c1.setPadding(5);
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(
                    new Phrase("Validation", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
            c2.setBackgroundColor(PRIMARY_COLOR);
            c2.setPadding(5);
            table.addCell(c2);

            // Mock Data or from Request if available (Assuming generic list for now as per
            // requirement)
            String[] commonModules = { "Méthodologie de Recherche Comp. et Avancée",
                    "Anglais Scientifique et Technique", "Entrepreneuriat et Innovation", "Pédagogie Universitaire" };
            for (String mod : commonModules) {
                table.addCell(createCell(mod, contentFont));
                table.addCell(createCell("VALIDÉ", contentFont));
            }

            document.add(table);

            Paragraph footer = new Paragraph(
                    "\nCe document est délivré pour servir de pièce justificative dans le dossier de soutenance.\n\n",
                    contentFont);
            document.add(footer);

            addSignatureBlock(document, "Le Directeur de l'ECOLE MAROCAINE DES SCIENCES DE LINGENIEUR DE CASABLANCA");
            addOfficialFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    // ==================== MÉTHODES COMMUNES ====================

    private void addOfficialHeader(Document document) throws DocumentException {
        // En-tête officiel
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY_COLOR);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

        Paragraph header = new Paragraph();
        header.add(new Chunk("ECOLE MAROCAINE DES SCIENCES DE L'INGENIEUR\n", headerFont));
        header.add(new Chunk("CASABLANCA\n\n", subHeaderFont));
        header.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell(header);
        cell.setBorder(com.lowagie.text.Rectangle.BOTTOM);
        cell.setBorderColor(PRIMARY_COLOR);
        cell.setBorderWidth(2);
        cell.setPaddingBottom(15);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(cell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addDocumentTitle(Document document, String titleText) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, SECONDARY_COLOR);

        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(30);

        // Ajouter une ligne décorative sous le titre
        document.add(title);
    }

    private void addEnrollmentContent(Document document, DocumentRequestDTO request) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        Paragraph content = new Paragraph();
        content.setLeading(20);

        content.add(new Chunk("Le Directeur de l'ECOLE MAROCAINE DES SCIENCES DE L'INGENIEUR de Casablanca, " +
                "soussigné, atteste que :\n\n", normalFont));

        // Informations du doctorant
        content.add(new Chunk("L'étudiant(e) : ", normalFont));
        content.add(new Chunk(request.getFirstName() + " " + request.getLastName() + "\n", boldFont));

        content.add(new Chunk("CIN : ", normalFont));
        content.add(new Chunk(request.getCin() + "\n", boldFont));

        content.add(new Chunk("CNE : ", normalFont));
        content.add(new Chunk(request.getCne() + "\n\n", boldFont));

        content.add(new Chunk("Est régulièrement inscrit(e) au titre de l'année universitaire ", normalFont));
        content.add(new Chunk("2024/2025 ", boldFont));
        content.add(new Chunk("en cycle de formation doctorale :\n\n", normalFont));

        content.add(new Chunk("Formation : ", normalFont));
        content.add(new Chunk(request.getFormation() + "\n", boldFont));

        content.add(new Chunk("Laboratoire : ", normalFont));
        content.add(new Chunk(request.getLaboratory() + "\n", boldFont));

        content.add(new Chunk("Titre de la thèse : ", normalFont));
        content.add(new Chunk("\"" + request.getThesisTitle() + "\"\n\n", boldFont));

        content.add(
                new Chunk("Cette attestation est délivrée à l'intéressé(e) pour servir et valoir ce que de droit.\n\n",
                        normalFont));

        content.add(new Chunk("Fait à Casablanca, le " + LocalDate.now().format(DATE_FORMATTER), normalFont));

        document.add(content);
    }

    private void addSoutenanceAuthorizationContent(Document document, SoutenanceAuthorizationDTO request)
            throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        Paragraph content = new Paragraph();
        content.setLeading(18);

        content.add(new Chunk("Le Directeur de l'Établissement autorise :\n\n", normalFont));

        content.add(new Chunk("M./Mme : ", normalFont));
        content.add(new Chunk(request.getFirstName() + " " + request.getLastName() + "\n", boldFont));

        content.add(new Chunk("CIN : ", normalFont));
        content.add(new Chunk(request.getCin() + "    CNE : ", boldFont));
        content.add(new Chunk(request.getCne() + "\n\n", boldFont));

        content.add(new Chunk("À soutenir sa thèse de doctorat intitulée :\n", normalFont));
        content.add(new Chunk("\"" + request.getThesisTitle() + "\"\n\n", boldFont));

        content.add(new Chunk("Préparée au sein du laboratoire : ", normalFont));
        content.add(new Chunk(request.getLaboratory() + "\n", boldFont));

        content.add(new Chunk("Sous la direction de : ", normalFont));
        content.add(new Chunk(request.getDirectorName() + "\n\n", boldFont));

        if (request.getSoutenanceDate() != null) {
            content.add(new Chunk("Date de soutenance : ", normalFont));
            content.add(new Chunk(request.getSoutenanceDate().format(DATE_FORMATTER) + "\n", boldFont));
        }

        if (request.getSoutenanceTime() != null) {
            content.add(new Chunk("Heure : ", normalFont));
            content.add(new Chunk(request.getSoutenanceTime() + "\n", boldFont));
        }

        if (request.getSoutenanceLocation() != null) {
            content.add(new Chunk("Lieu : ", normalFont));
            content.add(new Chunk(request.getSoutenanceLocation() + "\n\n", boldFont));
        }

        document.add(content);
    }

    private void addJuryTable(Document document, SoutenanceAuthorizationDTO request) throws DocumentException {
        if (request.getJuryMembers() == null || request.getJuryMembers().isEmpty()) {
            return;
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph juryTitle = new Paragraph("Composition du Jury :",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR));
        juryTitle.setSpacingBefore(20);
        juryTitle.setSpacingAfter(10);
        document.add(juryTitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 2, 2, 3 });

        // En-têtes
        String[] headers = { "Nom et Prénom", "Grade", "Qualité", "Établissement" };
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(PRIMARY_COLOR);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Données du jury
        for (SoutenanceAuthorizationDTO.JuryMemberDTO member : request.getJuryMembers()) {
            table.addCell(createCell(member.getName(), cellFont));
            table.addCell(createCell(member.getGrade(), cellFont));
            table.addCell(createCell(member.getRole(), cellFont));
            table.addCell(createCell(member.getInstitution(), cellFont));
        }

        document.add(table);
    }

    private void addPVContent(Document document, ProcesVerbalDTO request) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        Paragraph content = new Paragraph();
        content.setLeading(18);

        content.add(new Chunk("L'an ", normalFont));
        content.add(new Chunk(String.valueOf(LocalDate.now().getYear()), boldFont));
        content.add(new Chunk(", le ", normalFont));
        if (request.getSoutenanceDate() != null) {
            content.add(new Chunk(request.getSoutenanceDate().format(DATE_FORMATTER), boldFont));
        }
        content.add(new Chunk(", à ", normalFont));
        content.add(new Chunk(request.getSoutenanceTime() != null ? request.getSoutenanceTime() : "...", boldFont));
        content.add(new Chunk(", dans la salle ", normalFont));
        content.add(
                new Chunk(request.getSoutenanceLocation() != null ? request.getSoutenanceLocation() : "...", boldFont));
        content.add(new Chunk(" de l'EMSI Casablanca,\n\n", normalFont));

        content.add(
                new Chunk("Le jury de soutenance de thèse de doctorat s'est réuni pour examiner les travaux de :\n\n",
                        normalFont));

        content.add(new Chunk("M./Mme : ", normalFont));
        content.add(new Chunk(request.getFirstName() + " " + request.getLastName() + "\n", boldFont));

        content.add(new Chunk("CIN : ", normalFont));
        content.add(new Chunk(request.getCin() + "    CNE : ", boldFont));
        content.add(new Chunk(request.getCne() + "\n\n", boldFont));

        content.add(new Chunk("Thèse intitulée :\n", normalFont));
        content.add(new Chunk("\"" + request.getThesisTitle() + "\"\n\n", boldFont));

        content.add(new Chunk("Préparée sous la direction de : ", normalFont));
        content.add(new Chunk(request.getDirectorName() + "\n", boldFont));

        document.add(content);
    }

    private void addPVJuryTable(Document document, ProcesVerbalDTO request) throws DocumentException {
        if (request.getJuryMembers() == null || request.getJuryMembers().isEmpty()) {
            return;
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        Paragraph juryTitle = new Paragraph("Membres du Jury :",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY_COLOR));
        juryTitle.setSpacingBefore(15);
        juryTitle.setSpacingAfter(10);
        document.add(juryTitle);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1.5f, 2, 2.5f, 2 });

        String[] headers = { "Nom et Prénom", "Grade", "Qualité", "Établissement", "Signature" };
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(PRIMARY_COLOR);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (ProcesVerbalDTO.JuryMemberPVDTO member : request.getJuryMembers()) {
            table.addCell(createCell(member.getName(), cellFont));
            table.addCell(createCell(member.getGrade(), cellFont));
            table.addCell(createCell(member.getRole(), cellFont));
            table.addCell(createCell(member.getInstitution(), cellFont));
            table.addCell(createCell("", cellFont)); // Espace pour signature
        }

        document.add(table);
    }

    private void addDecisionBlock(Document document, ProcesVerbalDTO request) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font mentionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SECONDARY_COLOR);

        Paragraph decision = new Paragraph();
        decision.setSpacingBefore(25);
        decision.setLeading(20);

        decision.add(new Chunk("Après délibération, le jury déclare le(la) candidat(e) :\n\n", normalFont));

        decision.add(new Chunk("Décision : ", boldFont));
        decision.add(new Chunk(request.getDecision() != null ? request.getDecision() : "ADMIS(E)", mentionFont));
        decision.add(new Chunk("\n\n", normalFont));

        decision.add(new Chunk("Mention obtenue : ", boldFont));
        decision.add(new Chunk(request.getMention() != null ? request.getMention() : "...", mentionFont));
        decision.add(new Chunk("\n\n", normalFont));

        decision.add(
                new Chunk("Le(La) candidat(e) est autorisé(e) à faire usage du titre de DOCTEUR.\n\n", normalFont));

        decision.add(new Chunk("Fait à Casablanca, le " + LocalDate.now().format(DATE_FORMATTER), normalFont));

        document.add(decision);
    }

    private void addSignatureBlock(Document document, String signatoryTitle) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        Paragraph signature = new Paragraph();
        signature.setSpacingBefore(50);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setIndentationRight(30);

        signature.add(new Chunk(signatoryTitle + "\n\n\n\n", boldFont));
        signature.add(new Chunk("Signature et cachet", font));

        document.add(signature);
    }

    private void addOfficialFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

        Paragraph footer = new Paragraph();
        footer.setSpacingBefore(30);
        footer.setAlignment(Element.ALIGN_CENTER);

        footer.add(new Chunk("_______________________________________________________________________________\n",
                footerFont));
        footer.add(new Chunk("EMSI Casablanca - Ecole Marocaine des Sciences de l'Ingénieur\n", footerFont));
        footer.add(new Chunk("Document généré automatiquement par le Portail Doctoral EMSI - " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), footerFont));

        document.add(footer);
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
