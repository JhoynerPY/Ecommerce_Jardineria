package com.uniquindio.ecommerce.domain.vivero;

import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViveroTest {

    private Direccion direccionVivero() {
        return Direccion.de("Carrera 15 #10-20", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
    }

    private Direccion direccionClienteCercana() {
        return Direccion.de("Calle 20 #30-40", "Armenia", "Quindío", "630001", "Colombia", 4.5400, -75.6850);
    }

    private Direccion direccionClienteLejana() {
        return Direccion.de("Calle 100", "Bogotá", "Cundinamarca", "110111", "Colombia", 4.7110, -74.0721);
    }

    @Test
    void registrarViveroValido() {
        Vivero vivero = Vivero.registrar("Jardines del Quindío", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertNotNull(vivero.getId());
        assertEquals("Jardines del Quindío", vivero.getNombre());
        assertEquals(15.0, vivero.getRadioCoberturaKm());
        assertTrue(vivero.isActivo());
    }

    @Test
    void registrarViveroSinNombreLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("  ", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0));
    }

    @Test
    void registrarViveroSinDireccionLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("Jardines", null, "3101234567", "contacto@jardinesquindio.com", 15.0));
    }

    @Test
    void registrarViveroConRadioCeroLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 0));
    }

    @Test
    void cubreDireccionClienteCercano() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertTrue(vivero.cubreDireccion(direccionClienteCercana()));
    }

    @Test
    void noCubreDireccionClienteLejano() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertFalse(vivero.cubreDireccion(direccionClienteLejana()));
    }

    @Test
    void viveroInactivoNoCubreNingunaDireccion() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        vivero.desactivar();
        
        assertFalse(vivero.cubreDireccion(direccionClienteCercana()));
    }

    @Test
    void agregarYRemoverEspecieDisponible() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        vivero.agregarEspecieDisponible("ESP-001");
        assertTrue(vivero.tieneEspecieDisponible("ESP-001"));
        
        vivero.removerEspecieDisponible("ESP-001");
        assertFalse(vivero.tieneEspecieDisponible("ESP-001"));
    }

    @Test
    void noDuplicarEspeciesDisponibles() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        vivero.agregarEspecieDisponible("ESP-001");
        vivero.agregarEspecieDisponible("ESP-001");
        
        assertEquals(1, vivero.getEspeciesDisponiblesIds().size());
    }

    @Test
    void activarDesactivarVivero() {
        Vivero vivero = Vivero.registrar("Jardines", direccionVivero(), "3101234567", "contacto@jardinesquindio.com", 15.0);
        
        assertTrue(vivero.isActivo());
        
        vivero.desactivar();
        assertFalse(vivero.isActivo());
        
        vivero.activar();
        assertTrue(vivero.isActivo());
    }
}