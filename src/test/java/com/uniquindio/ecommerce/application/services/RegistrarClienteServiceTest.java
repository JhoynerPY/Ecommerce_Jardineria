package com.uniquindio.ecommerce.application.services;

import com.uniquindio.ecommerce.application.dto.RegistrarClienteCommand;
import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.application.testsupport.InMemoryClienteRepository;
import com.uniquindio.ecommerce.application.testsupport.InMemoryViveroRepository;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.domain.cliente.Email;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrarClienteServiceTest {

    private InMemoryClienteRepository clienteRepository;
    private InMemoryViveroRepository viveroRepository;
    private RegistrarClienteService service;

    private Direccion direccionValida() {
        return Direccion.de("Calle 10 #20-30", "Armenia", "Quindio", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Direccion direccionVivero() {
        return Direccion.de("Carrera 15 #10-20", "Armenia", "Quindio", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Vivero viveroActivo() {
        Vivero vivero = mock(Vivero.class);
        when(vivero.getId()).thenReturn("VIV-001");
        when(vivero.getDireccion()).thenReturn(direccionVivero());
        when(vivero.cubreDireccion(any(Direccion.class))).thenReturn(true);
        when(vivero.isActivo()).thenReturn(true);
        return vivero;
    }

    private RegistrarClienteCommand comandoValido() {
        return new RegistrarClienteCommand("CLI-001", "Juan Perez", "juan@test.com", "3101234567", direccionValida());
    }

    @BeforeEach
    void setUp() {
        clienteRepository = new InMemoryClienteRepository();
        viveroRepository = new InMemoryViveroRepository();
        viveroRepository.guardar(viveroActivo());
        service = new RegistrarClienteService(clienteRepository, viveroRepository);
    }

    @Test
    void registrarClienteExitosoAsignaViveroMasCercano() {
        // Act
        Cliente resultado = service.registrar(comandoValido());

        // Assert
        assertNotNull(resultado);
        assertEquals("CLI-001", resultado.getId());
        assertEquals("VIV-001", resultado.getViveroAsignadoId());
        assertTrue(resultado.tieneViveroAsignado());
    }

    @Test
    void registrarClienteEmailDuplicadoLanzaExcepcion() {
        // Arrange
        service.registrar(comandoValido());

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> service.registrar(comandoValido()));
    }

    @Test
    void registrarClienteSinViverosDisponiblesLanzaExcepcion() {
        // Arrange
        viveroRepository.limpiar();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> service.registrar(comandoValido()));
    }

    @Test
    void registrarClienteViveroNoCubreDireccionLanzaExcepcion() {
        // Arrange
        Vivero viveroLejano = mock(Vivero.class);
        when(viveroLejano.getId()).thenReturn("VIV-002");
        when(viveroLejano.getDireccion()).thenReturn(direccionVivero());
        when(viveroLejano.cubreDireccion(any(Direccion.class))).thenReturn(false);
        when(viveroLejano.isActivo()).thenReturn(true);

        viveroRepository.limpiar();
        viveroRepository.guardar(viveroLejano);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> service.registrar(comandoValido()));
    }
}