package com.uniquindio.ecommerce.domain.pedido;

import java.util.List;
import java.util.UUID;

public class Pedido {
    private String idPedido;
    private String idCliente;
    private List<DetallePedido> detalles;
    private EstadoPedido estado;
    private String direccionEntrega;

    public Pedido(String idCliente, List<DetallePedido> detalles, String direccionEntrega) {
        this.idPedido = UUID.randomUUID().toString();
        this.idCliente = idCliente;
        this.detalles = detalles;
        this.direccionEntrega = direccionEntrega;
        this.estado = EstadoPedido.PENDIENTE_PAGO;
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }

    public void marcarComoPagado() {
        this.estado = EstadoPedido.PAGADO;
    }

    public void marcarComoRechazado() {
        this.estado = EstadoPedido.RECHAZADO;
    }

    // Getters
    public String getIdPedido() { return idPedido; }
    public String getIdCliente() { return idCliente; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public EstadoPedido getEstado() { return estado; }
    public String getDireccionEntrega() { return direccionEntrega; }
}