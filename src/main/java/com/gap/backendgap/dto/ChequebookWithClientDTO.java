package com.gap.backendgap.dto;

import com.gap.backendgap.entity.ChequebookRequest;
import java.time.LocalDateTime;

public class ChequebookWithClientDTO {

    private Long id;
    private String pages;
    private String status;
    private LocalDateTime requestedAt;   // ✅ date de demande
    private LocalDateTime processedAt;
    private String clientName;           // ✅ nom du client

    public ChequebookWithClientDTO(ChequebookRequest req) {
        this.id = req.getId();
        this.pages = req.getPages().name();
        this.status = req.getStatus().name();
        this.requestedAt = req.getRequestedAt();
        this.processedAt = req.getProcessedAt();
        if (req.getAccount() != null && req.getAccount().getUser() != null) {
            this.clientName = req.getAccount().getUser().getFullName();
        }
    }

    public Long getId() { return id; }
    public String getPages() { return pages; }
    public String getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getClientName() { return clientName; }
}