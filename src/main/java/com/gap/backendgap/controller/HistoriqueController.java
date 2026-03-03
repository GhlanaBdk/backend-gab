package com.gap.backendgap.controller;

import com.gap.backendgap.entity.Transaction;
import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/history")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class HistoriqueController {

    private final OperationService operationService;

    public HistoriqueController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null)
            throw new RuntimeException("Not authenticated");
        return accountId;
    }

    @GetMapping
    public Page<Transaction> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        return operationService.historique(
                getAccountId(session),
                page,
                size
        );
    }
}
