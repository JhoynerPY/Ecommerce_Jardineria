package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private ExencionResponsabilidad exencionSinExencion() {
        return ExencionResponsabilidad.sinExencion();
    }

    private ExencionResponsabilidad exencionParcial() {
        return ExencionResponsabilidad.acuerdoParcialPerdida(
            "El vivero cubre 50% del valor en caso de daño total durante transporte", "1.0", true);
    }

    // 4. Pruebas del Agregado 2 (Pedido)
    @Test
    void crearPedidoSinDetallesLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        String idVivero = "VIV-456";
        List<DetallePedido> detallesVacios = new ArrayList<>();
        String direccion = "Calle 123";
        ExencionResponsabilidad exencion = exencionSinExencion();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Pedido.crear(idCliente, idVivero, detallesVacios, direccion, exencion);
        });
    }

    @Test
    void crearPedidoSinViveroLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        List<DetallePedido> detalles = List.of(new DetallePedido("ART-1", 2, 5000));
        String direccion = "Calle 123";
        ExencionResponsabilidad exencion = exencionSinExencion();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Pedido.crear(idCliente, null, detalles, direccion, exencion);
        });
    }

    @Test
    void crearPedidoSinExencionLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        String idVivero = "VIV-456";
        List<DetallePedido> detalles = List.of(new DetallePedido("ART-1", 2, 5000));
        String direccion = "Calle 123";

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Pedido.crear(idCliente, idVivero, detalles, direccion, null);
        });
    }

    @Test
    void crearExencionParcialSinAceptarLanzaExcepcion() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> 
            new ExencionResponsabilidad(
                ExencionResponsabilidad.TipoExencion.ACUERDO_PARCIAL_PERDIDA,
                "Términos", false, "1.0"
            )
        );
    }

    @Test
    void pagarPedidoQueNoEstaPendienteLanzaReglaDominioException() {
        // Arrange
        String idCliente = "CLI-123";
        String idVivero = "VIV-456";
        List<DetallePedido> detalles = List.of(new DetallePedido("ART-1", 2, 5000));
        String direccion = "Calle 123";
        ExencionResponsabilidad exencion = exencionSinExencion();

        Pedido pedido = Pedido.crear(idCliente, idVivero, detalles, direccion, exencion);
        pedido.marcarComoRechazado();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            pedido.marcarComoPagado();
        });
    }

    @Test
    void calcularTotalConExencionParcialReduceElTotal() {
        // Arrange
        String idCliente = "CLI-123";
        String idVivero = "VIV-456";
        List<DetallePedido> detalles = List.of(new DetallePedido("ART-1", 2, 10000));
        String direccion = "Calle 123";
        ExencionResponsabilidad exencion = exencionParcial();

        Pedido pedido = Pedido.crear(idCliente, idVivero, detalles, direccion, exencion);

        // Act
        double totalOriginal = pedido.calcularTotal();
        double totalConExencion = pedido.calcularTotalConExencion();

        // Assert
        assertEquals(20000, totalOriginal);
        assertEquals(10000, totalConExencion); // 50% coverage
    }

    @Test
    void calcularTotalConExencionTotalEsCero() {
        // Arrange
        ExencionResponsabilidad exencionTotal = ExencionResponsabilidad.exencionTotal(
            "Sin responsabilidad por daños en transporte", "1.0", true);

        Pedido pedido = Pedido.crear("CLI-123", "VIV-456",
            List.of(new DetallePedido("ART-1", 1, 10000)), "Calle 123", exencionTotal);

        // Act & Assert
        assertEquals(0, pedido.calcularTotalConExencion());
    }

    @Test
    void marcarComoEnviadoSoloSiPagado() {
        // Arrange
        Pedido pedido = Pedido.crear("CLI-123", "VIV-456",
            List.of(new DetallePedido("ART-1", 1, 10000)), "Calle 123", exencionSinExencion());

        // Act & Assert - no pagado
        assertThrows(ReglaDominioException.class, () -> {
            pedido.marcarComoEnviado();
        });

        // Pagado
        pedido.marcarComoPagado();
        assertDoesNotThrow(() -> pedido.marcarComoEnviado());
        assertEquals(EstadoPedido.ENVIADO, pedido.getEstado());
    }
}