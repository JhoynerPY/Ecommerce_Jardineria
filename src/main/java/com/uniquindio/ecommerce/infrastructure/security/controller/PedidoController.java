package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.application.dto.CancelarPedidoCommand;
import com.uniquindio.ecommerce.application.dto.CrearPedidoCommand;
import com.uniquindio.ecommerce.application.ports.input.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.ports.input.CrearPedidoUseCase;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CancelarPedidoUseCase cancelarPedidoUseCase;
    private final CrearPedidoUseCase crearPedidoUseCase;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ViveroRepositoryPort viveroRepositoryPort;

    public PedidoController(CancelarPedidoUseCase cancelarPedidoUseCase,
                            CrearPedidoUseCase crearPedidoUseCase,
                            ClienteRepositoryPort clienteRepositoryPort,
                            ViveroRepositoryPort viveroRepositoryPort) {
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.viveroRepositoryPort = viveroRepositoryPort;
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
    public ResponseEntity<CrearPedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request,
                                                           Principal principal) {

        List<DetallePedido> detallesDomain = request.getDetalles().stream()
                .map(d -> new DetallePedido(d.getIdProducto(), d.getCantidad(), d.getPrecioUnitario()))
                .toList();

        String idCliente = resolverIdCliente(request.getIdCliente(), principal);
        ExencionResponsabilidad.TipoExencion tipoExencion = request.getTipoExencion() != null
                ? request.getTipoExencion()
                : ExencionResponsabilidad.TipoExencion.SIN_EXENCION;

        CrearPedidoCommand command = new CrearPedidoCommand(
                idCliente,
                detallesDomain,
                new Direccion(request.getDireccion(), "Armenia", "Quindio", "630001", "Colombia", 4.5350, -75.6800),
                tipoExencion,
                resolverTerminosExencion(request, tipoExencion),
                request.getVersionExencion() != null && !request.getVersionExencion().isBlank()
                        ? request.getVersionExencion()
                        : "1.0",
                tipoExencion == ExencionResponsabilidad.TipoExencion.SIN_EXENCION || request.isExencionAceptada()
        );

        Pedido pedido = crearPedidoUseCase.crear(command);

        Optional<Vivero> vivero = viveroRepositoryPort.buscarPorId(pedido.getIdVivero());
        String nombreVivero = vivero.map(Vivero::getNombre).orElse(null);

        CrearPedidoResponse response = new CrearPedidoResponse(
                pedido.getIdPedido(),
                pedido.getEstado().name(),
                pedido.calcularTotal(),
                "Pedido creado exitosamente",
                pedido.getIdVivero(),
                nombreVivero
        );
        return ResponseEntity.created(java.net.URI.create("/api/pedidos/" + pedido.getIdPedido())).body(response);
    }

    private String resolverIdCliente(String idClienteSolicitado, Principal principal) {
        if (idClienteSolicitado != null && !idClienteSolicitado.isBlank()) {
            return idClienteSolicitado;
        }

        String emailAutenticado = principal != null ? principal.getName() : null;
        if (emailAutenticado == null || emailAutenticado.isBlank()) {
            throw new ReglaDominioException("No se pudo identificar al cliente autenticado");
        }

        return clienteRepositoryPort.buscarPorEmail(emailAutenticado)
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe un cliente asociado al usuario autenticado: " + emailAutenticado))
                .getId();
    }

    private String resolverTerminosExencion(CrearPedidoRequest request,
                                            ExencionResponsabilidad.TipoExencion tipoExencion) {
        if (request.getTerminosExencion() != null && !request.getTerminosExencion().isBlank()) {
            return request.getTerminosExencion();
        }

        return switch (tipoExencion) {
            case EXENCION_TOTAL -> "El cliente exime al vivero de toda responsabilidad por daños en transporte";
            case ACUERDO_PARCIAL_PERDIDA -> "Acuerdo parcial del "
                    + request.getPorcentajeExencion() + "% por daños en transporte";
            case SIN_EXENCION -> "Sin exención de responsabilidad aplicada";
        };
    }
}
