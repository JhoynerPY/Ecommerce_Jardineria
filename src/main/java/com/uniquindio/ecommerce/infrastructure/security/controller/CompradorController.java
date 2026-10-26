package com.uniquindio.ecommerce.infrastructure.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comprador")
@PreAuthorize("hasRole('COMPRADOR')")
public class CompradorController {

    @GetMapping("/perfil")
    public Map<String, Object> perfil(Principal principal) {
        return Map.of(
                "email", principal.getName(),
                "tipo", "COMPRADOR",
                "mensaje", "Perfil de comprador"
        );
    }

    @GetMapping("/pedidos")
    public Map<String, Object> misPedidos() {
        return Map.of(
                "pedidos", List.of(),
                "mensaje", "Lista de pedidos del comprador"
        );
    }
}