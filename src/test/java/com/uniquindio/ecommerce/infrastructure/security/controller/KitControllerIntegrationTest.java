package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ArticuloKitJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.KitJardineriaJpaRepository;
import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.security.jwt.JwtUtil;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class KitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticuloKitJpaRepository articuloKitRepository;

    @Autowired
    private KitJardineriaJpaRepository kitJardineriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepositoryPort clienteRepositoryPort;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenComprador;

    @BeforeEach
    void setUp() {
        kitJardineriaRepository.deleteAll();
        articuloKitRepository.deleteAll();
        usuarioRepository.deleteAll();

        guardarArticulo("planta-test", "Rosa miniatura", 15000, 15.0);
        guardarArticulo("maceta-test", "Maceta terracota 16cm", 14000, 16.0);
        guardarArticulo("sustrato-test", "Sustrato orgánico", 10000, 0.0);

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("usuario-kit-test")
                .email("kit@test.com")
                .password(passwordEncoder.encode("password123"))
                .nombre("Comprador Kit")
                .roles(List.of("COMPRADOR"))
                .activo(true)
                .deleted(false)
                .build();
        usuarioRepository.save(usuario);

        clienteRepositoryPort.guardar(Cliente.registrar(
                "cliente-kit-test",
                "Comprador Kit",
                new Email("kit@test.com"),
                "3000000000",
                Direccion.de("Calle 1 #2-3", "Armenia", "Quindío", "630001", "Colombia", 4.5350, -75.6800)
        ));

        tokenComprador = jwtUtil.generateToken(
                "kit@test.com",
                List.of(new SimpleGrantedAuthority("COMPRADOR"))
        );
    }

    @Test
    void listarArticulosEsPublicoYDevuelveArticulosNoEliminados() throws Exception {
        mockMvc.perform(get("/api/kits/articulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[1].nombre").value("Rosa miniatura"));
    }

    @Test
    void crearKitAutenticadoPersisteYCalculaDescuento() throws Exception {
        String request = """
                {
                  "idPlanta": "planta-test",
                  "idMaceta": "maceta-test",
                  "idSustrato": "sustrato-test",
                  "nombreKit": "Kit de prueba"
                }
                """;

        mockMvc.perform(post("/api/kits")
                        .header("Authorization", "Bearer " + tokenComprador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idKit").isNotEmpty())
                .andExpect(jsonPath("$.nombreKit").value("Kit de prueba"))
                .andExpect(jsonPath("$.subtotal").value(39000.0))
                .andExpect(jsonPath("$.descuento").value(5850.0))
                .andExpect(jsonPath("$.precioTotal").value(33150.0));

        mockMvc.perform(get("/api/kits")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].planta.nombre").value("Rosa miniatura"));
    }

    @Test
    void crearKitSinTokenDevuelveNoAutorizado() throws Exception {
        String request = """
                {
                  "idPlanta": "planta-test",
                  "idMaceta": "maceta-test",
                  "idSustrato": "sustrato-test"
                }
                """;

        mockMvc.perform(post("/api/kits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    private void guardarArticulo(String id, String nombre, double precio, double diametro) {
        ArticuloKitEntity articulo = new ArticuloKitEntity();
        articulo.setId(id);
        articulo.setNombre(nombre);
        articulo.setPrecio(new PrecioEmbeddable(BigDecimal.valueOf(precio), "COP"));
        articulo.setDiametro(diametro);
        articulo.setDeleted(false);
        articuloKitRepository.save(articulo);
    }
}