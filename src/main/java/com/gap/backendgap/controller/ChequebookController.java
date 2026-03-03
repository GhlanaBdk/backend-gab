package com.gap.backendgap.controller;

import com.gap.backendgap.entity.ChequebookRequest;
import com.gap.backendgap.service.OperationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client/chequebook")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ChequebookController {

    private final OperationService operationService;

    public ChequebookController(OperationService operationService) {
        this.operationService = operationService;
    }

    private Long getAccountId(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) throw new RuntimeException("Not authenticated");
        return accountId;
    }

    @PostMapping
    public ResponseEntity<?> requestChequebook(
            @RequestParam Long gabId,
            @RequestParam ChequebookRequest.Pages pages,
            HttpSession session
    ) {

        operationService.demandeChequier(
                getAccountId(session),
                gabId,
                pages
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Demande de chéquier envoyée");
        response.put("pages", pages);
        response.put("gabId", gabId);

        return ResponseEntity.ok(response);
    }
}