package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.CancelarPedidoCommand;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

public interface CancelarPedidoUseCase {

    Pedido cancelar(CancelarPedidoCommand command);
}
