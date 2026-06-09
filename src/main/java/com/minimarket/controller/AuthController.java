package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.dto.AuthResponse;
import com.minimarket.security.dto.RegisterRequest;
import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.SecurityAuditService;
import com.minimarket.security.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityAuditService securityAuditService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService,
                          UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          SecurityAuditService securityAuditService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityAuditService = securityAuditService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return ResponseEntity.ok(new AuthResponse(token, "Bearer", userDetails.getUsername(), roles));
        } catch (AuthenticationException ex) {
            securityAuditService.failedLogin(loginRequest == null ? null : loginRequest.getUsername(), request.getRemoteAddr());
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest == null || registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (usuarioRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(409).build();
        }

        Rol cliente = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol cliente no disponible"));

        Usuario usuario = new Usuario();
        usuario.setUsername(registerRequest.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRoles(Set.of(cliente));
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", userDetails.getUsername(), roles));
    }
}
