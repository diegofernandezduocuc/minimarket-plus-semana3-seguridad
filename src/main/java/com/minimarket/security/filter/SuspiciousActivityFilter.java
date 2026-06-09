package com.minimarket.security.filter;

import com.minimarket.security.service.SecurityAuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class SuspiciousActivityFilter extends OncePerRequestFilter {

    private final SecurityAuditService securityAuditService;

    private final List<String> blockedPatterns = List.of(
            "<script",
            "</script",
            "javascript:",
            " or 1=1",
            "' or '1'='1",
            "union select",
            "drop table",
            "--",
            "/*",
            "*/"
    );

    public SuspiciousActivityFilter(SecurityAuditService securityAuditService) {
        this.securityAuditService = securityAuditService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String query = request.getQueryString();

        if (containsSuspiciousPattern(path, path, request, response)) {
            return;
        }

        if (containsSuspiciousPattern(path, query, request, response)) {
            return;
        }

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (containsSuspiciousPattern(path, entry.getKey(), request, response)) {
                return;
            }

            for (String value : entry.getValue()) {
                if (containsSuspiciousPattern(path, value, request, response)) {
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean containsSuspiciousPattern(String path,
                                              String value,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        if (value == null || value.isBlank()) {
            return false;
        }

        String evaluated = value.toLowerCase(Locale.ROOT);

        for (String pattern : blockedPatterns) {
            if (evaluated.contains(pattern)) {
                securityAuditService.suspiciousRequest(path, pattern, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Solicitud rechazada por patron sospechoso\"}");
                return true;
            }
        }

        return false;
    }
}