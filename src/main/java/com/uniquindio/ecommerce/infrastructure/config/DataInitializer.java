package com.uniquindio.ecommerce.infrastructure.config;

import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.ArticuloKitSeleccionado;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ArticuloKitJpaRepository;
import com.uniquindio.ecommerce.infrastructure.security.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.security.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ArticuloKitJpaRepository articuloKitRepository;
    private final ViveroRepositoryPort viveroRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final CatalogoArticulosPort catalogoArticulosPort;
    private final KitJardineriaRepository kitJardineriaRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ClienteRepositoryPort clienteRepositoryPort,
                           ArticuloKitJpaRepository articuloKitRepository,
                           ViveroRepositoryPort viveroRepositoryPort,
                           PasswordEncoder passwordEncoder,
                           CatalogoArticulosPort catalogoArticulosPort,
                           KitJardineriaRepository kitJardineriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.articuloKitRepository = articuloKitRepository;
        this.viveroRepositoryPort = viveroRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.catalogoArticulosPort = catalogoArticulosPort;
        this.kitJardineriaRepository = kitJardineriaRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        crearUsuarioSiNoExiste("admin@raizlocal.com", "Admin123*",
                "Administrador Raíz Local", List.of("ADMIN"));
        crearUsuarioSiNoExiste("vendedor@raizlocal.com", "Vendedor123*",
                "Vendedor Raíz Local", List.of("VENDEDOR_VIVERO"));
        crearUsuarioSiNoExiste("comprador@raizlocal.com", "Comprador123*",
                "Comprador Raíz Local", List.of("COMPRADOR"));

        crearUsuarioSiNoExiste("comprador@test.com", "password123",
                "Comprador Test", List.of("COMPRADOR"));
        crearUsuarioSiNoExiste("vendedor@test.com", "password123",
                "Vendedor Test", List.of("VENDEDOR_VIVERO"));
        crearUsuarioSiNoExiste("admin@test.com", "password123",
                "Admin Test", List.of("ADMIN", "COMPRADOR", "VENDEDOR_VIVERO"));
        crearUsuarioSiNoExiste("maria.gonzalez@email.com", "password123",
                "María González", List.of("COMPRADOR"));
        crearUsuarioSiNoExiste("contacto@eljardin.com", "password123",
                "Vivero El Jardín", List.of("VENDEDOR_VIVERO"));

        asegurarCredencialesDePrueba();
        sincronizarClientesCompradores();
        asegurarArticulosKitBase();
        crearKitsPreconfiguradosSiNoExisten();
    }

    private void crearUsuarioSiNoExiste(String email, String password,
                                        String nombre, List<String> roles) {
        if (usuarioRepository.existsByEmailAndDeletedFalse(email)) {
            return;
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .password(passwordEncoder.encode(password))
                .nombre(nombre)
                .roles(roles)
                .activo(true)
                .deleted(false)
                .build();
        usuarioRepository.save(usuario);
    }

    private void asegurarCredencialesDePrueba() {
        asegurarPasswordSiNoCoincide("admin@raizlocal.com", "Admin123*");
        asegurarPasswordSiNoCoincide("vendedor@raizlocal.com", "Vendedor123*");
        asegurarPasswordSiNoCoincide("comprador@raizlocal.com", "Comprador123*");

        asegurarPasswordSiNoCoincide("comprador@test.com", "password123");
        asegurarPasswordSiNoCoincide("vendedor@test.com", "password123");
        asegurarPasswordSiNoCoincide("admin@test.com", "password123");
        asegurarPasswordSiNoCoincide("maria.gonzalez@email.com", "password123");
        asegurarPasswordSiNoCoincide("contacto@eljardin.com", "password123");
    }

    private void asegurarPasswordSiNoCoincide(String email, String password) {
        usuarioRepository.findByEmailAndDeletedFalse(email).ifPresent(usuario -> {
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(password));
                usuarioRepository.save(usuario);
            }
        });
    }

    private void sincronizarClientesCompradores() {
        for (UsuarioEntity usuario : usuarioRepository.findAll()) {
            if (Boolean.TRUE.equals(usuario.getDeleted())
                    || usuario.getRoles() == null
                    || !usuario.getRoles().contains("COMPRADOR")) {
                continue;
            }
            if (clienteRepositoryPort.existePorEmail(usuario.getEmail())) {
                continue;
            }

            Cliente cliente = Cliente.registrar(
                    UUID.randomUUID().toString(),
                    usuario.getNombre(),
                    new Email(usuario.getEmail()),
                    "3000000000",
                    Direccion.de("Calle 1 #2-3", "Armenia", "Quindío", "630001",
                            "Colombia", 4.5350, -75.6800)
            );
            if (viveroRepositoryPort.buscarPorId("vivero-001").isPresent()) {
                cliente.asignarVivero("vivero-001");
            }
            clienteRepositoryPort.guardar(cliente);
        }
    }

    private void asegurarArticulosKitBase() {
        guardarArticuloSiNoExiste("seed-abono-001", "Abono orgánico humus de lombriz", 12000, 0.0);
        guardarArticuloSiNoExiste("seed-sustrato-001", "Sustrato universal para interiores", 13000, 0.0);
        guardarArticuloSiNoExiste("seed-maceta-001", "Maceta de terracota artesanal", 24000, 18.0);
        guardarArticuloSiNoExiste("seed-regadera-001", "Regadera de cuello largo", 35000, 0.0);
        guardarArticuloSiNoExiste("seed-tijeras-001", "Tijeras de poda botánica", 28000, 0.0);
        guardarArticuloSiNoExiste("seed-perlita-001", "Perlita agrícola 5L", 8000, 0.0);
        guardarArticuloSiNoExiste("seed-palas-001", "Kit de palas manuales", 32000, 0.0);
        guardarArticuloSiNoExiste("seed-fertilizante-001", "Fertilizante líquido foliar", 18000, 0.0);
    }

    private void crearKitsPreconfiguradosSiNoExisten() {
        crearKitPreconfiguradoSiNoExiste(
                "kit-principiante-interior",
                "Kit Principiante Interior",
                "seed-sustrato-001", "seed-maceta-001", "seed-abono-001");
        crearKitPreconfiguradoSiNoExiste(
                "kit-cuidado-suculentas",
                "Kit Cuidado Suculentas",
                "seed-sustrato-001", "seed-maceta-001", "seed-perlita-001");
        crearKitPreconfiguradoSiNoExiste(
                "kit-jardin-urbano",
                "Kit Jardín Urbano",
                "seed-fertilizante-001", "seed-regadera-001", "seed-palas-001");
    }

    private void crearKitPreconfiguradoSiNoExiste(String idKit, String nombreKit,
                                                  String idUno, String idDos, String idTres) {
        if (kitJardineriaRepository.obtenerKitPorIdentificador(idKit).isPresent()) {
            return;
        }

        ArticuloKit uno = catalogoArticulosPort.obtenerArticulo(idUno);
        ArticuloKit dos = catalogoArticulosPort.obtenerArticulo(idDos);
        ArticuloKit tres = catalogoArticulosPort.obtenerArticulo(idTres);
        if (uno == null || dos == null || tres == null) {
            return;
        }

        KitJardineria kit = KitJardineria.restaurar(
                idKit,
                List.of(
                        new ArticuloKitSeleccionado(uno, 1),
                        new ArticuloKitSeleccionado(dos, 1),
                        new ArticuloKitSeleccionado(tres, 1)
                ),
                nombreKit,
                null
        );
        kitJardineriaRepository.almacenarKit(kit);
    }

    private void guardarArticuloSiNoExiste(String id, String nombre, double precio, double diametro) {
        if (articuloKitRepository.existsById(id)) {
            return;
        }
        guardarArticulo(id, nombre, precio, diametro);
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