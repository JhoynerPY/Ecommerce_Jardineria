package com.uniquindio.ecommerce.infrastructure.security.controller;

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
                .map(this::mapearArticulo)
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

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KitResponse> crearKit(@Valid @RequestBody ConfigurarKitRequest request,
                                                Principal principal) throws Exception {
        String idCliente = clienteRepositoryPort.buscarPorEmail(principal.getName())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe un cliente asociado al usuario autenticado: " + principal.getName()))
                .getId();

        KitJardineria kit = configurarKitUseCase.armarKit(
                request.getIdPlanta(),
                request.getIdMaceta(),
                request.getIdSustrato(),
                idCliente
        );

        return ResponseEntity.created(URI.create("/api/kits/" + kit.getIdKit()))
                .body(mapearKit(kit));
    }

    private ArticuloKitResponse mapearArticulo(ArticuloKit articulo) {
        return new ArticuloKitResponse(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getPrecio().getValor(),
                articulo.getPrecio().getMoneda(),
                articulo.getDiametro()
        );
    }

    private KitResponse mapearKit(KitJardineria kit) {
        double subtotal = kit.getPlanta().getPrecio().getValor()
                + kit.getMaceta().getPrecio().getValor()
                + kit.getSustrato().getPrecio().getValor();
        double total = kit.calcularPrecioTotal();

        return new KitResponse(
                kit.getIdKit(),
                mapearArticulo(kit.getPlanta()),
                mapearArticulo(kit.getMaceta()),
                mapearArticulo(kit.getSustrato()),
                subtotal,
                subtotal - total,
                total
        );
    }
}