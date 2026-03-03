package com.gap.backendgap.controller;

import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/balance")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BalanceController {

    private final OperationService operationService;

    public BalanceController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) throw new RuntimeException("Not authenticated");
        return accountId;
    }

    // ==========================================
    // GET /api/client/balance
    // ==========================================
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBalance(HttpSession session) {

        BigDecimal balance = operationService.getBalance(getAccountId(session));

        Map<String, Object> response = new HashMap<>();
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }
}