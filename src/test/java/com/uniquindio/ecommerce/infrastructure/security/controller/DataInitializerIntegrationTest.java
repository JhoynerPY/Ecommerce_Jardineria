package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ArticuloKitJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ClienteJpaRepository;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DataInitializerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private ArticuloKitJpaRepository articuloKitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void inicializadorCreaUsuariosClientesYArticulosKit() {
        assertTrue(usuarioRepository.existsByEmailAndDeletedFalse("admin@raizlocal.com"));
        assertTrue(usuarioRepository.existsByEmailAndDeletedFalse("vendedor@raizlocal.com"));
        assertTrue(usuarioRepository.existsByEmailAndDeletedFalse("comprador@raizlocal.com"));

        assertTrue(clienteJpaRepository.findByEmail_ValorAndDeletedFalse("comprador@raizlocal.com").isPresent());
        assertTrue(articuloKitRepository.findByDeletedFalseOrderByIdAsc().size() >= 8);
    }

    @Test
    void loginDeLosTresPerfilesFuncionaConLasCredencialesDelInicializador() throws Exception {
        comprobarLogin("admin@raizlocal.com", "Admin123*");
        comprobarLogin("vendedor@raizlocal.com", "Vendedor123*");
        comprobarLogin("comprador@raizlocal.com", "Comprador123*");
    }

    @Test
    void registroDeCompradorCreaUsuarioYClienteAsociado() throws Exception {
        String email = "nuevo-comprador@test.com";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Clave123*",
                                  "nombre": "Comprador Nuevo",
                                  "roles": ["COMPRADOR"]
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        assertTrue(usuarioRepository.existsByEmailAndDeletedFalse(email));
        assertTrue(clienteJpaRepository.findByEmail_ValorAndDeletedFalse(email).isPresent());
    }

    @Test
    void compradorAutenticadoPuedeCrearKitConMultiplesArticulos() throws Exception {
        String token = login("comprador@raizlocal.com", "Comprador123*");

        List<ArticuloKitEntity> articulos = articuloKitRepository.findByDeletedFalseOrderByIdAsc();
        assertTrue(articulos.size() >= 3);

        String request = """
                {
                  "nombreKit": "Kit de integración",
                  "articulos": [
                    { "articuloId": "%s", "cantidad": 2 },
                    { "articuloId": "%s", "cantidad": 1 },
                    { "articuloId": "%s", "cantidad": 3 }
                  ]
                }
                """.formatted(
                articulos.get(0).getId(),
                articulos.get(1).getId(),
                articulos.get(2).getId()
        );

        mockMvc.perform(post("/api/kits")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idKit").isNotEmpty())
                .andExpect(jsonPath("$.articulos.length()").value(3))
                .andExpect(jsonPath("$.articulos[0].cantidad").value(2))
                .andExpect(jsonPath("$.subtotal").isNumber())
                .andExpect(jsonPath("$.precioTotal").isNumber());
    }

    @Test
    void loginDeLosUsuariosDePruebaClasicosFunciona() throws Exception {
        comprobarLogin("comprador@test.com", "password123");
        comprobarLogin("vendedor@test.com", "password123");
        comprobarLogin("admin@test.com", "password123");
        comprobarLogin("maria.gonzalez@email.com", "password123");
        comprobarLogin("contacto@eljardin.com", "password123");
    }

    @Test
    void kitsPreconfiguradosEstanDisponibles() throws Exception {
        String token = login("comprador@raizlocal.com", "Comprador123*");

        mockMvc.perform(get("/api/kits")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[?(@.nombreKit == 'Kit Principiante Interior')]").isNotEmpty());
    }

    @Test
    void misPedidosDevuelveListaParaCompradorAutenticado() throws Exception {
        String token = login("comprador@raizlocal.com", "Comprador123*");

        mockMvc.perform(get("/api/pedidos/mios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private void comprobarLogin(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email));
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }
}