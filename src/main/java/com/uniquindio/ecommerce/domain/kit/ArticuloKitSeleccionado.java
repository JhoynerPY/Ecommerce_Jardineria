package com.uniquindio.ecommerce.domain.kit;

public record ArticuloKitSeleccionado(ArticuloKit articulo, int cantidad) {
    public ArticuloKitSeleccionado {
        if (articulo == null) {
            throw new IllegalArgumentException("El ArticuloKit no puede ser nulo");
        }
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor o igual a 1");
        }
    }
}