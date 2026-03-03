package com.gap.backendgap.controller.admin;

import com.gap.backendgap.config.SessionAuthFilter;
import com.gap.backendgap.service.admin.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        adminAuthService.login(username, password);
        session.setAttribute("ADMIN_AUTH", true);

        // ✅ Génère token pour mobile
        String token = UUID.randomUUID().toString();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("ADMIN_AUTH", true);
        SessionAuthFilter.tokenStore.put(token, sessionData);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin login successful");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpSession session,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {

        session.invalidate();
        if (token != null) SessionAuthFilter.tokenStore.remove(token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);
    }
}