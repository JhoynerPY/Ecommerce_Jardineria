package com.uniquindio.ecommerce.application.ports.input;

import com.uniquindio.ecommerce.domain.pedido.Pedido;

public interface ProcesarCompraUseCase {
    Pedido procesar(Pedido pedido, String metodoPago) throws Exception;
}