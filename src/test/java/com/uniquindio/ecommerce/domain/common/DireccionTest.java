package com.uniquindio.ecommerce.domain.common;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DireccionTest {

    @Test
    void crearDireccionValida() {
        Direccion dir = Direccion.de("Calle 10 #20-30", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
        
        assertEquals("Calle 10 #20-30", dir.calle());
        assertEquals("Armenia", dir.ciudad());
        assertEquals("Quindío", dir.departamento());
        assertEquals("630001", dir.codigoPostal());
        assertEquals("Colombia", dir.pais());
        assertEquals(4.5339, dir.latitud());
        assertEquals(-75.6811, dir.longitud());
    }

    @Test
    void crearDireccionConCalleNulaLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> 
            Direccion.de(null, "Armenia", "Quindío", "630001", "Colombia", 4.5, -75.6));
    }

    @Test
    void crearDireccionConCiudadVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            Direccion.de("Calle 10", "  ", "Quindío", "630001", "Colombia", 4.5, -75.6));
    }

    @Test
    void crearDireccionConCodigoPostalInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            Direccion.de("Calle 10", "Armenia", "Quindío", "123", "Colombia", 4.5, -75.6));
    }

    @Test
    void crearDireccionConLatitudFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            Direccion.de("Calle 10", "Armenia", "Quindío", "630001", "Colombia", 95, -75.6));
    }

    @Test
    void crearDireccionConLongitudFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            Direccion.de("Calle 10", "Armenia", "Quindío", "630001", "Colombia", 4.5, 200));
    }

    @Test
    void distanciaEntreDosDirecciones() {
        Direccion armenia = Direccion.de("Calle 10", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
        Direccion pereira = Direccion.de("Calle 20", "Pereira", "Risaralda", "660001", "Colombia", 4.8133, -75.6936);
        
        double distancia = armenia.distanciaA(pereira);
        
        // Aproximadamente 31 km entre Armenia y Pereira
        assertTrue(distancia > 30 && distancia < 35);
    }

    @Test
    void distanciaCeroMismaDireccion() {
        Direccion dir = Direccion.de("Calle 10", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
        
        assertEquals(0, dir.distanciaA(dir));
    }

    @Test
    void toStringFormatoCorrecto() {
        Direccion dir = Direccion.de("Calle 10 #20-30", "Armenia", "Quindío", "630001", "Colombia", 4.5339, -75.6811);
        
        assertEquals("Calle 10 #20-30, Armenia, Quindío 630001, Colombia", dir.toString());
    }
}