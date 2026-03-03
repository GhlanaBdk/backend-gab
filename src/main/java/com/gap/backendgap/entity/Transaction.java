package com.gap.backendgap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    // ✅ CHEQUIER ajouté
    public enum Type { RETRAIT, DEPOT, VIREMENT, CHEQUIER }
    public enum Status { SUCCESS, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Account =====
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "cards"})
    private Account account;

    // ===== GAB =====
    // ✅ optional = true — car traitement admin n'a pas de GAB
    @ManyToOne(optional = true)
    @JoinColumn(name = "gab_id", nullable = true)
    @JsonIgnoreProperties({"transactions"})
    private Gab gab;

    // ===== Type =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    // ===== Amount =====
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // ===== Beneficiary (for transfer only) =====
    @ManyToOne
    @JoinColumn(name = "beneficiary_account_id")
    @JsonIgnoreProperties({"transactions", "cards"})
    private Account beneficiaryAccount;

    // ===== Status =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SUCCESS;

    // ===== Description =====
    @Column(length = 255)
    private String description;

    // ===== Date =====
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}