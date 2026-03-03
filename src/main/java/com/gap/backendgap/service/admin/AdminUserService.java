package com.gap.backendgap.service.admin;

import com.gap.backendgap.entity.*;
import com.gap.backendgap.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public AdminUserService(UserRepository userRepository,
                            AccountRepository accountRepository,
                            CardRepository cardRepository,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= HELPERS =================

    private String generateUniqueRib() {
        String rib;
        do {
            rib = String.format("RIB%013d", random.nextInt(1000000000));
        } while (accountRepository.findByRib(rib).isPresent());
        return rib;
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = String.format("%016d",
                    Math.abs(random.nextLong() % 10000000000000000L));
        } while (cardRepository.findByCardNumber(cardNumber).isPresent());
        return cardNumber;
    }

    // ================= CRÉER CLIENT COMPLET =================

    @Transactional
    public User createCompleteUser(String fullName, String email,
                                   String phone, String initialBalance,
                                   String pin) {
        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom complet est obligatoire.");
        }
        if (email != null && !email.trim().isEmpty() &&
                userRepository.findByEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé.");
        }
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN invalide (4 chiffres requis).");
        }

        BigDecimal balance;
        try {
            balance = new BigDecimal(initialBalance);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le solde initial ne peut pas être négatif.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Solde invalide.");
        }

        // 1️⃣ User
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        user.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        user = userRepository.save(user);

        // 2️⃣ Account
        Account account = new Account();
        account.setUser(user);
        account.setRib(generateUniqueRib());
        account.setBalance(balance);
        account.setStatus(Account.Status.ACTIVE);
        account = accountRepository.save(account);

        // 3️⃣ Card
        Card card = new Card();
        card.setAccount(account);
        card.setCardNumber(generateUniqueCardNumber());
        card.setPinHash(passwordEncoder.encode(pin));
        card.setStatus(Card.Status.ACTIVE);
        card.setExpiresAt(LocalDate.now().plusYears(3));
        card.setTriesLeft(3);
        cardRepository.save(card);

        return user;
    }

    // ================= LISTE =================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}