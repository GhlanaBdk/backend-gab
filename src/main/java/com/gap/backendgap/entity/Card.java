package com.gap.backendgap.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
@Entity
@Table(name = "cards")
public class Card {

    public enum Status { ACTIVE, BLOCKED, EXPIRED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    // ✅ CORRIGÉ : on ignore transactions et cards MAIS on laisse passer "user"
    // Avant : @JsonIgnoreProperties({"transactions", "cards"})
    // Problème : "user" n'était pas ignoré mais Account.java avait peut-être @JsonIgnore sur user
    @JsonIgnoreProperties({"transactions", "chequebookRequests", "cards", "pinHash"})
    private Account account;

    @Column(nullable = false, unique = true, length = 19)
    private String cardNumber;

    @Column(nullable = false)
    private String pinHash;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private LocalDate expiresAt;

    private Integer triesLeft = 3;

    private LocalDateTime createdAt = LocalDateTime.now();
}