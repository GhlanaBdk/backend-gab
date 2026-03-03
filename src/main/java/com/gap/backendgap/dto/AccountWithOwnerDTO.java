package com.gap.backendgap.dto;

import com.gap.backendgap.entity.Account;
import java.time.LocalDateTime;

public class AccountWithOwnerDTO {

    private Long id;
    private String rib;
    private Double balance;
    private String status;
    private LocalDateTime createdAt;
    private String ownerName;   // ✅ nom du propriétaire directement

    public AccountWithOwnerDTO(Account account) {
        this.id = account.getId();
        this.rib = account.getRib();
        this.balance = account.getBalance() != null ? account.getBalance().doubleValue() : 0.0;
        this.status = account.getStatus().name();
        this.createdAt = account.getCreatedAt();
        if (account.getUser() != null) {
            this.ownerName = account.getUser().getFullName();
        }
    }

    public Long getId() { return id; }
    public String getRib() { return rib; }
    public Double getBalance() { return balance; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getOwnerName() { return ownerName; }
}