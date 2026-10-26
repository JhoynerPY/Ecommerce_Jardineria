package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.common.ReglaDominioException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
    private final String idPedido;
    private final String idCliente;
    private final String idVivero;
    private final List<DetallePedido> detalles;
    private EstadoPedido estado;
    private final String direccionEntrega;
    private ExencionResponsabilidad exencionResponsabilidad;

    private Pedido(String idPedido, String idCliente, String idVivero, List<DetallePedido> detalles, String direccionEntrega, ExencionResponsabilidad exencionResponsabilidad) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idVivero = idVivero;
        this.detalles = detalles;
        this.direccionEntrega = direccionEntrega;
        this.exencionResponsabilidad = exencionResponsabilidad;
        this.estado = EstadoPedido.PENDIENTE_PAGO;
    }

    public static Pedido crear(String idCliente, String idVivero, List<DetallePedido> detalles, String direccionEntrega, ExencionResponsabilidad exencionResponsabilidad) {
        if (detalles == null || detalles.isEmpty()) {
            throw new ReglaDominioException("El pedido debe contener al menos un detalle (artículo o kit).");
        }

        if (direccionEntrega == null || direccionEntrega.trim().isEmpty()) {
            throw new ReglaDominioException("La dirección de entrega no puede estar vacía.");
        }

        if (idVivero == null || idVivero.trim().isEmpty()) {
            throw new ReglaDominioException("El pedido debe estar asignado a un vivero.");
        }

        if (exencionResponsabilidad == null) {
            throw new ReglaDominioException("La exención de responsabilidad es obligatoria.");
        }

        return new Pedido(UUID.randomUUID().toString(), idCliente, idVivero, detalles, direccionEntrega, exencionResponsabilidad);
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }

    public double calcularTotalConExencion() {
        double total = calcularTotal();
        return exencionResponsabilidad.calcularCobertura(total);
    }

    public void marcarComoPagado() {
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

    public void marcarComoEnviado() {
        if (this.estado != EstadoPedido.PAGADO) {
            throw new ReglaDominioException("Solo se puede enviar un pedido que haya sido pagado.");
        }
        this.estado = EstadoPedido.ENVIADO;
    }

    public String getIdPedido() { return idPedido; }
    public String getIdCliente() { return idCliente; }
    public String getIdVivero() { return idVivero; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public EstadoPedido getEstado() { return estado; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public ExencionResponsabilidad getExencionResponsabilidad() { return exencionResponsabilidad; }

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