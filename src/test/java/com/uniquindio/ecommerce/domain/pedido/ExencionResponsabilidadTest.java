package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExencionResponsabilidadTest {

    @Test
    void crearExencionTotalValida() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.exencionTotal(
            "El vivero no se hace responsable por daños en transporte", "1.0", true);
        
        assertEquals(ExencionResponsabilidad.TipoExencion.EXENCION_TOTAL, exencion.tipo());
        assertTrue(exencion.aceptadaPorCliente());
        assertEquals("1.0", exencion.version());
    }

    @Test
    void crearAcuerdoParcialPerdidaValido() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.acuerdoParcialPerdida(
            "Cobertura del 50% por daños totales en transporte", "1.0", true);
        
        assertEquals(ExencionResponsabilidad.TipoExencion.ACUERDO_PARCIAL_PERDIDA, exencion.tipo());
        assertTrue(exencion.aceptadaPorCliente());
    }

    @Test
    void crearSinExencion() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.sinExencion();
        
        assertEquals(ExencionResponsabilidad.TipoExencion.SIN_EXENCION, exencion.tipo());
        assertTrue(exencion.aceptadaPorCliente());
        assertEquals("Sin exención de responsabilidad aplicada", exencion.terminos());
    }

    @Test
    void exencionTotalSinAceptarLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            ExencionResponsabilidad.exencionTotal("Términos", "1.0", false));
    }

    @Test
    void acuerdoParcialSinAceptarLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            ExencionResponsabilidad.acuerdoParcialPerdida("Términos", "1.0", false));
    }

    @Test
    void terminosVaciosLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            new ExencionResponsabilidad(ExencionResponsabilidad.TipoExencion.EXENCION_TOTAL, "  ", true, "1.0"));
    }

    @Test
    void versionVaciaLanzaExcepcion() {
        assertThrows(ReglaDominioException.class, () -> 
            new ExencionResponsabilidad(ExencionResponsabilidad.TipoExencion.EXENCION_TOTAL, "Términos", true, "  "));
    }

    @Test
    void calcularCoberturaExencionTotal() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.exencionTotal("Términos", "1.0", true);
        
        assertEquals(0.0, exencion.calcularCobertura(10000));
        assertEquals(0.0, exencion.calcularCobertura(50000));
    }

    @Test
    void calcularCoberturaAcuerdoParcial() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.acuerdoParcialPerdida("Términos", "1.0", true);
        
        assertEquals(5000, exencion.calcularCobertura(10000));
        assertEquals(25000, exencion.calcularCobertura(50000));
    }

    @Test
    void calcularCoberturaSinExencion() {
        ExencionResponsabilidad exencion = ExencionResponsabilidad.sinExencion();
        
        assertEquals(10000, exencion.calcularCobertura(10000));
        assertEquals(50000, exencion.calcularCobertura(50000));
    }

    @Test
    void esExencionTotal() {
        assertTrue(ExencionResponsabilidad.exencionTotal("T", "1", true).esExencionTotal());
        assertFalse(ExencionResponsabilidad.acuerdoParcialPerdida("T", "1", true).esExencionTotal());
        assertFalse(ExencionResponsabilidad.sinExencion().esExencionTotal());
    }

    @Test
    void esAcuerdoParcial() {
        assertTrue(ExencionResponsabilidad.acuerdoParcialPerdida("T", "1", true).esAcuerdoParcial());
        assertFalse(ExencionResponsabilidad.exencionTotal("T", "1", true).esAcuerdoParcial());
        assertFalse(ExencionResponsabilidad.sinExencion().esAcuerdoParcial());
    }

    @Test
    void requiereAceptacion() {
        assertTrue(ExencionResponsabilidad.exencionTotal("T", "1", true).requiereAceptacion());
        assertTrue(ExencionResponsabilidad.acuerdoParcialPerdida("T", "1", true).requiereAceptacion());
        assertFalse(ExencionResponsabilidad.sinExencion().requiereAceptacion());
    }

    @Test
    void igualdadPorValor() {
        ExencionResponsabilidad e1 = ExencionResponsabilidad.exencionTotal("Términos", "1.0", true);
        ExencionResponsabilidad e2 = ExencionResponsabilidad.exencionTotal("Términos", "1.0", true);
        ExencionResponsabilidad e3 = ExencionResponsabilidad.acuerdoParcialPerdida("Términos", "1.0", true);
        
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
    }
}