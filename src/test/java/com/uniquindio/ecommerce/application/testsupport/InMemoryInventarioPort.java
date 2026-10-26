package com.uniquindio.ecommerce.application.testsupport;

import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import java.util.List;

public class InMemoryInventarioPort implements InventarioPort {

    private boolean debeFallar = false;

    @Override
    public boolean descontarInventario(List<DetallePedido> detalles) {
        return !debeFallar;
    }

    public void setDebeFallar(boolean debeFallar) {
        this.debeFallar = debeFallar;
    }
}