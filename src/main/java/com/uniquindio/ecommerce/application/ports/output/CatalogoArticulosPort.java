package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import java.util.List;

public interface CatalogoArticulosPort {
    ArticuloKit obtenerArticulo(String idArticulo);
    List<ArticuloKit> listarArticulos();
}