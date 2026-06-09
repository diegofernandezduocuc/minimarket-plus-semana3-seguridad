package com.minimarket.security.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String tipo;
    private String username;
    private List<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tipo, String username, List<String> roles) {
        this.token = token;
        this.tipo = tipo;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
