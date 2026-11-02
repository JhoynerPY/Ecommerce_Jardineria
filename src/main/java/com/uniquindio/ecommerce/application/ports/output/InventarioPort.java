package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import java.util.List;

public interface InventarioPort {
    boolean descontarInventario(List<DetallePedido> detalles);
    boolean reponerInventario (List<DetallePedido> detalles);
}