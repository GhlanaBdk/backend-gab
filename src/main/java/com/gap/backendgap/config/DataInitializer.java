package com.gap.backendgap.config;

import com.gap.backendgap.entity.*;
import com.gap.backendgap.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final GabRepository gabRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AccountRepository accountRepository,
                           CardRepository cardRepository,
                           GabRepository gabRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.gabRepository = gabRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ✅ Éviter de recréer les données à chaque démarrage
        if (cardRepository.findByCardNumber("1111222233334444").isPresent()) {
            System.out.println("ℹ️  Seed already exists — skipping.");
            return;
        }

        // ==========================================
        // GAB PAR DÉFAUT
        // ==========================================
        Gab gab = new Gab();
        gab.setCode("GAB-001");
        gab.setLocation("Agence Principale - Nouakchott");
        gabRepository.save(gab);

        // ==========================================
        // USER 1 — Ahmed Mohamed
        // ==========================================
        User u1 = new User();
        u1.setFullName("Ahmed Mohamed");
        u1.setEmail("ahmed@test.com");
        u1.setPhone("22223333");
        u1 = userRepository.save(u1);

        Account a1 = new Account();
        a1.setUser(u1);
        a1.setRib("RIB001");
        a1.setBalance(new BigDecimal("5000.00"));
        a1.setStatus(Account.Status.ACTIVE);
        a1 = accountRepository.save(a1);

        Card c1 = new Card();
        c1.setAccount(a1);
        c1.setCardNumber("1111222233334444");
        c1.setPinHash(passwordEncoder.encode("1234"));
        c1.setStatus(Card.Status.ACTIVE);
        c1.setExpiresAt(LocalDate.of(2028, 12, 31));
        c1.setTriesLeft(3);
        cardRepository.save(c1);

        // ==========================================
        // USER 2 — Sara Ali
        // ==========================================
        User u2 = new User();
        u2.setFullName("Sara Ali");
        u2.setEmail("sara@test.com");
        u2.setPhone("33334444");
        u2 = userRepository.save(u2);

        Account a2 = new Account();
        a2.setUser(u2);
        a2.setRib("RIB002");
        a2.setBalance(new BigDecimal("8000.00"));
        a2.setStatus(Account.Status.ACTIVE);
        a2 = accountRepository.save(a2);

        Card c2 = new Card();
        c2.setAccount(a2);
        c2.setCardNumber("5555666677778888");
        c2.setPinHash(passwordEncoder.encode("4321"));
        c2.setStatus(Card.Status.ACTIVE);
        c2.setExpiresAt(LocalDate.of(2029, 6, 30));
        c2.setTriesLeft(3);
        cardRepository.save(c2);

        // ==========================================
        // USER 3 — Omar Hassan
        // ==========================================
        User u3 = new User();
        u3.setFullName("Omar Hassan");
        u3.setEmail("omar@test.com");
        u3.setPhone("44445555");
        u3 = userRepository.save(u3);

        Account a3 = new Account();
        a3.setUser(u3);
        a3.setRib("RIB003");
        a3.setBalance(new BigDecimal("12000.00"));
        a3.setStatus(Account.Status.ACTIVE);
        a3 = accountRepository.save(a3);

        Card c3 = new Card();
        c3.setAccount(a3);
        c3.setCardNumber("9999000011112222");
        c3.setPinHash(passwordEncoder.encode("9999"));
        c3.setStatus(Card.Status.ACTIVE);
        c3.setExpiresAt(LocalDate.of(2030, 1, 1));
        c3.setTriesLeft(3);
        cardRepository.save(c3);

        // ==========================================
        // RÉSUMÉ
        // ==========================================
        System.out.println("✅ Seed done: 1 GAB, 3 users, 3 accounts, 3 cards.");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 Données de test:");
        System.out.println("   GAB ID : 1   Code: GAB-001");
        System.out.println("   ─────────────────────────────────────");
        System.out.println("   Card: 1111222233334444  PIN: 1234  Balance: 5000.00  → Ahmed");
        System.out.println("   Card: 5555666677778888  PIN: 4321  Balance: 8000.00  → Sara");
        System.out.println("   Card: 9999000011112222  PIN: 9999  Balance: 12000.00 → Omar");
        System.out.println("   ─────────────────────────────────────");
        System.out.println("   RIB Ahmed : RIB001");
        System.out.println("   RIB Sara  : RIB002");
        System.out.println("   RIB Omar  : RIB003");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}