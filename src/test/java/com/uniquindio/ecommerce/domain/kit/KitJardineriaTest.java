package com.uniquindio.ecommerce.domain.kit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KitJardineriaTest {

    // 1. Pruebas de Value Object

    @Test
    void dosPreciosConElMismoValorYMonedaDebenSerIguales() {
        // Arrange
        Precio precio1 = new Precio(15000, "COP");
        Precio precio2 = new Precio(15000, "COP");

        // Act & Assert
        assertEquals(precio1, precio2); // Igualdad por valor[cite: 3]
    }

    @Test
    void crearPrecioNegativoLanzaReglaDominioException() {
        // Arrange
        double valorInvalido = -5000;

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Precio(valorInvalido, "COP");
        });
    }

    // 2. Pruebas de Entidad

    @Test
    void dosArticulosConElMismoIdSonElMismoAunqueTenganDatosDistintos() {
        // Arrange
        Precio precio = new Precio(10000, "COP");
        ArticuloKit articuloOriginal = new ArticuloKit("ART-1", "Maceta Barro", precio, 20.0);
        ArticuloKit articuloModificado = new ArticuloKit("ART-1", "Maceta Plastico", precio, 25.0);

        // Act & Assert
        assertEquals(articuloOriginal, articuloModificado); // Igualdad por identidad[cite: 3]
    }

    @Test
    void crearArticuloConIdentidadAsignadaMantieneElId() {
        // Arrange
        String idEsperado = "ART-99";
        Precio precio = new Precio(5000, "COP");

        // Act
        ArticuloKit articulo = new ArticuloKit(idEsperado, "Sustrato Universal", precio, 0);

        // Assert
        assertEquals(idEsperado, articulo.getId()); // Comportamiento de la entidad
    }

    //  3. Pruebas del Agregado

    @Test
    void cambiarPorMacetaIncompatibleLanzaReglaDominioException() throws Exception {
        // Arrange
        Precio precio = new Precio(10000, "COP");
        ArticuloKit planta = new ArticuloKit("P-1", "Monstera", precio, 20.0);
        ArticuloKit maceta = new ArticuloKit("M-1", "Maceta Grande", precio, 25.0);
        ArticuloKit sustrato = new ArticuloKit("S-1", "Abono", precio, 0);

        KitJardineria kit = new KitJardineria(planta, maceta, sustrato);
        ArticuloKit macetaPequena = new ArticuloKit("M-2", "Maceta Pequeña", precio, 15.0);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            kit.cambiarMaceta(macetaPequena); // Invariante: Maceta >= Planta[cite: 3]
        });
    }

    @Test
    void estadoDeMacetaNoCambiaTrasRechazoPorIncompatibilidad() throws Exception {
        // Arrange
        Precio precio = new Precio(10000, "COP");
        ArticuloKit planta = new ArticuloKit("P-1", "Monstera", precio, 20.0);
        ArticuloKit macetaOriginal = new ArticuloKit("M-1", "Maceta Grande", precio, 25.0);
        ArticuloKit sustrato = new ArticuloKit("S-1", "Abono", precio, 0);

        KitJardineria kit = new KitJardineria(planta, macetaOriginal, sustrato);
        ArticuloKit macetaPequena = new ArticuloKit("M-2", "Maceta Pequeña", precio, 15.0);

        // Act
        try {
            kit.cambiarMaceta(macetaPequena);
        } catch (ReglaDominioException e) {
            // Se ignora la excepción intencionalmente para verificar el estado posterior
        }

        // Assert
        assertEquals(macetaOriginal, kit.getMaceta()); // El estado no cambió tras el rechazo[cite: 3]
    }
}