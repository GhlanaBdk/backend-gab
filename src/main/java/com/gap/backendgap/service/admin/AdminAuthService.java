package com.gap.backendgap.service.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    @Value("${gab.admin.username}")
    private String adminUsername;

    @Value("${gab.admin.password}")
    private String adminPassword;

    public void login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Champs obligatoires.");
        }
        if (!adminUsername.equals(username.trim()) || !adminPassword.equals(password)) {
            throw new IllegalArgumentException("Identifiants admin incorrects.");
        }
    }
}