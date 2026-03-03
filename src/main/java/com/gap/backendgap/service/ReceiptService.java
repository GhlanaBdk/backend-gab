package com.gap.backendgap.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.gap.backendgap.entity.Account;
import com.gap.backendgap.entity.Transaction;
import com.gap.backendgap.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    private final TransactionRepository transactionRepository;

    public ReceiptService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ==========================================
    // GENERER PDF
    // ==========================================
    public byte[] generateReceiptPdf(Long txId) {

        Transaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        return buildPdf(tx);
    }

    // ==========================================
    // GENERER PDF AVEC VERIFICATION COMPTE
    // ==========================================
    public byte[] generateReceiptPdfSecure(Long txId, Long currentAccountId) {

        Transaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        // Vérification que la transaction appartient au compte connecté
        if (tx.getAccount() == null || !tx.getAccount().getId().equals(currentAccountId)) {
            throw new RuntimeException("Accès refusé: cette transaction ne vous appartient pas.");
        }

        return buildPdf(tx);
    }

    // ==========================================
    // CONSTRUCTION DU PDF
    // ==========================================
    private byte[] buildPdf(Transaction tx) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A6, 18, 18, 18, 18);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font boldFont  = new Font(Font.HELVETICA, 9,  Font.BOLD);
            Font normalFont= new Font(Font.HELVETICA, 9,  Font.NORMAL);
            Font smallFont = new Font(Font.HELVETICA, 8,  Font.NORMAL);

            // Header
            Paragraph brand = new Paragraph("GP Banque - GAB", titleFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            doc.add(brand);

            Paragraph sub = new Paragraph("Reçu d'opération", normalFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            doc.add(sub);

            doc.add(new Paragraph(" "));
            doc.add(new LineSeparator());
            doc.add(new Paragraph(" "));

            // Données
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String date   = tx.getCreatedAt() != null ? tx.getCreatedAt().format(fmt) : "-";
            String type   = tx.getType()   != null ? tx.getType().name()   : "-";
            String status = tx.getStatus() != null ? tx.getStatus().name() : "-";
            String amount = tx.getAmount() != null ? tx.getAmount().toPlainString() + " UM" : "0 UM";
            String desc   = (tx.getDescription() != null && !tx.getDescription().isBlank())
                    ? tx.getDescription() : "-";
            String gabCode= (tx.getGab() != null) ? tx.getGab().getCode() : "-";

            Account sourceAcc = tx.getAccount();
            String sourceRib  = (sourceAcc != null) ? sourceAcc.getRib() : "-";

            Account benAcc = tx.getBeneficiaryAccount();
            String benRib  = (benAcc != null) ? benAcc.getRib() : null;

            // Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.2f, 1.5f});

            addRow(table, "N° Transaction", String.valueOf(tx.getId()), boldFont, normalFont);
            addRow(table, "Date",           date,                        boldFont, normalFont);
            addRow(table, "Type",           type,                        boldFont, normalFont);
            addRow(table, "Statut",         status,                      boldFont, normalFont);
            addRow(table, "Montant",        amount,                      boldFont, normalFont);
            addRow(table, "GAB",            gabCode,                     boldFont, normalFont);
            addRow(table, "RIB Source",     sourceRib,                   boldFont, normalFont);

            if (benRib != null) {
                addRow(table, "RIB Bénéficiaire", benRib,               boldFont, normalFont);
            }

            addRow(table, "Détails", desc, boldFont, normalFont);

            doc.add(table);

            doc.add(new Paragraph(" "));
            doc.add(new LineSeparator());
            doc.add(new Paragraph("Merci d'avoir utilisé notre GAB.", smallFont));
            doc.add(new Paragraph("Conservez ce reçu pour vos archives.", smallFont));

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF: " + e.getMessage(), e);
        }
    }

    private void addRow(PdfPTable table, String key, String value, Font kFont, Font vFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(key, kFont));
        PdfPCell c2 = new PdfPCell(new Phrase(value, vFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(2);
        c2.setPadding(2);
        table.addCell(c1);
        table.addCell(c2);
    }
}