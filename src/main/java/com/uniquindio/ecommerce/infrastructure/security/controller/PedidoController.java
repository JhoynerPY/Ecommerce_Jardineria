package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.CancelarPedidoCommand;
import com.uniquindio.ecommerce.application.dto.CrearPedidoCommand;
import com.uniquindio.ecommerce.application.ports.input.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.ports.input.CrearPedidoUseCase;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.infrastructure.security.dto.CancelarPedidoRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.CancelarPedidoResponse;
import com.uniquindio.ecommerce.infrastructure.security.dto.CrearPedidoRequest;
import com.uniquindio.ecommerce.infrastructure.security.dto.CrearPedidoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CancelarPedidoUseCase cancelarPedidoUseCase;
    private final CrearPedidoUseCase crearPedidoUseCase;

    public PedidoController(CancelarPedidoUseCase cancelarPedidoUseCase, CrearPedidoUseCase crearPedidoUseCase) {
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.crearPedidoUseCase = crearPedidoUseCase;
    }

    @PostMapping("/cancelar")
    @PreAuthorize("hasAuthority('COMPRADOR')") //SOLO UN COMPRADOR PUEDE CANCELAR
    public ResponseEntity<CancelarPedidoResponse> cancelarPedido(@Valid @RequestBody CancelarPedidoRequest request){

        //Mapea DTO de la web al Command de la app
        CancelarPedidoCommand command = new CancelarPedidoCommand(request.getIdPedido(),
                request.getMotivoCancelacion());

        //Delega caso de uso
        Pedido pedidoCancelado = cancelarPedidoUseCase.cancelar(command);

        //Mapea respuesta
        CancelarPedidoResponse response = new CancelarPedidoResponse(pedidoCancelado.getIdPedido(),
                pedidoCancelado.getEstado().name(), "Pedido cancelado exitosamente");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPRADOR')")
    public ResponseEntity<CrearPedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {

        List<DetallePedido> detallesDomain = request.getDetalles().stream()
                .map(d -> new DetallePedido(d.getIdProducto(), d.getCantidad(), d.getPrecioUnitario()))
                .toList();

        CrearPedidoCommand command = new CrearPedidoCommand(
                request.getIdCliente(),
                detallesDomain,
                new Direccion(request.getDireccion(), "Armenia", "Quindio", "630001", "Colombia", 4.5350, -75.6800),
                ExencionResponsabilidad.TipoExencion.SIN_EXENCION,
                "Terminos por defecto", "1.0", true
        );

        Pedido pedido = crearPedidoUseCase.crear(command);

        CrearPedidoResponse response = new CrearPedidoResponse(
                pedido.getIdPedido(),
                pedido.getEstado().name(),
                pedido.calcularTotal(),
                "Pedido creado exitosamente"
        );
        return ResponseEntity.created(java.net.URI.create("/api/pedidos/" + pedido.getIdPedido())).body(response);
    }
}
