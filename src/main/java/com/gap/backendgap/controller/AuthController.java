package com.gap.backendgap.controller;

import com.gap.backendgap.config.SessionAuthFilter;
import com.gap.backendgap.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String cardNumber,
            @RequestParam String pin,
            HttpSession session) {

        authService.login(cardNumber, pin, session);

        // ✅ Génère token pour mobile
        String token = UUID.randomUUID().toString();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("accountId", session.getAttribute("accountId"));
        SessionAuthFilter.tokenStore.put(token, sessionData);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpSession session,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {

        authService.logout(session);
        if (token != null) SessionAuthFilter.tokenStore.remove(token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);
    }
}