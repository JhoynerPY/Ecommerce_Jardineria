package com.uniquindio.ecommerce.domain.cliente;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Direccion direccionValida() {
        return Direccion.de("Calle 10 #20-30", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Email emailValido() {
        return new Email("cliente@ejemplo.com");
    }

    @Test
    void registrarClienteValido() {
        Cliente cliente = Cliente.registrar("CLI-001", "Juan Pérez", emailValido(), "3101234567", direccionValida());
        
        assertEquals("CLI-001", cliente.getId());
        assertEquals("Juan Pérez", cliente.getNombre());
        assertEquals(emailValido(), cliente.getEmail());
        assertEquals("3101234567", cliente.getTelefono());
        assertEquals(direccionValida(), cliente.getDireccion());
        assertNull(cliente.getViveroAsignadoId());
        assertFalse(cliente.tieneViveroAsignado());
    }

    @Test
    void registrarClienteSinDireccionLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Cliente.registrar("CLI-001", "Juan", emailValido(), "3101234567", null));
    }

    @Test
    void actualizarDatosContactoActualizaDireccionYReseteaVivero() {
        Cliente cliente = Cliente.registrar("CLI-001", "Juan", emailValido(), "3101234567", direccionValida());
        cliente.asignarVivero("VIV-001");
        
        Direccion nuevaDireccion = Direccion.de("Calle 50", "Pereira", "Risaralda", "660001", "Colombia", 4.8133, -75.6936);
        cliente.actualizarDatosContacto("3207654321", nuevaDireccion);
        
        assertEquals("3207654321", cliente.getTelefono());
        assertEquals(nuevaDireccion, cliente.getDireccion());
        assertNull(cliente.getViveroAsignadoId());
        assertFalse(cliente.tieneViveroAsignado());
    }

    @Test
    void asignarVivero() {
        Cliente cliente = Cliente.registrar("CLI-001", "Juan", emailValido(), "3101234567", direccionValida());
        
        cliente.asignarVivero("VIV-001");
        
        assertEquals("VIV-001", cliente.getViveroAsignadoId());
        assertTrue(cliente.tieneViveroAsignado());
    }

    @Test
    void igualdadPorId() {
        Cliente c1 = Cliente.registrar("CLI-001", "Juan", emailValido(), "3101234567", direccionValida());
        Cliente c2 = Cliente.registrar("CLI-001", "Pedro", new Email("otro@ejemplo.com"), "3207654321", direccionValida());
        Cliente c3 = Cliente.registrar("CLI-002", "Juan", emailValido(), "3101234567", direccionValida());
        
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
    }
}