package com.gap.backendgap.controller.admin;

import com.gap.backendgap.dto.CardWithOwnerDTO;
import com.gap.backendgap.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/cards")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminCardsController {

    private final AdminService adminService;

    public AdminCardsController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ CORRIGÉ : retourne CardWithOwnerDTO avec ownerName inclus
    @GetMapping
    public ResponseEntity<?> getAllCards() {
        List<CardWithOwnerDTO> cards = adminService.allCards()
                .stream()
                .map(CardWithOwnerDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockCard(@PathVariable Long id) {
        adminService.blockCard(id);
        return ResponseEntity.ok(Map.of("message", "Carte bloquée.", "cardId", id));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable Long id) {
        adminService.unblockCard(id);
        return ResponseEntity.ok(Map.of("message", "Carte débloquée.", "cardId", id));
    }

    @PostMapping("/{id}/reset-pin")
    public ResponseEntity<?> resetPin(@PathVariable Long id,
                                      @RequestParam String newPin) {
        adminService.resetCardPin(id, newPin);
        return ResponseEntity.ok(Map.of("message", "PIN réinitialisé.", "cardId", id));
    }
}