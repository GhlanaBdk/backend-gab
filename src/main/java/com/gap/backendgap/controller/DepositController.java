package com.gap.backendgap.controller;

import com.gap.backendgap.dto.CashItemDTO;
import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/deposit")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class DepositController {

    private final OperationService operationService;

    public DepositController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return accountId;
    }

    /**
     * Dépôt cash simulé (billets multiples)
     */
    @PostMapping("/cash")
    public ResponseEntity<?> depositCash(
            @RequestParam Long gabId,
            @RequestBody List<CashItemDTO> items,
            HttpSession session
    ) {

        Long accountId = getAccountId(session);

        BigDecimal total = operationService.depositCash(
                accountId,
                gabId,
                items
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Dépôt effectué avec succès");
        response.put("totalDeposited", total);

        return ResponseEntity.ok(response);
    }
}
