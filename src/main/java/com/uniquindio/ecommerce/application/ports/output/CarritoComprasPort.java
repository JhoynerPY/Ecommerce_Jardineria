package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;

public interface CarritoComprasPort {
    void agregarKit(String idCliente, KitJardineria kit);
}