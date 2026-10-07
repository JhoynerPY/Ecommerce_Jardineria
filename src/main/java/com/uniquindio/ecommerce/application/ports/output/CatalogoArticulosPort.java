package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.kit.ArticuloKit;

public interface CatalogoArticulosPort {
    ArticuloKit obtenerArticulo(String idArticulo);
}