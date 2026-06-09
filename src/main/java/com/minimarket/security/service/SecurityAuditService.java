package com.minimarket.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuditService.class);

    public void failedLogin(String username, String remoteAddress) {
        LOGGER.warn("Login fallido. usuario={}, origen={}", safe(username), safe(remoteAddress));
    }

    public void invalidToken(String reason, String remoteAddress) {
        LOGGER.warn("Token invalido. motivo={}, origen={}", safe(reason), safe(remoteAddress));
    }

    public void accessDenied(String username, String path, String remoteAddress) {
        LOGGER.warn("Acceso denegado. usuario={}, ruta={}, origen={}", safe(username), safe(path), safe(remoteAddress));
    }

    public void suspiciousRequest(String path, String payload, String remoteAddress) {
        LOGGER.warn("Solicitud sospechosa. ruta={}, dato={}, origen={}", safe(path), safe(payload), safe(remoteAddress));
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "no_disponible";
        }
        return value.replaceAll("[\\r\\n\\t]", "_");
    }
}
