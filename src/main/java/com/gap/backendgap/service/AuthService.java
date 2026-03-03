package com.gap.backendgap.service;

import com.gap.backendgap.entity.Card;
import com.gap.backendgap.repository.CardRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(CardRepository cardRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String cardNumber, String pin, HttpSession session) {

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getStatus() != Card.Status.ACTIVE) {
            throw new RuntimeException("Card not active");
        }

        // ✅ CORRECTION : sauvegarde le statut EXPIRED en base
        if (card.getExpiresAt().isBefore(LocalDate.now())) {
            card.setStatus(Card.Status.EXPIRED);
            cardRepository.save(card);
            throw new RuntimeException("Card expired");
        }

        if (card.getTriesLeft() <= 0) {
            card.setStatus(Card.Status.BLOCKED);
            cardRepository.save(card);
            throw new RuntimeException("Card blocked");
        }

        if (!passwordEncoder.matches(pin, card.getPinHash())) {
            card.setTriesLeft(card.getTriesLeft() - 1);
            cardRepository.save(card);
            throw new RuntimeException("Invalid PIN");
        }

        // ✅ Réinitialiser les tentatives après succès
        card.setTriesLeft(3);
        cardRepository.save(card);

        // ✅ Créer la session
        session.setAttribute("accountId", card.getAccount().getId());
        session.setAttribute("cardId", card.getId());

        return "Login successful";
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}