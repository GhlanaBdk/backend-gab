package com.gap.backendgap.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceiptResponseDTO {

    private Long transactionId;
    private String type;
    private BigDecimal amount;
    private String status;
    private String gabCode;
    private String sourceRib;
    private String beneficiaryRib;
    private String description;
    private LocalDateTime createdAt;

    public ReceiptResponseDTO(Long transactionId,
                              String type,
                              BigDecimal amount,
                              String status,
                              String gabCode,
                              String sourceRib,
                              String beneficiaryRib,
                              String description,
                              LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.gabCode = gabCode;
        this.sourceRib = sourceRib;
        this.beneficiaryRib = beneficiaryRib;
        this.description = description;
        this.createdAt = createdAt;
    }

}
