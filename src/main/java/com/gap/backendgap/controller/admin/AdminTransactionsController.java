package com.gap.backendgap.controller.admin;

import com.gap.backendgap.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/transactions")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminTransactionsController {

    private final AdminService adminService;

    public AdminTransactionsController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admin/transactions
    @GetMapping
    public ResponseEntity<?> getTransactions() {
        return ResponseEntity.ok(adminService.lastTransactions());
    }
}