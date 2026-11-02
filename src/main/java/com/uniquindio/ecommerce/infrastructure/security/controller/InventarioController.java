package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.ActualizarEstadoSaludCommand;
import com.uniquindio.ecommerce.application.ports.input.ActualizarEstadoSaludUseCase;
import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.infrastructure.security.dto.ActualizarEstadoSaludRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.ActualizarEstadoSaludResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ActualizarEstadoSaludUseCase actualizarEstadoSaludUseCase;

    public InventarioController(ActualizarEstadoSaludUseCase actualizarEstadoSaludUseCase) {
        this.actualizarEstadoSaludUseCase = actualizarEstadoSaludUseCase;
    }

    @PutMapping("/ejemplar/{id}/estado-salud")
    @PreAuthorize("hasAuthority('VENDEDOR_VIVERO') or hasAuthority('ADMIN')") // Solo vendedores o admin
    public ResponseEntity<ActualizarEstadoSaludResponse> actualizarEstadoSalud(
            @PathVariable String id,
            @Valid @RequestBody ActualizarEstadoSaludRequest request) {

        ActualizarEstadoSaludCommand command = new ActualizarEstadoSaludCommand(id, request.getNuevoEstado());
        Ejemplar ejemplar = actualizarEstadoSaludUseCase.actualizar(command);

        ActualizarEstadoSaludResponse response = new ActualizarEstadoSaludResponse(
                ejemplar.getId(),
                ejemplar.getEstadoDeSalud().name(),
                ejemplar.estaDisponibleParaVenta(),
                "Estado de salud actualizado correctamente"
        );
        return ResponseEntity.ok(response);
    }
}
