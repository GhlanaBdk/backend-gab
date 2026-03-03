package com.gap.backendgap.controller.admin;

import com.gap.backendgap.dto.ChequebookWithClientDTO;
import com.gap.backendgap.entity.ChequebookRequest;
import com.gap.backendgap.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/chequebooks")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminChequebookController {

    private final AdminService adminService;

    public AdminChequebookController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ Retourne ChequebookWithClientDTO avec clientName et requestedAt inclus
    @GetMapping
    public ResponseEntity<?> getAllChequebooks() {
        List<ChequebookWithClientDTO> list = adminService.allChequebookRequests()
                .stream()
                .map(ChequebookWithClientDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> processChequebook(@PathVariable Long id,
                                               @RequestParam String status) {
        adminService.processChequebook(id, ChequebookRequest.Status.valueOf(status));
        return ResponseEntity.ok(Map.of("message", "Statut mis à jour.", "id", id));
    }
}