package com.gap.backendgap.controller;

import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/transfer")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class VirementController {

    private final OperationService operationService;

    public VirementController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) throw new RuntimeException("Not authenticated");
        return accountId;
    }

    @PostMapping
    public ResponseEntity<?> transfer(@RequestParam String beneficiaryRib,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam Long gabId,
                                      HttpSession session) {

        operationService.virement(
                getAccountId(session),
                gabId,
                beneficiaryRib,
                amount
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Virement effectué avec succès");
        response.put("amount", amount);
        response.put("beneficiary", beneficiaryRib);

        return ResponseEntity.ok(response);
    }
}
