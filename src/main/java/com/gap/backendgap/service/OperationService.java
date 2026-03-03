package com.gap.backendgap.service;

import com.gap.backendgap.dto.CashItemDTO;
import com.gap.backendgap.entity.*;
import com.gap.backendgap.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OperationService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ChequebookRequestRepository chequebookRequestRepository;
    private final GabRepository gabRepository;

    public OperationService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository,
                            ChequebookRequestRepository chequebookRequestRepository,
                            GabRepository gabRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.chequebookRequestRepository = chequebookRequestRepository;
        this.gabRepository = gabRepository;
    }

    // ================= HELPERS =================

    private Account getActiveAccount(Long accountId) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        if (acc.getStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Compte non actif");
        }
        return acc;
    }

    private Gab getGab(Long gabId) {
        return gabRepository.findById(gabId)
                .orElseThrow(() -> new RuntimeException("GAB introuvable"));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Montant invalide");
        }
    }

    // ================= RETRAIT =================

    @Transactional
    public void retrait(Long accountId, Long gabId, BigDecimal amount) {

        validateAmount(amount);

        Account acc = getActiveAccount(accountId);
        Gab gab = getGab(gabId);

        if (acc.getBalance().compareTo(amount) < 0) {
            createTx(acc, gab, Transaction.Type.RETRAIT,
                    amount,
                    Transaction.Status.FAILED,
                    "Solde insuffisant",
                    null);
            throw new RuntimeException("Solde insuffisant");
        }

        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);

        createTx(acc, gab, Transaction.Type.RETRAIT,
                amount,
                Transaction.Status.SUCCESS,
                "Retrait effectué",
                null);
    }

    // ================= DEPOT CASH =================

    @Transactional
    public BigDecimal depositCash(Long accountId,
                                  Long gabId,
                                  List<CashItemDTO> items) {

        Account account = getActiveAccount(accountId);
        Gab gab = getGab(gabId);

        BigDecimal total = BigDecimal.ZERO;

        for (CashItemDTO item : items) {
            if (item.getDenomination() != null && item.getQuantity() != null) {
                BigDecimal line = item.getDenomination()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(line);
            }
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Montant invalide");
        }

        account.setBalance(account.getBalance().add(total));
        accountRepository.save(account);

        createTx(account,
                gab,
                Transaction.Type.DEPOT,
                total,
                Transaction.Status.SUCCESS,
                "Dépôt cash via GAB " + gab.getCode(),
                null);

        return total;
    }

    // ================= VIREMENT =================

    @Transactional
    public void virement(Long sourceAccountId,
                         Long gabId,
                         String beneficiaryRib,
                         BigDecimal amount) {

        validateAmount(amount);

        Account source = getActiveAccount(sourceAccountId);
        Gab gab = getGab(gabId);

        Account beneficiary = accountRepository.findByRib(beneficiaryRib)
                .orElseThrow(() -> new RuntimeException("Compte bénéficiaire introuvable"));

        if (source.getBalance().compareTo(amount) < 0) {
            createTx(source, gab, Transaction.Type.VIREMENT,
                    amount,
                    Transaction.Status.FAILED,
                    "Solde insuffisant",
                    beneficiary);
            throw new RuntimeException("Solde insuffisant");
        }

        source.setBalance(source.getBalance().subtract(amount));
        beneficiary.setBalance(beneficiary.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(beneficiary);

        createTx(source, gab, Transaction.Type.VIREMENT,
                amount,
                Transaction.Status.SUCCESS,
                "Virement vers " + beneficiary.getRib(),
                beneficiary);

        createTx(beneficiary, gab, Transaction.Type.VIREMENT,
                amount,
                Transaction.Status.SUCCESS,
                "Virement reçu de " + source.getRib(),
                source);
    }

    // ================= HISTORIQUE =================

    public Page<Transaction> historique(Long accountId, int page, int size) {

        Account acc = getActiveAccount(accountId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return transactionRepository.findByAccount(acc, pageable);
    }

    // ================= BALANCE =================

    public BigDecimal getBalance(Long accountId) {
        return getActiveAccount(accountId).getBalance();
    }

    // ================= CHEQUIER =================

    // ✅ CORRECTION : gabId obligatoire car Transaction.gab est @ManyToOne(optional = false)
    @Transactional
    public void demandeChequier(Long accountId,
                                Long gabId,
                                ChequebookRequest.Pages pages) {

        Account account = getActiveAccount(accountId);
        Gab gab = getGab(gabId); // ✅ on récupère le GAB au lieu de passer null

        ChequebookRequest request = new ChequebookRequest();
        request.setAccount(account);
        request.setPages(pages);
        request.setStatus(ChequebookRequest.Status.REQUESTED);
        chequebookRequestRepository.save(request);

        // ✅ gab est passé correctement — plus de null
        createTx(account,
                gab,
                Transaction.Type.DEPOT,
                BigDecimal.ZERO,
                Transaction.Status.SUCCESS,
                "Demande chéquier: " + pages,
                null);
    }

    // ================= HELPER TRANSACTION =================

    private void createTx(Account account,
                          Gab gab,
                          Transaction.Type type,
                          BigDecimal amount,
                          Transaction.Status status,
                          String description,
                          Account beneficiary) {

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setGab(gab);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setStatus(status);
        tx.setDescription(description);
        tx.setCreatedAt(java.time.LocalDateTime.now());

        if (beneficiary != null) {
            tx.setBeneficiaryAccount(beneficiary);
        }

        transactionRepository.save(tx);
    }
}