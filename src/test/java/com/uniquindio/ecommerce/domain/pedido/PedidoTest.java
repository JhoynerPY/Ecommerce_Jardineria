package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    // 4. Pruebas del Agregado 2 (Pedido)
    @Test
    void crearPedidoSinDetallesLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        List<DetallePedido> detallesVacios = new ArrayList<>();
        String direccion = "Calle 123";

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Pedido.crear(idCliente, detallesVacios, direccion); // Invariante: Pedido requiere detalles
        });
    }

    @Test
    void pagarPedidoQueNoEstaPendienteLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        List<DetallePedido> detalles = List.of(new DetallePedido("ART-1", 2, 5000));
        String direccion = "Calle 123";

        Pedido pedido = Pedido.crear(idCliente, detalles, direccion);
        pedido.marcarComoRechazado(); // Cambiamos el estado a RECHAZADO

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            pedido.marcarComoPagado(); // Invariante: Transición de estado inválida
        });
    }
}