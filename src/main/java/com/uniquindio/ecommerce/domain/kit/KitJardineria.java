package com.uniquindio.ecommerce.domain.kit;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.Objects;
import java.util.UUID;

public class KitJardineria {
    private final String idKit;
    private final ArticuloKit planta;
    private ArticuloKit maceta;
    private final ArticuloKit sustrato;
    private static final double DESCUENTO_PAQUETE = 0.15;


    private KitJardineria(String idKit, ArticuloKit planta, ArticuloKit maceta, ArticuloKit sustrato) {
        this.idKit = idKit;
        this.planta = planta;
        this.maceta = maceta;
        this.sustrato = sustrato;
    }

    // Método de fábrica para instanciar
    public static KitJardineria crear(ArticuloKit planta, ArticuloKit maceta, ArticuloKit sustrato) {
        // Invariante 1: Un kit requiere estrictamente los 3 elementos bases
        if (planta == null || maceta == null || sustrato == null) {
            throw new ReglaDominioException("El kit debe contener una planta, una maceta y un sustrato.");
        }

        // Invariante 2: La maceta debe ser al menos igual o más grande que la planta
        if (!esMacetaCompatible(planta, maceta)) {
            throw new ReglaDominioException("La maceta seleccionada no es compatible con las dimensiones de la planta.");
        }

        return new KitJardineria(UUID.randomUUID().toString(), planta, maceta, sustrato);
    }

    private static boolean esMacetaCompatible(ArticuloKit planta, ArticuloKit maceta) {
        return maceta.getDiametro() >= planta.getDiametro();
    }

    public double calcularPrecioTotal() {
        double subtotal = planta.getPrecio().getValor() +
                maceta.getPrecio().getValor() +
                sustrato.getPrecio().getValor();
        return subtotal - (subtotal * DESCUENTO_PAQUETE);
    }

    public void cambiarMaceta(ArticuloKit nuevaMaceta) {
        // Invariante 3: No se puede dejar el kit sin maceta ni poner una incompatible
        if (nuevaMaceta == null) {
            throw new ReglaDominioException("La nueva maceta no puede ser nula.");
        }
        if (nuevaMaceta.getDiametro() < planta.getDiametro()) {
            throw new ReglaDominioException("La maceta es muy pequeña para la planta actual.");
        }
        this.maceta = nuevaMaceta;
    }

    public String getIdKit() { return idKit; }
    public ArticuloKit getPlanta() { return planta; }
    public ArticuloKit getMaceta() { return maceta; }
    public ArticuloKit getSustrato() { return sustrato; }

    // equals/hashCode por identidad
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KitJardineria that = (KitJardineria) o;
        return Objects.equals(idKit, that.idKit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKit);
    }
}