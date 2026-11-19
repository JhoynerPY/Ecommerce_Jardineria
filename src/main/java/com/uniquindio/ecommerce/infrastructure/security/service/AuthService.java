package com.uniquindio.ecommerce.infrastructure.security.service;

import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new ReglaDominioException("Ya existe un usuario con el email: " + request.getEmail());
        }

        List<String> roles = request.getRoles() != null && !request.getRoles().isEmpty()
                ? new ArrayList<>(request.getRoles())
                : new ArrayList<>(List.of("COMPRADOR"));

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .roles(roles)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);
        sincronizarClienteSiEsComprador(usuario);

        String token = jwtUtil.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(usuario.getRoles())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UsuarioEntity usuario = usuarioRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ReglaDominioException("Usuario no encontrado"));
        sincronizarClienteSiEsComprador(usuario);

        String token = jwtUtil.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(usuario.getRoles())
                .build();
    }

    private void sincronizarClienteSiEsComprador(UsuarioEntity usuario) {
        if (usuario.getRoles() == null || !usuario.getRoles().contains("COMPRADOR")) {
            return;
        }
        if (clienteRepositoryPort.existePorEmail(usuario.getEmail())) {
            return;
        }

        Cliente cliente = Cliente.registrar(
                UUID.randomUUID().toString(),
                usuario.getNombre(),
                new Email(usuario.getEmail()),
                "3000000000",
                Direccion.de("Calle 1 #2-3", "Armenia", "Quindío", "630001", "Colombia", 4.5350, -75.6800)
        );
        cliente.asignarVivero("vivero-001");
        clienteRepositoryPort.guardar(cliente);
    }
}