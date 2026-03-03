package com.gap.backendgap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // HELPER — construction de la réponse JSON
    // ==========================================
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }

    // ==========================================
    // 401 — Non authentifié
    // ==========================================
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurity(SecurityException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Non authentifié", ex.getMessage());
    }

    // ==========================================
    // 400 — Erreurs métier (solde, PIN, carte...)
    // ==========================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        String message = ex.getMessage();

        // 🔴 Carte bloquée ou expirée → 401
        if (message != null && (
                message.contains("Card blocked") ||
                        message.contains("Card not active") ||
                        message.contains("Card expired") ||
                        message.contains("Not authenticated"))) {
            return buildResponse(HttpStatus.UNAUTHORIZED, "Accès refusé", message);
        }

        // 🔴 Solde insuffisant → 422
        if (message != null && message.contains("Solde insuffisant")) {
            return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Solde insuffisant", message);
        }

        // 🔴 Ressource introuvable → 404
        if (message != null && (
                message.contains("introuvable") ||
                        message.contains("not found"))) {
            return buildResponse(HttpStatus.NOT_FOUND, "Ressource introuvable", message);
        }

        // 🔴 Autres erreurs → 400
        return buildResponse(HttpStatus.BAD_REQUEST, "Erreur", message);
    }

    // ==========================================
    // 500 — Erreur serveur inattendue
    // ==========================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur serveur",
                "Une erreur inattendue s'est produite"
        );
    }
}