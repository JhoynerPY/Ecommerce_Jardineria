package com.uniquindio.ecommerce.infrastructure.security.service;

import com.uniquindio.ecommerce.infrastructure.security.dto.AuthResponse;
import com.uniquindio.ecommerce.infrastructure.security.dto.LoginRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.RegisterRequest;
import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.security.jwt.JwtUtil;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("nuevo@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setNombre("Nuevo Usuario");
        registerRequest.setRoles(List.of("COMPRADOR"));

        loginRequest = new LoginRequest();
        loginRequest.setEmail("existente@test.com");
        loginRequest.setPassword("password123");

        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId("user-001");
        usuarioEntity.setEmail("existente@test.com");
        usuarioEntity.setPassword("$2a$12$encodedPassword");
        usuarioEntity.setNombre("Usuario Existente");
        usuarioEntity.setRoles(List.of("COMPRADOR"));
        usuarioEntity.setActivo(true);
    }

    @Test
    void registrar_usuarioNuevo_debeGuardarYRetornarToken() {
        // Given
        when(usuarioRepository.existsByEmailAndDeletedFalse("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encodedPassword");
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token-123");

        // When
        AuthResponse response = authService.registrar(registerRequest);

        // Then
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getEmail()).isEqualTo("nuevo@test.com");
        assertThat(response.getNombre()).isEqualTo("Nuevo Usuario");
        assertThat(response.getRoles()).contains("COMPRADOR");

        // Verificar que se guardó el usuario
        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository).save(captor.capture());
        UsuarioEntity saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("nuevo@test.com");
        assertThat(saved.getPassword()).isEqualTo("$2a$12$encodedPassword");
        assertThat(saved.getRoles()).contains("COMPRADOR");

        // Verificar que se generó el token
        verify(jwtUtil).generateToken(any());
    }

    @Test
    void registrar_emailDuplicado_debeLanzarExcepcionYNoGuardar() {
        // Given
        when(usuarioRepository.existsByEmailAndDeletedFalse("nuevo@test.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registrar(registerRequest))
                .isInstanceOf(ReglaDominioException.class)
                .hasMessageContaining("Ya existe un usuario");

        // Verificar que NO se guardó el usuario
        verify(usuarioRepository, never()).save(any());
        // Verificar que NO se encriptó la contraseña
        verify(passwordEncoder, never()).encode(any());
        // Verificar que NO se generó token
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void registrar_sinRoles_debeAsignarRolCompradorPorDefecto() {
        // Given
        registerRequest.setRoles(null);
        when(usuarioRepository.existsByEmailAndDeletedFalse("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encodedPassword");
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token-123");

        // When
        AuthResponse response = authService.registrar(registerRequest);

        // Then
        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository).save(captor.capture());
        UsuarioEntity saved = captor.getValue();
        assertThat(saved.getRoles()).contains("COMPRADOR");
    }

    @Test
    void registrar_listaRolesVacia_debeAsignarRolCompradorPorDefecto() {
        // Given
        registerRequest.setRoles(List.of());
        when(usuarioRepository.existsByEmailAndDeletedFalse("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encodedPassword");
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token-123");

        // When
        AuthResponse response = authService.registrar(registerRequest);

        // Then
        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository).save(captor.capture());
        UsuarioEntity saved = captor.getValue();
        assertThat(saved.getRoles()).contains("COMPRADOR");
    }

    @Test
    void login_credencialesValidas_debeAutenticarYRetornarToken() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmailAndDeletedFalse("existente@test.com"))
                .thenReturn(Optional.of(usuarioEntity));
        when(jwtUtil.generateToken(usuarioEntity)).thenReturn("jwt-token-456");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response.getToken()).isEqualTo("jwt-token-456");
        assertThat(response.getEmail()).isEqualTo("existente@test.com");
        assertThat(response.getNombre()).isEqualTo("Usuario Existente");
        assertThat(response.getRoles()).contains("COMPRADOR");

        // Verificar autenticación
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        // Verificar búsqueda de usuario
        verify(usuarioRepository).findByEmailAndDeletedFalse("existente@test.com");
        // Verificar generación de token
        verify(jwtUtil).generateToken(usuarioEntity);
    }

    @Test
    void login_usuarioNoEncontrado_debeLanzarExcepcion() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmailAndDeletedFalse("existente@test.com"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ReglaDominioException.class)
                .hasMessageContaining("Usuario no encontrado");

        // Verificar que NO se generó token
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_autenticacionFallida_debePropagarExcepcion() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);

        // Verificar que NO se buscó el usuario (la autenticación falló antes)
        verify(usuarioRepository, never()).findByEmailAndDeletedFalse(any());
        // Verificar que NO se generó token
        verify(jwtUtil, never()).generateToken(any());
    }
}