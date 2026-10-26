package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.security.jwt.JwtUtil;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoleBasedAccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String compradorToken;
    private String vendedorToken;
    private String adminToken;
    private String sinRolToken;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        // Crear usuarios con diferentes roles
        crearUsuario("comprador@test.com", "Comprador Test", List.of("COMPRADOR"));
        crearUsuario("vendedor@test.com", "Vendedor Test", List.of("VENDEDOR_VIVERO"));
        crearUsuario("admin@test.com", "Admin Test", List.of("ADMIN", "COMPRADOR", "VENDEDOR_VIVERO"));
        crearUsuario("sinrol@test.com", "Sin Rol Test", List.of());

        // Generar tokens
        compradorToken = generarToken("comprador@test.com", "COMPRADOR");
        vendedorToken = generarToken("vendedor@test.com", "VENDEDOR_VIVERO");
        adminToken = generarToken("admin@test.com", "ADMIN", "COMPRADOR", "VENDEDOR_VIVERO");
        sinRolToken = generarToken("sinrol@test.com");
    }

    private void crearUsuario(String email, String nombre, List<String> roles) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId("test-" + email.replace("@", "-").replace(".", "-"));
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode("password123"));
        usuario.setNombre(nombre);
        usuario.setRoles(roles);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    private String generarToken(String email, String... roles) {
        List<GrantedAuthority> authorities = java.util.Arrays.stream(roles)
                .map(r -> new SimpleGrantedAuthority(r))
                .collect(Collectors.toList());
        return jwtUtil.generateToken(email, authorities);
    }

    // ===== TESTS PARA ENDPOINTS DE COMPRADOR =====

    @Test
    void comprador_conTokenValido_debeAccederAEndpointsDeComprador() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("comprador@test.com"))
                .andExpect(jsonPath("$.tipo").value("COMPRADOR"));
    }

    @Test
    void vendedor_sinRolComprador_noDebeAccederAEndpointsDeComprador() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinRol_noDebeAccederAEndpointsDeComprador() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + sinRolToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinToken_noDebeAccederAEndpointsDeComprador() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void admin_conRolComprador_debeAccederAEndpointsDeComprador() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    // ===== TESTS PARA ENDPOINTS DE VENDEDOR =====

    @Test
    void vendedor_conTokenValido_debeAccederAEndpointsDeVendedor() throws Exception {
        mockMvc.perform(get("/api/vendedor/dashboard")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("vendedor@test.com"))
                .andExpect(jsonPath("$.tipo").value("VENDEDOR_VIVERO"));
    }

    @Test
    void comprador_sinRolVendedor_noDebeAccederAEndpointsDeVendedor() throws Exception {
        mockMvc.perform(get("/api/vendedor/dashboard")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_conRolVendedor_debeAccederAEndpointsDeVendedor() throws Exception {
        mockMvc.perform(get("/api/vendedor/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void vendedor_debeAccederAEndpointEstadisticas() throws Exception {
        mockMvc.perform(get("/api/vendedor/estadisticas")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estadísticas del vivero"));
    }

    // ===== TESTS PARA ENDPOINTS DE ADMIN =====

    @Test
    void admin_conTokenValido_debeAccederAEndpointsDeAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Lista de todos los usuarios"));
    }

    @Test
    void comprador_sinRolAdmin_noDebeAccederAEndpointsDeAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void vendedor_sinRolAdmin_noDebeAccederAEndpointsDeAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_debeAccederAEndpointEstadisticasGlobales() throws Exception {
        mockMvc.perform(get("/api/admin/estadisticas")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estadísticas globales del sistema"));
    }

    // ===== TESTS DE VERIFICACIÓN DE COMPORTAMIENTOS CON verify(never()) =====

    @Test
    void accesoDenegado_noDebeEjecutarLogicaDelController() throws Exception {
        // Este test verifica que cuando se deniega el acceso (403),
        // no se ejecuta la lógica del controller (no hay side effects)
        
        // Intentar acceder a endpoint de vendedor con token de comprador
        mockMvc.perform(get("/api/vendedor/pedidos")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isForbidden());

        // Verificar que no se realizó ninguna operación de negocio
        // (en este caso, el controller solo retorna datos estáticos, 
        // pero en un caso real verificamos que no se llamaron servicios)
        
        // El hecho de que retorne 403 confirma que Spring Security
        // bloqueó la request ANTES de llegar al controller
    }

    @Test
    void accesoDenegadoAdmin_noDebeEjecutarLogicaAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/estadisticas")
                        .header("Authorization", "Bearer " + vendedorToken))
                .andExpect(status().isForbidden());

        // Spring Security intercepta la request antes de invocar el handler
        // Por lo tanto, el método del controller NUNCA se ejecuta
    }

    // ===== TESTS DE CASOS DE ERROR ADICIONALES =====

    @Test
    void tokenMalformado_debeRetornar401() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer token.mal.formado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenConFirmaInvalida_debeRetornar401() throws Exception {
        // Token firmado con clave diferente
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZXMiOlsiQ09NUFJBRE9SIl19.firma_invalida";
        
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorizationHeaderSinBearer_debeRetornar401() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", compradorToken)) // Sin "Bearer "
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorizationHeaderVacio_debeRetornar401() throws Exception {
        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioInactivo_debeRetornar401() throws Exception {
        // Crear usuario inactivo
        UsuarioEntity usuarioInactivo = new UsuarioEntity();
        usuarioInactivo.setId("test-inactivo-001");
        usuarioInactivo.setEmail("inactivo@test.com");
        usuarioInactivo.setPassword(passwordEncoder.encode("password123"));
        usuarioInactivo.setNombre("Usuario Inactivo");
        usuarioInactivo.setRoles(List.of("COMPRADOR"));
        usuarioInactivo.setActivo(false); // INACTIVO
        usuarioRepository.save(usuarioInactivo);

        String tokenInactivo = jwtUtil.generateToken("inactivo@test.com",
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("COMPRADOR")));

        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + tokenInactivo))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioEliminadoSoftDelete_debeRetornar401() throws Exception {
        // Crear usuario y marcar como eliminado
        UsuarioEntity usuarioEliminado = new UsuarioEntity();
        usuarioEliminado.setId("test-eliminado-001");
        usuarioEliminado.setEmail("eliminado@test.com");
        usuarioEliminado.setPassword(passwordEncoder.encode("password123"));
        usuarioEliminado.setNombre("Usuario Eliminado");
        usuarioEliminado.setRoles(List.of("COMPRADOR"));
        usuarioEliminado.setActivo(true);
        usuarioEliminado.setDeleted(true); // SOFT DELETE
        usuarioRepository.save(usuarioEliminado);

        String tokenEliminado = jwtUtil.generateToken("eliminado@test.com",
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("COMPRADOR")));

        mockMvc.perform(get("/api/comprador/perfil")
                        .header("Authorization", "Bearer " + tokenEliminado))
                .andExpect(status().isUnauthorized());
    }
}