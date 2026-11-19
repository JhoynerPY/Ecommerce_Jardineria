package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comprador")
@PreAuthorize("hasAuthority('COMPRADOR')")
public class CompradorController {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final KitJardineriaRepository kitJardineriaRepository;

    public CompradorController(ClienteRepositoryPort clienteRepositoryPort,
                               PedidoRepositoryPort pedidoRepositoryPort,
                               KitJardineriaRepository kitJardineriaRepository) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.kitJardineriaRepository = kitJardineriaRepository;
    }

    @GetMapping("/perfil")
    public Map<String, Object> perfil(Principal principal) {
        Cliente cliente = clienteRepositoryPort.buscarPorEmail(principal.getName()).orElse(null);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("nombre", cliente != null ? cliente.getNombre() : principal.getName());
        response.put("email",
                cliente != null ? cliente.getEmail().valor() : principal.getName());
        response.put("rol", "COMPRADOR");
        response.put("tipo", "COMPRADOR");
        response.put("direccion",
                cliente != null && cliente.getDireccion() != null
                        ? cliente.getDireccion().calle() + ", " + cliente.getDireccion().ciudad()
                        : "Sin dirección registrada");
        response.put("totalPedidos",
                cliente != null ? pedidoRepositoryPort.buscarPorClienteId(cliente.getId()).size() : 0);
        response.put("totalKits",
                cliente != null ? kitJardineriaRepository.buscarPorClienteId(cliente.getId()).size() : 0);
        return response;
    }

    @GetMapping("/pedidos")
    public Map<String, Object> misPedidos() {
        return Map.of(
                "pedidos", List.of(),
                "mensaje", "Lista de pedidos del comprador"
        );
    }
}