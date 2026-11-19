package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.ArticuloKitSeleccionadoCommand;
import com.uniquindio.ecommerce.application.ports.input.ConfigurarKitUseCase;
import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.infrastructure.security.dto.ArticuloKitResponse;
import com.uniquindio.ecommerce.infrastructure.security.dto.ConfigurarKitRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.KitResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/kits")
public class KitController {

    private final ConfigurarKitUseCase configurarKitUseCase;
    private final CatalogoArticulosPort catalogoArticulosPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    public KitController(ConfigurarKitUseCase configurarKitUseCase,
                         CatalogoArticulosPort catalogoArticulosPort,
                         ClienteRepositoryPort clienteRepositoryPort) {
        this.configurarKitUseCase = configurarKitUseCase;
        this.catalogoArticulosPort = catalogoArticulosPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @GetMapping("/articulos")
    public ResponseEntity<List<ArticuloKitResponse>> listarArticulos() {
        List<ArticuloKitResponse> articulos = catalogoArticulosPort.listarArticulos().stream()
                .map(articulo -> mapearArticulo(articulo, 1))
                .toList();
        return ResponseEntity.ok(articulos);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KitResponse>> listarKits() {
        List<KitResponse> kits = configurarKitUseCase.listarKits().stream()
                .map(this::mapearKit)
                .toList();
        return ResponseEntity.ok(kits);
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAuthority('COMPRADOR')")
    public ResponseEntity<List<KitResponse>> misKits(Principal principal) {
        String idCliente = clienteRepositoryPort.buscarPorEmail(principal.getName())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe un cliente asociado al usuario autenticado: " + principal.getName()))
                .getId();

        List<KitResponse> kits = configurarKitUseCase.listarKitsPorCliente(idCliente).stream()
                .map(this::mapearKit)
                .toList();
        return ResponseEntity.ok(kits);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KitResponse> crearKit(@Valid @RequestBody ConfigurarKitRequest request,
                                                Principal principal) throws Exception {
        String idCliente = clienteRepositoryPort.buscarPorEmail(principal.getName())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe un cliente asociado al usuario autenticado: " + principal.getName()))
                .getId();

        List<ArticuloKitSeleccionadoCommand> articulos = request.getArticulos() != null
                && !request.getArticulos().isEmpty()
                ? request.getArticulos().stream()
                        .map(articulo -> new ArticuloKitSeleccionadoCommand(
                                articulo.getArticuloId(), articulo.getCantidad()))
                        .toList()
                : List.of(
                        new ArticuloKitSeleccionadoCommand(request.getIdPlanta(), 1),
                        new ArticuloKitSeleccionadoCommand(request.getIdMaceta(), 1),
                        new ArticuloKitSeleccionadoCommand(request.getIdSustrato(), 1)
                );

        KitJardineria kit = configurarKitUseCase.armarKit(
                articulos,
                idCliente,
                request.getNombreKit()
        );

        return ResponseEntity.created(URI.create("/api/kits/" + kit.getIdKit()))
                .body(mapearKit(kit));
    }

    private ArticuloKitResponse mapearArticulo(ArticuloKit articulo, int cantidad) {
        return new ArticuloKitResponse(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getPrecio().getValor(),
                articulo.getPrecio().getMoneda(),
                articulo.getDiametro(),
                cantidad
        );
    }

    private KitResponse mapearKit(KitJardineria kit) {
        double subtotal = kit.calcularSubtotal();
        double total = kit.calcularPrecioTotal();
        List<ArticuloKitResponse> articulos = kit.getArticulos().stream()
                .map(seleccionado -> mapearArticulo(
                        seleccionado.articulo(), seleccionado.cantidad()))
                .toList();

        return new KitResponse(
                kit.getIdKit(),
                kit.getNombreKit(),
                kit.getIdCliente(),
                articulos,
                subtotal,
                subtotal - total,
                total
        );
    }
}