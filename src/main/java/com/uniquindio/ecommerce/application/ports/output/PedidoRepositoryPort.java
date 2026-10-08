package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.pedido.Pedido;
import java.util.Optional;

public interface PedidoRepositoryPort {
    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(String idPedido);
}