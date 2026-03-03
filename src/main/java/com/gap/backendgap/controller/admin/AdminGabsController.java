package com.gap.backendgap.controller.admin;

import com.gap.backendgap.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/gabs")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminGabsController {

    private final AdminService adminService;

    public AdminGabsController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admin/gabs
    @GetMapping
    public ResponseEntity<?> getAllGabs() {
        return ResponseEntity.ok(adminService.allGabs());
    }

    // POST /api/admin/gabs/create
    @PostMapping("/create")
    public ResponseEntity<?> createGab(@RequestParam String code,
                                       @RequestParam String location) {
        var gab = adminService.createGab(code, location);
        return ResponseEntity.ok(Map.of(
                "message", "GAB créé avec succès.",
                "gabId", gab.getId(),
                "code", gab.getCode()
        ));
    }
}