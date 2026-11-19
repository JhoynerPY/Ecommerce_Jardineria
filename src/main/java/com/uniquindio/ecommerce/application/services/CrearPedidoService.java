package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.CrearPedidoUseCase;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.application.dto.CrearPedidoCommand;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import java.util.Optional;

public class CrearPedidoService implements CrearPedidoUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ViveroRepositoryPort viveroRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;

    public CrearPedidoService(ClienteRepositoryPort clienteRepositoryPort,
                              ViveroRepositoryPort viveroRepositoryPort,
                              PedidoRepositoryPort pedidoRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.viveroRepositoryPort = viveroRepositoryPort;
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido crear(CrearPedidoCommand command) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(command.getIdCliente());
        if (clienteOpt.isEmpty()) {
            throw new ReglaDominioException("Cliente no encontrado: " + command.getIdCliente());
        }
        Cliente cliente = clienteOpt.get();

        if (!cliente.tieneViveroAsignado()) {
            throw new ReglaDominioException("El cliente no tiene vivero asignado");
        }

        Optional<Vivero> viveroOpt = viveroRepositoryPort.buscarPorId(cliente.getViveroAsignadoId());
        if (viveroOpt.isEmpty()) {
            throw new ReglaDominioException("Vivero asignado no encontrado: " + cliente.getViveroAsignadoId());
        }
        Vivero vivero = viveroOpt.get();

        if (!vivero.cubreDireccion(command.getDireccionEntrega())) {
            throw new ReglaDominioException("El vivero asignado no cubre la direccion de entrega");
        }

        ExencionResponsabilidad exencion = buildExencionResponsabilidad(command);

        Pedido pedido = Pedido.crear(
            command.getIdCliente(),
            vivero.getId(),
            command.getDetalles(),
            command.getDireccionEntrega().toString(),
            exencion
        );

        return pedidoRepositoryPort.guardar(pedido);
    }

    private ExencionResponsabilidad buildExencionResponsabilidad(CrearPedidoCommand command) {
        return switch (command.getTipoExencion()) {
            case EXENCION_TOTAL -> ExencionResponsabilidad.exencionTotal(
                command.getTerminosExencion(), command.getVersionExencion(), command.isExencionAceptada());
            case ACUERDO_PARCIAL_PERDIDA -> ExencionResponsabilidad.acuerdoParcialPerdida(
                command.getTerminosExencion(), command.getVersionExencion(), command.isExencionAceptada());
            case SIN_EXENCION -> ExencionResponsabilidad.sinExencion();
        };
    }
}