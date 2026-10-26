package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.application.dto.ProcesarCompraCommand;
import com.uniquindio.ecommerce.domain.pedido.Pedido;

public interface ProcesarCompraUseCase {
    Pedido procesar(ProcesarCompraCommand command) throws Exception;
}
