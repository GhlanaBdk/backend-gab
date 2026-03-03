package com.gap.backendgap.controller;

import com.gap.backendgap.dto.ReceiptResponseDTO;
import com.gap.backendgap.entity.Transaction;
import com.gap.backendgap.repository.TransactionRepository;
import com.gap.backendgap.service.ReceiptService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/receipt")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReceiptController {

    private final TransactionRepository transactionRepository;
    private final ReceiptService receiptService;

    public ReceiptController(TransactionRepository transactionRepository,
                             ReceiptService receiptService) {
        this.transactionRepository = transactionRepository;
        this.receiptService = receiptService;
    }

    // 🔹 Get receipt JSON
    @GetMapping("/{id}")
    public ReceiptResponseDTO getReceipt(@PathVariable Long id) {

        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        return new ReceiptResponseDTO(
                tx.getId(),
                tx.getType().name(),
                tx.getAmount(),
                tx.getStatus().name(),
                tx.getGab().getCode(),
                tx.getAccount().getRib(),
                tx.getBeneficiaryAccount() != null ?
                        tx.getBeneficiaryAccount().getRib() : null,
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }

    // 🔹 Download PDF
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        byte[] pdf = receiptService.generateReceiptPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=receipt-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
