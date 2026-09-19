package com.uniquindio.ecommerce.domain.pedido;

public class DetallePedido {
    private String idProducto;
    private int cantidad;
    private double precioUnitario;

    public DetallePedido(String idProducto, int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }

    // Getters
    public String getIdProducto() { return idProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}