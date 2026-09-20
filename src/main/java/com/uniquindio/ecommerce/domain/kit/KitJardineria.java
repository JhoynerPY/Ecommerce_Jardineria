package com.uniquindio.ecommerce.domain.kit;

import java.util.UUID;

public class KitJardineria {
    private String idKit;
    private ArticuloKit planta;
    private ArticuloKit maceta;
    private ArticuloKit sustrato;
    private static final double DESCUENTO_PAQUETE = 0.15; // 15% de descuento

    public KitJardineria(ArticuloKit planta, ArticuloKit maceta, ArticuloKit sustrato) throws Exception {
        if (!esMacetaCompatible(planta, maceta)) {
            throw new Exception("La maceta seleccionada no es compatible con las dimensiones de la planta.");
        }

        this.idKit = UUID.randomUUID().toString();
        this.planta = planta;
        this.maceta = maceta;
        this.sustrato = sustrato;
    }

    private boolean esMacetaCompatible(ArticuloKit planta, ArticuloKit maceta) {
        // Regla de negocio: La maceta debe ser al menos igual o ligeramente más grande que la planta
        return maceta.getDiametro() >= planta.getDiametro();
    }

    public double calcularPrecioTotal() {
        double subtotal = planta.getPrecio() + maceta.getPrecio() + sustrato.getPrecio();
        return subtotal - (subtotal * DESCUENTO_PAQUETE);
    }

    public String getIdKit() { return idKit; }
    public ArticuloKit getPlanta() { return planta; }
    public ArticuloKit getMaceta() { return maceta; }
    public ArticuloKit getSustrato() { return sustrato; }
}