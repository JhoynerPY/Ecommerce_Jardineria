package com.uniquindio.ecommerce.infrastructure.security.jwt;

import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Usar reflection para setear valores privados
        setField(jwtUtil, "secret", "testSecretKey1234567890123456789012345678901234567890");
        setField(jwtUtil, "expiration", 86400000L); // 24 horas
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateToken_y_extractUsername_debenFuncionarCorrectamente() {
        // Given
        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );

        // When
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);

        // Then
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(username).isEqualTo("test@test.com");
    }

    @Test
    void generateToken_conRoles_debeIncluirRolesEnClaims() {
        // Given
        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(
                        new SimpleGrantedAuthority("COMPRADOR"),
                        new SimpleGrantedAuthority("VENDEDOR_VIVERO")
                )
        );

        // When
        String token = jwtUtil.generateToken(userDetails);
        List<String> roles = jwtUtil.extractRoles(token);

        // Then
        assertThat(roles).containsExactlyInAnyOrder("COMPRADOR", "VENDEDOR_VIVERO");
    }

    @Test
    void validateToken_conTokenValido_debeRetornarTrue() {
        // Given
        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_conTokenInvalido_debeRetornarFalse() {
        // Given
        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );
        String tokenValido = jwtUtil.generateToken(userDetails);
        String tokenInvalido = tokenValido + "invalido";

        // When
        boolean isValid = jwtUtil.validateToken(tokenInvalido);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_conTokenExpirado_debeRetornarFalse() {
        // Given - crear JwtUtil con expiración muy corta
        JwtUtil jwtUtilExpirado = new JwtUtil();
        setField(jwtUtilExpirado, "secret", "testSecretKey1234567890123456789012345678901234567890");
        setField(jwtUtilExpirado, "expiration", -1000L); // Ya expirado

        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );
        String tokenExpirado = jwtUtilExpirado.generateToken(userDetails);

        // When
        boolean isValid = jwtUtilExpirado.validateToken(tokenExpirado);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void extractExpiration_debeRetornarFechaFutura() {
        // Given
        User userDetails = new User(
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );
        String token = jwtUtil.generateToken(userDetails);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void extractRoles_conTokenSinRoles_debeRetornarListaVacia() {
        // Given - token generado manualmente sin claim roles
        String tokenSinRoles = jwtUtil.generateToken("test@test.com", List.of());

        // When
        List<String> roles = jwtUtil.extractRoles(tokenSinRoles);

        // Then
        assertThat(roles).isEmpty();
    }
}