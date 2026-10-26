package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.infrastructure.security.dto.AuthResponse;
import com.uniquindio.ecommerce.infrastructure.security.dto.LoginRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.RegisterRequest;
import com.uniquindio.ecommerce.infrastructure.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        // El filtro JWT ya valida el token, si llega aquí es válido
        return ResponseEntity.ok().build();
    }
}