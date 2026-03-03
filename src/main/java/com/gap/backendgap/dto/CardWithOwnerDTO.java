package com.gap.backendgap.dto;

import com.gap.backendgap.entity.Card;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour exposer les cartes avec le nom du propriétaire
 * sans toucher aux @JsonIgnore existants dans les entités
 */
public class CardWithOwnerDTO {

    private Long id;
    private String cardNumber;
    private String status;
    private LocalDate expiresAt;
    private Integer triesLeft;
    private LocalDateTime createdAt;

    // ✅ Champ propriétaire directement à la racine — pas besoin de account.user
    private String ownerName;
    private String accountRib;

    // Constructeur depuis entité Card
    public CardWithOwnerDTO(Card card) {
        this.id = card.getId();
        this.cardNumber = card.getCardNumber();
        this.status = card.getStatus().name();
        this.expiresAt = card.getExpiresAt();
        this.triesLeft = card.getTriesLeft();
        this.createdAt = card.getCreatedAt();

        if (card.getAccount() != null) {
            this.accountRib = card.getAccount().getRib();
            if (card.getAccount().getUser() != null) {
                this.ownerName = card.getAccount().getUser().getFullName();
            }
        }
    }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getStatus() { return status; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public Integer getTriesLeft() { return triesLeft; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getOwnerName() { return ownerName; }
    public String getAccountRib() { return accountRib; }
}