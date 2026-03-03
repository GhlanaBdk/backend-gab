package com.gap.backendgap.repository;

import com.gap.backendgap.entity.Account;
import com.gap.backendgap.entity.Gab;
import com.gap.backendgap.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ================= ACCOUNT =================

    List<Transaction> findTop10ByAccountOrderByCreatedAtDesc(Account account);

    Page<Transaction> findByAccount(Account account, Pageable pageable);

    Page<Transaction> findByAccountAndType(
            Account account,
            Transaction.Type type,
            Pageable pageable
    );

    Page<Transaction> findByAccountAndCreatedAtBetween(
            Account account,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Transaction> findByAccountAndTypeAndCreatedAtBetween(
            Account account,
            Transaction.Type type,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // ================= GAB =================

    Page<Transaction> findByGab(Gab gab, Pageable pageable);

    List<Transaction> findByGabOrderByCreatedAtDesc(Gab gab);

    Page<Transaction> findByGabAndType(
            Gab gab,
            Transaction.Type type,
            Pageable pageable
    );

    Page<Transaction> findByGabAndCreatedAtBetween(
            Gab gab,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // ================= ACCOUNT + GAB =================

    Page<Transaction> findByAccountAndGab(
            Account account,
            Gab gab,
            Pageable pageable
    );

    // ================= ADMIN GLOBAL =================

    List<Transaction> findTop100ByOrderByCreatedAtDesc();

}
