package com.uniquindio.ecommerce.domain.kit;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
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
        assertEquals(precio1, precio2);
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
        assertEquals(articuloOriginal, articuloModificado);
    }

    @Test
    void crearArticuloConIdentidadAsignadaMantieneElId() {
        // Arrange
        String idEsperado = "ART-99";
        Precio precio = new Precio(5000, "COP");

        // Act
        ArticuloKit articulo = new ArticuloKit(idEsperado, "Sustrato Universal", precio, 0);

        // Assert
        assertEquals(idEsperado, articulo.getId());
    }

    // 3. Pruebas del Agregado 1 (KitJardineria)
    @Test
    void cambiarPorMacetaIncompatibleLanzaReglaDominioException() {
        // Arrange
        Precio precio = new Precio(10000, "COP");
        ArticuloKit planta = new ArticuloKit("P-1", "Monstera", precio, 20.0);
        ArticuloKit maceta = new ArticuloKit("M-1", "Maceta Grande", precio, 25.0);
        ArticuloKit sustrato = new ArticuloKit("S-1", "Abono", precio, 0);

        KitJardineria kit = KitJardineria.crear(planta, maceta, sustrato);
        ArticuloKit macetaPequena = new ArticuloKit("M-2", "Maceta Pequeña", precio, 15.0);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            kit.cambiarMaceta(macetaPequena); // Invariante
        });
    }

    @Test
    void estadoDeMacetaNoCambiaTrasRechazoPorIncompatibilidad() {
        // Arrange
        Precio precio = new Precio(10000, "COP");
        ArticuloKit planta = new ArticuloKit("P-1", "Monstera", precio, 20.0);
        ArticuloKit macetaOriginal = new ArticuloKit("M-1", "Maceta Grande", precio, 25.0);
        ArticuloKit sustrato = new ArticuloKit("S-1", "Abono", precio, 0);

        KitJardineria kit = KitJardineria.crear(planta, macetaOriginal, sustrato);
        ArticuloKit macetaPequena = new ArticuloKit("M-2", "Maceta Pequeña", precio, 15.0);

        // Act
        try {
            kit.cambiarMaceta(macetaPequena);
        } catch (ReglaDominioException e) {
            // Assert implícito: Se captura la excepción esperada
        }

        // Assert
        assertEquals(macetaOriginal, kit.getMaceta());
    }
}