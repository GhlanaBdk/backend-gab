package com.gap.backendgap.service.admin;

import com.gap.backendgap.entity.*;
import com.gap.backendgap.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final ChequebookRequestRepository chequebookRequestRepository;
    private final GabRepository gabRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(AccountRepository accountRepository,
                        CardRepository cardRepository,
                        TransactionRepository transactionRepository,
                        ChequebookRequestRepository chequebookRequestRepository,
                        GabRepository gabRepository,
                        BCryptPasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.chequebookRequestRepository = chequebookRequestRepository;
        this.gabRepository = gabRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= COMPTES =================

    public List<Account> allAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public void setAccountStatus(Long accountId, Account.Status status) {
        Account a = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        a.setStatus(status);
        accountRepository.save(a);
    }

    // ================= CARTES =================

    // ✅ CORRIGÉ : utilise findAllWithAccountAndUser() pour charger le propriétaire
    public List<Card> allCards() {
        return cardRepository.findAllWithAccountAndUser();
    }

    @Transactional
    public void blockCard(Long cardId) {
        Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Carte introuvable."));
        c.setStatus(Card.Status.BLOCKED);
        c.setTriesLeft(0);
        cardRepository.save(c);
    }

    @Transactional
    public void unblockCard(Long cardId) {
        Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Carte introuvable."));
        if (c.getExpiresAt().isBefore(java.time.LocalDate.now())) {
            c.setStatus(Card.Status.EXPIRED);
        } else {
            c.setStatus(Card.Status.ACTIVE);
            c.setTriesLeft(3);
        }
        cardRepository.save(c);
    }

    @Transactional
    public void resetCardPin(Long cardId, String newPin) {
        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN invalide. Entrez exactement 4 chiffres.");
        }
        Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Carte introuvable."));

        if (c.getExpiresAt().isBefore(java.time.LocalDate.now())) {
            c.setStatus(Card.Status.EXPIRED);
            c.setTriesLeft(0);
            cardRepository.save(c);
            throw new IllegalArgumentException("Carte expirée : PIN non modifié.");
        }

        c.setPinHash(passwordEncoder.encode(newPin));
        c.setTriesLeft(3);
        c.setStatus(Card.Status.ACTIVE);
        cardRepository.save(c);
    }

    // ================= TRANSACTIONS =================

    public List<Transaction> lastTransactions() {
        return transactionRepository.findTop100ByOrderByCreatedAtDesc();
    }

    // ================= CHEQUIER =================

    public List<ChequebookRequest> allChequebookRequests() {
        return chequebookRequestRepository.findAllByOrderByRequestedAtDesc();
    }

    @Transactional
    public void processChequebook(Long requestId, ChequebookRequest.Status newStatus) {
        ChequebookRequest req = chequebookRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

        req.setStatus(newStatus);
        req.setProcessedAt(LocalDateTime.now());
        chequebookRequestRepository.save(req);

        Transaction tx = new Transaction();
        tx.setAccount(req.getAccount());
        tx.setGab(null);
        tx.setType(Transaction.Type.CHEQUIER);
        tx.setAmount(BigDecimal.ZERO);
        tx.setStatus(Transaction.Status.SUCCESS);
        tx.setDescription("Chéquier " + newStatus.name() + " (" + req.getPages().name() + ")");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    // ================= GABs =================

    public List<Gab> allGabs() {
        return gabRepository.findAll();
    }

    @Transactional
    public Gab createGab(String code, String location) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code GAB est obligatoire.");
        }
        if (gabRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Code GAB déjà utilisé.");
        }
        Gab gab = new Gab();
        gab.setCode(code.trim());
        gab.setLocation(location != null ? location.trim() : "");
        return gabRepository.save(gab);
    }

    // ================= DASHBOARD =================

    public java.util.Map<String, Long> dashboardStats() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        stats.put("users", (long) allAccounts().size());
        stats.put("cards", (long) allCards().size());
        stats.put("transactions", (long) lastTransactions().size());
        stats.put("chequebooks", (long) allChequebookRequests().size());
        stats.put("gabs", (long) allGabs().size());
        return stats;
    }
}