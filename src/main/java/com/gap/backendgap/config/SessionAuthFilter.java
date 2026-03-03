package com.gap.backendgap.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    // ✅ Map publique statique — accessible depuis les controllers
    public static final ConcurrentHashMap<String, Map<String, Object>> tokenStore
            = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ Méthode 1 — Token dans header (pour mobile)
        String token = request.getHeader("X-Auth-Token");
        if (token != null && tokenStore.containsKey(token)) {
            Map<String, Object> data = tokenStore.get(token);
            if (Boolean.TRUE.equals(data.get("ADMIN_AUTH"))) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("admin", null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                );
            } else if (data.get("accountId") != null) {
                Long accountId = (Long) data.get("accountId");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(accountId, null,
                                List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
                );
            }
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Méthode 2 — Cookie session (pour PC)
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (Boolean.TRUE.equals(session.getAttribute("ADMIN_AUTH"))) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("admin", null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                );
            } else if (session.getAttribute("accountId") != null) {
                Long accountId = (Long) session.getAttribute("accountId");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(accountId, null,
                                List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}