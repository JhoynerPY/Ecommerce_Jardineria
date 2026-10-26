package com.uniquindio.ecommerce.infrastructure.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendedor")
@PreAuthorize("hasRole('VENDEDOR_VIVERO')")
public class VendedorController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Principal principal) {
        return Map.of(
                "email", principal.getName(),
                "tipo", "VENDEDOR_VIVERO",
                "mensaje", "Dashboard de vivero"
        );
    }

    @GetMapping("/pedidos")
    public Map<String, Object> pedidosVivero() {
        return Map.of(
                "pedidos", List.of(),
                "mensaje", "Pedidos asignados al vivero"
        );
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticas() {
        return Map.of(
                "ventasTotales", 0,
                "pedidosPendientes", 0,
                "mensaje", "Estadísticas del vivero"
        );
    }
}