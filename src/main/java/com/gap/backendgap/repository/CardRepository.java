package com.gap.backendgap.repository;

import com.gap.backendgap.entity.Card;
import com.gap.backendgap.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByAccount(Account account);

    // ✅ NOUVEAU : charge account + user en une seule requête SQL
    // Résout le problème "Propriétaire — " dans la page admin Cartes
    @Query("SELECT c FROM Card c " +
            "JOIN FETCH c.account a " +
            "JOIN FETCH a.user u " +
            "ORDER BY c.id ASC")
    List<Card> findAllWithAccountAndUser();
}