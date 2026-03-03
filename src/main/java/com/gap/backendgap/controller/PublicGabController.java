package com.gap.backendgap.controller;

import com.gap.backendgap.repository.GabRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PublicGabController {

    private final GabRepository gabRepository;

    public PublicGabController(GabRepository gabRepository) {
        this.gabRepository = gabRepository;
    }

    // ✅ GET /api/public/gabs — accessible SANS session (pour la page login)
    @GetMapping("/gabs")
    public ResponseEntity<?> getPublicGabs() {
        return ResponseEntity.ok(gabRepository.findAll());
    }
}