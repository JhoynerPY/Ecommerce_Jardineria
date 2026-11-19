package com.uniquindio.ecommerce.infrastructure.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @GetMapping("/usuarios")
    public Map<String, Object> listarUsuarios() {
        return Map.of(
                "usuarios", List.of(),
                "total", 0,
                "mensaje", "Lista de todos los usuarios"
        );
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticasGlobales() {
        return Map.of(
                "totalUsuarios", 0,
                "totalViveros", 0,
                "totalPedidos", 0,
                "mensaje", "Estadísticas globales del sistema"
        );
    }
}