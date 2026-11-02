package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CrearPedidoRequest {

    @NotBlank(message = "El ID del cliente es obligatorio")
    private String idCliente;

    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    private List<DetallePedidoRequest> detalles;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    public String getIdCliente() {
        return idCliente; }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente; }

    public List<DetallePedidoRequest> getDetalles() {
        return detalles; }

    public void setDetalles(List<DetallePedidoRequest> detalles) {
        this.detalles = detalles; }

    public String getDireccion() {
        return direccion; }

    public void setDireccion(String direccion) {
        this.direccion = direccion; }

    // Clase interna para los detalles
    public static class DetallePedidoRequest {

        @NotBlank private String idProducto;
        private int cantidad;
        private double precioUnitario;

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
    }
}
