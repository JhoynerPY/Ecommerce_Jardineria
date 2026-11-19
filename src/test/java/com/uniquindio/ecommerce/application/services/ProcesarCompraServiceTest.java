package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.ProcesarCompraCommand;
import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.application.ports.output.PasarelaPagoPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.application.testsupport.InMemoryInventarioPort;
import com.uniquindio.ecommerce.application.testsupport.InMemoryPasarelaPagoPort;
import com.uniquindio.ecommerce.application.testsupport.InMemoryPedidoRepository;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcesarCompraServiceTest {

    private InMemoryPedidoRepository pedidoRepository;
    private InMemoryPasarelaPagoPort pasarelaPagoPort;
    private InMemoryInventarioPort inventarioPort;
    private ProcesarCompraService service;

    private Pedido pedidoConExencionParcial() {
        Pedido pedido = mock(Pedido.class);
        when(pedido.getIdPedido()).thenReturn("PED-001");
        when(pedido.getDetalles()).thenReturn(List.of(new DetallePedido("ART-001", 2, 10000)));
        when(pedido.calcularTotalConExencion()).thenReturn(10000.0);
        when(pedido.getExencionResponsabilidad()).thenReturn(
            ExencionResponsabilidad.acuerdoParcialPerdida("Terminos", "1.0", true)
        );
        // Allow state changes
        doNothing().when(pedido).marcarComoRechazado();
        doNothing().when(pedido).marcarComoPagado();
        return pedido;
    }

    private Pedido pedidoSinExencion() {
        Pedido pedido = mock(Pedido.class);
        when(pedido.getIdPedido()).thenReturn("PED-002");
        when(pedido.getDetalles()).thenReturn(List.of(new DetallePedido("ART-001", 1, 15000)));
        when(pedido.calcularTotalConExencion()).thenReturn(15000.0);
        when(pedido.getExencionResponsabilidad()).thenReturn(ExencionResponsabilidad.sinExencion());
        doNothing().when(pedido).marcarComoRechazado();
        doNothing().when(pedido).marcarComoPagado();
        return pedido;
    }

    @BeforeEach
    void setUp() {
        pedidoRepository = new InMemoryPedidoRepository();
        pasarelaPagoPort = new InMemoryPasarelaPagoPort();
        inventarioPort = new InMemoryInventarioPort();
        service = new ProcesarCompraService(pasarelaPagoPort, inventarioPort, pedidoRepository);
    }

    @Test
    void procesarCompraExitosoConExencionParcialUsaTotalConExencion() throws Exception {
        // Arrange
        ProcesarCompraCommand command = new ProcesarCompraCommand("PED-001", "TARJETA");
        Pedido mockPedido = pedidoConExencionParcial();
        pedidoRepository.guardar(mockPedido);

        // Act
        Pedido resultado = service.procesar(command);

        // Assert
        assertNotNull(resultado);
        assertEquals("PED-001", resultado.getIdPedido());
    }

    @Test
    void procesarCompraExitosoSinExencionUsaTotalCompleto() throws Exception {
        // Arrange
        ProcesarCompraCommand command = new ProcesarCompraCommand("PED-002", "EFECTIVO");
        Pedido mockPedido = pedidoSinExencion();
        pedidoRepository.guardar(mockPedido);

        // Act
        Pedido resultado = service.procesar(command);

        // Assert
        assertNotNull(resultado);
    }

    @Test
    void procesarCompraPagoRechazadoMarcaPedidoComoRechazado() {
        // Arrange
        ProcesarCompraCommand command = new ProcesarCompraCommand("PED-001", "TARJETA");
        Pedido mockPedido = pedidoConExencionParcial();
        pedidoRepository.guardar(mockPedido);
        pasarelaPagoPort.setDebeFallar(true);

        // Act & Assert
        assertThrows(Exception.class, () -> service.procesar(command));
        verify(mockPedido).marcarComoRechazado();
    }

    @Test
    void procesarCompraPedidoNoExisteLanzaExcepcion() {
        ProcesarCompraCommand command = new ProcesarCompraCommand("PED-999", "TARJETA");

        assertThrows(Exception.class, () -> service.procesar(command));
    }

    @Test
    void procesarCompraInventarioFallaLanzaExcepcion() throws Exception {
        // Arrange
        ProcesarCompraCommand command = new ProcesarCompraCommand("PED-001", "TARJETA");
        Pedido mockPedido = pedidoConExencionParcial();
        pedidoRepository.guardar(mockPedido);
        inventarioPort.setDebeFallar(true);

        // Act & Assert
        assertThrows(Exception.class, () -> service.procesar(command));
        verify(mockPedido).marcarComoPagado();
    }
}