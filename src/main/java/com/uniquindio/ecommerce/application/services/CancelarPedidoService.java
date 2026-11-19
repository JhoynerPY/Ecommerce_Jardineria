package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.CancelarPedidoCommand;
import com.uniquindio.ecommerce.application.ports.input.CancelarPedidoUseCase;
import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.pedido.EstadoPedido;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

public class CancelarPedidoService implements CancelarPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final InventarioPort inventarioPort;

    public CancelarPedidoService(PedidoRepositoryPort pedidoRepositoryPort, InventarioPort inventarioPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.inventarioPort = inventarioPort;
    }

    @Override
    public Pedido cancelar(CancelarPedidoCommand command) {

        //Buscar el pedido
        Pedido pedido = pedidoRepositoryPort.buscarPorId(command.getIdPedido())
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado: " + command.getIdPedido()));

        //Guardar si estaba pagado antes de cancelar (saber si devolver a stock)
        boolean estadoPagado = pedido.getEstado() == EstadoPedido.PAGADO;

        //Ejecuta regla de dominio
        pedido.cancelar();

        //De estar pagado, devuelve articulo a inventario
        if (estadoPagado){
            boolean repuesto = inventarioPort.reponerInventario(pedido.getDetalles());
            if (!repuesto){
                throw new ReglaDominioException("Error al devolver los articulos al inventario.");
            }
        }

        //Guarda el pedido actualizado
        return pedidoRepositoryPort.guardar(pedido);
    }
}
