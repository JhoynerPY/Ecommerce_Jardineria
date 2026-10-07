package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.ports.input.ProcesarCompraUseCase;
import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.application.ports.output.PasarelaPagoPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

public class ProcesarCompraService implements ProcesarCompraUseCase {

    private final PasarelaPagoPort pasarelaPagoPort;
    private final InventarioPort inventarioPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;

    // Inyección de dependencias mediante el constructor
    public ProcesarCompraService(PasarelaPagoPort pasarelaPagoPort,
                                 InventarioPort inventarioPort,
                                 PedidoRepositoryPort pedidoRepositoryPort) {
        this.pasarelaPagoPort = pasarelaPagoPort;
        this.inventarioPort = inventarioPort;
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido procesar(Pedido pedido, String metodoPago) throws Exception {
        double total = pedido.calcularTotal();

        // 1. Intentar procesar el pago
        boolean pagoExitoso = pasarelaPagoPort.procesarPago(metodoPago, total);

        if (!pagoExitoso) {
            pedido.marcarComoRechazado();
            pedidoRepositoryPort.guardar(pedido); // Guardamos el registro del intento fallido
            throw new Exception("El pago fue rechazado por la pasarela.");
        }

        // 2. Si el pago es exitoso, actualizamos estado
        pedido.marcarComoPagado();

        // 3. Descontar del inventario general (sin usar consultarEjemplares)
        boolean inventarioActualizado = inventarioPort.descontarInventario(pedido.getDetalles());

        if (!inventarioActualizado) {
            // Aquí iría una lógica de compensación (ej. reversar el pago) si el inventario falla
            throw new Exception("Error al descontar el inventario tras el pago.");
        }

        // 4. Persistir el pedido exitoso
        return pedidoRepositoryPort.guardar(pedido);
    }
}