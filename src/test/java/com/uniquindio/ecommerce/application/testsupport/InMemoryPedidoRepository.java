package com.uniquindio.ecommerce.application.testsupport;

import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPedidoRepository implements PedidoRepositoryPort {

    private final Map<String, Pedido> pedidos = new ConcurrentHashMap<>();

    @Override
    public Pedido guardar(Pedido pedido) {
        pedidos.put(pedido.getIdPedido(), pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> buscarPorId(String idPedido) {
        return Optional.ofNullable(pedidos.get(idPedido));
    }

    @Override
    public List<Pedido> buscarPorClienteId(String idCliente) {
        return pedidos.values().stream()
                .filter(pedido -> pedido.getIdCliente().equals(idCliente))
                .toList();
    }

    public void limpiar() {
        pedidos.clear();
    }
}