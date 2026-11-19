package com.uniquindio.ecommerce.domain.kit;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KitJardineriaTest {

    @Test
    void dosPreciosConElMismoValorYMonedaDebenSerIguales() {
        Precio precio1 = new Precio(15000, "COP");
        Precio precio2 = new Precio(15000, "COP");

        assertEquals(precio1, precio2);
    }

    @Test
    void crearPrecioNegativoLanzaReglaDominioException() {
        assertThrows(ReglaDominioException.class, () -> new Precio(-5000, "COP"));
    }

    @Test
    void dosArticulosConElMismoIdSonElMismoAunqueTenganDatosDistintos() {
        Precio precio = new Precio(10000, "COP");
        ArticuloKit articuloOriginal = new ArticuloKit("ART-1", "Maceta Barro", precio, 20.0);
        ArticuloKit articuloModificado = new ArticuloKit("ART-1", "Maceta Plastico", precio, 25.0);

        assertEquals(articuloOriginal, articuloModificado);
    }

    @Test
    void crearArticuloConIdentidadAsignadaMantieneElId() {
        Precio precio = new Precio(5000, "COP");
        ArticuloKit articulo = new ArticuloKit("ART-99", "Sustrato Universal", precio, 0);

        assertEquals("ART-99", articulo.getId());
    }

    @Test
    void crearKitConVariosArticulosCalculaSubtotalDescuentoYTotal() {
        ArticuloKit abono = new ArticuloKit("abono-001", "Abono orgánico",
                new Precio(10000, "COP"), 0);
        ArticuloKit maceta = new ArticuloKit("maceta-001", "Maceta terracota",
                new Precio(20000, "COP"), 18);
        ArticuloKit sustrato = new ArticuloKit("sustrato-001", "Sustrato universal",
                new Precio(5000, "COP"), 0);

        KitJardineria kit = KitJardineria.crear(List.of(
                new ArticuloKitSeleccionado(abono, 2),
                new ArticuloKitSeleccionado(maceta, 1),
                new ArticuloKitSeleccionado(sustrato, 3)
        ), "Kit de prueba");

        assertEquals(20000 + 20000 + 15000, kit.calcularSubtotal(), 0.0001);
        assertEquals(8250, kit.calcularDescuento(), 0.0001);
        assertEquals(46750, kit.calcularPrecioTotal(), 0.0001);
    }

    @Test
    void crearKitSinArticulosLanzaReglaDominioException() {
        assertThrows(ReglaDominioException.class,
                () -> KitJardineria.crear(List.of(), "Kit vacío"));
    }

    @Test
    void crearKitConArticuloRepetidoLanzaReglaDominioException() {
        ArticuloKit abono = new ArticuloKit("abono-001", "Abono orgánico",
                new Precio(10000, "COP"), 0);

        assertThrows(ReglaDominioException.class,
                () -> KitJardineria.crear(List.of(
                        new ArticuloKitSeleccionado(abono, 1),
                        new ArticuloKitSeleccionado(abono, 2)
                ), "Kit repetido"));
    }

    @Test
    void crearSeleccionConCantidadInvalidaLanzaIllegalArgumentException() {
        ArticuloKit abono = new ArticuloKit("abono-001", "Abono orgánico",
                new Precio(10000, "COP"), 0);

        assertThrows(IllegalArgumentException.class,
                () -> new ArticuloKitSeleccionado(abono, 0));
    }
}