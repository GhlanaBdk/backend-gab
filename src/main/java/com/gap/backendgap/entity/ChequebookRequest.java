package com.gap.backendgap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "chequebook_requests")
public class ChequebookRequest {

    public enum Pages {
        P50,
        P100
    }

    public enum Status {
        REQUESTED,
        APPROVED,
        REJECTED,
        DELIVERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Account =====
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "cards"})
    private Account account;

    // ===== Pages =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private Pages pages;

    // ===== Status =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Status status = Status.REQUESTED;

    // ===== Dates =====
    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime processedAt;

    // ================= GETTERS =================

    // ================= SETTERS =================


}
