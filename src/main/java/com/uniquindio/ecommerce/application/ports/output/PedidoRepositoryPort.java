package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.pedido.Pedido;

public interface PedidoRepositoryPort {
    Pedido guardar(Pedido pedido);
}