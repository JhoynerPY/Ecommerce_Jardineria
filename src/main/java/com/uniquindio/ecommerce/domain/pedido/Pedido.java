package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
    private final String idPedido;
    private final String idCliente;
    private final List<DetallePedido> detalles;
    private EstadoPedido estado;
    private final String direccionEntrega;

    // Rúbrica: Constructor privado
    private Pedido(String idPedido, String idCliente, List<DetallePedido> detalles, String direccionEntrega) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.detalles = detalles;
        this.direccionEntrega = direccionEntrega;
        this.estado = EstadoPedido.PENDIENTE_PAGO;
    }

    // Rúbrica: Método de fábrica
    public static Pedido crear(String idCliente, List<DetallePedido> detalles, String direccionEntrega) {
        // Invariante 1: Un pedido no tiene sentido sin detalles
        if (detalles == null || detalles.isEmpty()) {
            throw new ReglaDominioException("El pedido debe contener al menos un detalle (artículo o kit).");
        }

        // Invariante 2: Se requiere una dirección válida para procesar la entrega
        if (direccionEntrega == null || direccionEntrega.trim().isEmpty()) {
            throw new ReglaDominioException("La dirección de entrega no puede estar vacía.");
        }

        return new Pedido(UUID.randomUUID().toString(), idCliente, detalles, direccionEntrega);
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }

    public void marcarComoPagado() {
        // Invariante 3: Solo se puede pagar un pedido que esté esperando pago
        if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
            throw new ReglaDominioException("Solo se puede pagar un pedido que esté pendiente de pago.");
        }
        this.estado = EstadoPedido.PAGADO;
    }

    public void marcarComoRechazado() {
        if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
            throw new ReglaDominioException("No se puede rechazar un pedido que ya fue procesado o pagado.");
        }
        this.estado = EstadoPedido.RECHAZADO;
    }

    // Getters omitidos por brevedad (manten los tuyos)
    public String getIdPedido() { return idPedido; }
    public String getIdCliente() { return idCliente; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public EstadoPedido getEstado() { return estado; }
    public String getDireccionEntrega() { return direccionEntrega; }

    // Rúbrica: equals/hashCode por identidad
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(idPedido, pedido.idPedido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido);
    }
}