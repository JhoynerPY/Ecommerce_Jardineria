package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.ProcesarCompraUseCase;
import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.application.ports.output.PasarelaPagoPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.application.dto.ProcesarCompraCommand;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import java.util.Optional;

public class ProcesarCompraService implements ProcesarCompraUseCase {

    private final PasarelaPagoPort pasarelaPagoPort;
    private final InventarioPort inventarioPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;

    public ProcesarCompraService(PasarelaPagoPort pasarelaPagoPort,
                                 InventarioPort inventarioPort,
                                 PedidoRepositoryPort pedidoRepositoryPort) {
        this.pasarelaPagoPort = pasarelaPagoPort;
        this.inventarioPort = inventarioPort;
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido procesar(ProcesarCompraCommand command) throws Exception {
        Optional<Pedido> pedidoOpt = pedidoRepositoryPort.buscarPorId(command.getIdPedido());
        if (pedidoOpt.isEmpty()) {
            throw new Exception("Pedido no encontrado: " + command.getIdPedido());
        }
        Pedido pedido = pedidoOpt.get();

        // Usar calcularTotalConExencion() para aplicar la cobertura de responsabilidad
        double totalConExencion = pedido.calcularTotalConExencion();

        // 1. Intentar procesar el pago con el total ajustado por exencion
        boolean pagoExitoso = pasarelaPagoPort.procesarPago(command.getMetodoPago(), totalConExencion);

        if (!pagoExitoso) {
            pedido.marcarComoRechazado();
            pedidoRepositoryPort.guardar(pedido);
            throw new Exception("El pago fue rechazado por la pasarela.");
        }

        // 2. Si el pago es exitoso, actualizamos estado
        pedido.marcarComoPagado();

        // 3. Descontar del inventario
        boolean inventarioActualizado = inventarioPort.descontarInventario(pedido.getDetalles());

        if (!inventarioActualizado) {
            throw new Exception("Error al descontar el inventario tras el pago.");
        }

        // 4. Persistir el pedido exitoso
        return pedidoRepositoryPort.guardar(pedido);
    }
}