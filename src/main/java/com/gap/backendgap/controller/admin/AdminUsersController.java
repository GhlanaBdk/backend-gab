package com.gap.backendgap.controller.admin;

import com.gap.backendgap.service.admin.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminUsersController {

    private final AdminUserService adminUserService;

    public AdminUsersController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // GET /api/admin/users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // POST /api/admin/users/create
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @RequestParam String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam String initialBalance,
            @RequestParam String pin) {

        var user = adminUserService.createCompleteUser(
                fullName, email, phone, initialBalance, pin);

        return ResponseEntity.ok(Map.of(
                "message", "Client créé avec succès",
                "userId", user.getId(),
                "fullName", user.getFullName()
        ));
    }
}