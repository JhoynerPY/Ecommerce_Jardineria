package com.uniquindio.ecommerce.infrastructure.security.service;

import com.uniquindio.ecommerce.infrastructure.security.dto.AuthResponse;
import com.uniquindio.ecommerce.infrastructure.security.dto.LoginRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.RegisterRequest;
import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.security.jwt.JwtUtil;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new ReglaDominioException("Ya existe un usuario con el email: " + request.getEmail());
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .roles(request.getRoles() != null && !request.getRoles().isEmpty() 
                    ? request.getRoles() 
                    : java.util.List.of("COMPRADOR"))
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(usuario.getRoles())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UsuarioEntity usuario = usuarioRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ReglaDominioException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(usuario.getRoles())
                .build();
    }
}