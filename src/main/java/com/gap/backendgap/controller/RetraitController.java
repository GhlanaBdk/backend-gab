package com.gap.backendgap.controller;

import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/withdraw")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RetraitController {

    private final OperationService operationService;

    public RetraitController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) throw new RuntimeException("Not authenticated");
        return accountId;
    }

    @PostMapping
    public ResponseEntity<?> withdraw(@RequestParam Long gabId,
                                      @RequestParam BigDecimal amount,
                                      HttpSession session) {

        operationService.retrait(getAccountId(session), gabId, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Retrait effectué avec succès");
        response.put("amount", amount);

        return ResponseEntity.ok(response);
    }
}
