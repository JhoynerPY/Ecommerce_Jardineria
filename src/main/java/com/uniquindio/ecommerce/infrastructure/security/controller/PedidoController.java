package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.CancelarPedidoCommand;
import com.uniquindio.ecommerce.application.ports.input.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.infrastructure.security.dto.CancelarPedidoRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.CancelarPedidoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CancelarPedidoUseCase cancelarPedidoUseCase;

    public PedidoController(CancelarPedidoUseCase cancelarPedidoUseCase) {
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
    }

    @PostMapping("/cancelar")
    @PreAuthorize("hasAuthority('COMPRADOR')") //SOLO UN COMPRADOR PUEDE CANCELAR
    public ResponseEntity<CancelarPedidoResponse> cancelarPedido(@Valid @RequestBody CancelarPedidoRequest request){

        //Mapea DTO de la web al Command de la app
        CancelarPedidoCommand command = new CancelarPedidoCommand(request.getIdpedido(),
                request.getMotivoCancelacion());

        //Delega caso de uso
        Pedido pedidoCancelado = cancelarPedidoUseCase.cancelar(command);

        //Mapea respuesta
        CancelarPedidoResponse response = new CancelarPedidoResponse(pedidoCancelado.getIdPedido(),
                pedidoCancelado.getEstado().name(), "Pedido cancelado exitosamente");

        return ResponseEntity.ok(response);
    }
}
