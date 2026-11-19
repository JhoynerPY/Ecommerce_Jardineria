package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.CrearPedidoCommand;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

public interface CrearPedidoUseCase {
    Pedido crear(CrearPedidoCommand command);
}
