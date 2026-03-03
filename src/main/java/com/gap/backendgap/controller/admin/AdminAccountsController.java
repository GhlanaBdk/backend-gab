package com.gap.backendgap.controller.admin;

import com.gap.backendgap.dto.AccountWithOwnerDTO;
import com.gap.backendgap.entity.Account;
import com.gap.backendgap.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/accounts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminAccountsController {

    private final AdminService adminService;

    public AdminAccountsController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ Retourne AccountWithOwnerDTO avec ownerName inclus
    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        List<AccountWithOwnerDTO> accounts = adminService.allAccounts()
                .stream()
                .map(AccountWithOwnerDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> setStatus(@PathVariable Long id,
                                       @RequestParam String status) {
        adminService.setAccountStatus(id, Account.Status.valueOf(status));
        return ResponseEntity.ok(Map.of("message", "Statut mis à jour.", "accountId", id));
    }
}