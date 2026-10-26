package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.CrearPedidoCommand;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.application.testsupport.InMemoryClienteRepository;
import com.uniquindio.ecommerce.application.testsupport.InMemoryPedidoRepository;
import com.uniquindio.ecommerce.application.testsupport.InMemoryViveroRepository;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrearPedidoServiceTest {

    private InMemoryClienteRepository clienteRepository;
    private InMemoryViveroRepository viveroRepository;
    private InMemoryPedidoRepository pedidoRepository;
    private CrearPedidoService service;

    private Direccion direccionEntrega() {
        return Direccion.de("Calle 25 #35-45", "Armenia", "Quindio", "630001", "Colombia", 4.5410, -75.6860);
    }

    private Direccion direccionVivero() {
        return Direccion.de("Carrera 15 #10-20", "Armenia", "Quindio", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Cliente clienteConVivero(String id, String viveroId, String email) {
        Cliente cliente = mock(Cliente.class);
        when(cliente.getId()).thenReturn(id);
        when(cliente.getEmail()).thenReturn(new Email(email));
        when(cliente.tieneViveroAsignado()).thenReturn(true);
        when(cliente.getViveroAsignadoId()).thenReturn(viveroId);
        return cliente;
    }

    private Cliente clienteSinVivero(String id, String email) {
        Cliente cliente = mock(Cliente.class);
        when(cliente.getId()).thenReturn(id);
        when(cliente.getEmail()).thenReturn(new Email(email));
        when(cliente.tieneViveroAsignado()).thenReturn(false);
        return cliente;
    }

    private Vivero viveroQueCubre(String id, Direccion direccion) {
        Vivero vivero = mock(Vivero.class);
        when(vivero.getId()).thenReturn(id);
        when(vivero.getDireccion()).thenReturn(direccion);
        when(vivero.getRadioCoberturaKm()).thenReturn(15.0);
        when(vivero.isActivo()).thenReturn(true);
        when(vivero.cubreDireccion(any(Direccion.class))).thenReturn(true);
        return vivero;
    }

    private Vivero viveroQueNoCubre(String id, Direccion direccion) {
        Vivero vivero = mock(Vivero.class);
        when(vivero.getId()).thenReturn(id);
        when(vivero.getDireccion()).thenReturn(direccion);
        when(vivero.getRadioCoberturaKm()).thenReturn(15.0);
        when(vivero.isActivo()).thenReturn(true);
        when(vivero.cubreDireccion(any(Direccion.class))).thenReturn(false);
        return vivero;
    }

    private CrearPedidoCommand comandoValido(ExencionResponsabilidad.TipoExencion tipo) {
        return new CrearPedidoCommand(
            "CLI-001",
            List.of(new DetallePedido("ART-001", 2, 10000)),
            direccionEntrega(),
            tipo,
            "Terminos de exencion de responsabilidad por danos en transporte",
            "1.0",
            true
        );
    }

    @BeforeEach
    void setUp() {
        clienteRepository = new InMemoryClienteRepository();
        viveroRepository = new InMemoryViveroRepository();
        pedidoRepository = new InMemoryPedidoRepository();
        service = new CrearPedidoService(clienteRepository, viveroRepository, pedidoRepository);
    }

    @Test
    void crearPedidoExitosoConExencionParcial() {
        // Arrange
        Cliente cliente = clienteConVivero("CLI-001", "VIV-001", "cliente@test.com");
        clienteRepository.guardar(cliente);
        Vivero vivero = viveroQueCubre("VIV-001", direccionVivero());
        viveroRepository.guardar(vivero);

        // Act
        Pedido resultado = service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.ACUERDO_PARCIAL_PERDIDA));

        // Assert
        assertNotNull(resultado);
        assertEquals("CLI-001", resultado.getIdCliente());
        assertEquals("VIV-001", resultado.getIdVivero());
        assertEquals(ExencionResponsabilidad.TipoExencion.ACUERDO_PARCIAL_PERDIDA, resultado.getExencionResponsabilidad().tipo());
    }

    @Test
    void crearPedidoExitosoConExencionTotal() {
        // Arrange
        Cliente cliente = clienteConVivero("CLI-001", "VIV-001", "cliente@test.com");
        clienteRepository.guardar(cliente);
        Vivero vivero = viveroQueCubre("VIV-001", direccionVivero());
        viveroRepository.guardar(vivero);

        // Act
        Pedido resultado = service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.EXENCION_TOTAL));

        // Assert
        assertEquals(ExencionResponsabilidad.TipoExencion.EXENCION_TOTAL, resultado.getExencionResponsabilidad().tipo());
        assertEquals(0.0, resultado.calcularTotalConExencion());
    }

    @Test
    void crearPedidoExitosoSinExencion() {
        // Arrange
        Cliente cliente = clienteConVivero("CLI-001", "VIV-001", "cliente@test.com");
        clienteRepository.guardar(cliente);
        Vivero vivero = viveroQueCubre("VIV-001", direccionVivero());
        viveroRepository.guardar(vivero);

        // Act
        Pedido resultado = service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.SIN_EXENCION));

        // Assert
        assertEquals(ExencionResponsabilidad.TipoExencion.SIN_EXENCION, resultado.getExencionResponsabilidad().tipo());
        assertEquals(20000.0, resultado.calcularTotalConExencion());
    }

    @Test
    void crearPedidoClienteNoExisteLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.SIN_EXENCION)));
    }

    @Test
    void crearPedidoClienteSinViveroAsignadoLanzaExcepcion() {
        // Arrange
        Cliente cliente = clienteSinVivero("CLI-002", "cliente2@test.com");
        clienteRepository.guardar(cliente);

        assertThrows(ReglaDominioException.class, () -> service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.SIN_EXENCION)));
    }

    @Test
    void crearPedidoViveroNoEncontradoLanzaExcepcion() {
        // Arrange
        Cliente cliente = clienteConVivero("CLI-001", "VIV-999", "cliente@test.com");
        clienteRepository.guardar(cliente);

        assertThrows(ReglaDominioException.class, () -> service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.SIN_EXENCION)));
    }

    @Test
    void crearPedidoViveroNoCubreDireccionEntregaLanzaExcepcion() {
        // Arrange
        Cliente cliente = clienteConVivero("CLI-001", "VIV-002", "cliente@test.com");
        clienteRepository.guardar(cliente);
        Vivero vivero = viveroQueNoCubre("VIV-002", direccionVivero());
        viveroRepository.guardar(vivero);

        assertThrows(ReglaDominioException.class, () -> service.crear(comandoValido(ExencionResponsabilidad.TipoExencion.SIN_EXENCION)));
    }
}