package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.ProcesarCompraCommand;
import com.uniquindio.ecommerce.application.ports.input.ProcesarCompraUseCase;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.infrastructure.security.dto.ProcesarCompraRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final ProcesarCompraUseCase procesarCompraUseCase;

    public PagoController(ProcesarCompraUseCase procesarCompraUseCase) {
        this.procesarCompraUseCase = procesarCompraUseCase;
    }

    @PostMapping("/procesar")
    @PreAuthorize("hasAuthority('COMPRADOR')")
    public ResponseEntity<Map<String, String>> procesarPago(@Valid @RequestBody ProcesarCompraRequest request) throws Exception {

        ProcesarCompraCommand command = new ProcesarCompraCommand(request.getIdPedido(), request.getMetodoPago());
        Pedido pedido = procesarCompraUseCase.procesar(command);

        return ResponseEntity.ok(Map.of(
                "idPedido", pedido.getIdPedido(),
                "estado", pedido.getEstado().name(),
                "mensaje", "Pago procesado exitosamente"
        ));
    }
}
