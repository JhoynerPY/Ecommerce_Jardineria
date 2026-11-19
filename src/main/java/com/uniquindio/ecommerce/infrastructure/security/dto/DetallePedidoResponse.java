package com.uniquindio.ecommerce.infrastructure.security.dto;

public class DetallePedidoResponse {

    private String idProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetallePedidoResponse() {
    }

    public DetallePedidoResponse(String idProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getIdProducto() {
        return idProducto; }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto; }

    public int getCantidad() {
        return cantidad; }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad; }

    public double getPrecioUnitario() {
        return precioUnitario; }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario; }

    public double getSubtotal() {
        return subtotal; }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal; }
}