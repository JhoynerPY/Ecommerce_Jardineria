package com.uniquindio.ecommerce.infrastructure.security.dto;

import com.uniquindio.ecommerce.domain.pedido.ExencionResponsabilidad;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CrearPedidoRequest {

    private String idCliente;

    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    private List<@Valid DetallePedidoRequest> detalles;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private ExencionResponsabilidad.TipoExencion tipoExencion;

    @DecimalMin(value = "1.0", message = "El porcentaje de exención debe ser mayor o igual a 1")
    @DecimalMax(value = "100.0", message = "El porcentaje de exención debe ser menor o igual a 100")
    private Double porcentajeExencion;

    private String terminosExencion;

    private String versionExencion;

    private boolean exencionAceptada;

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

    public ExencionResponsabilidad.TipoExencion getTipoExencion() {
        return tipoExencion; }

    public void setTipoExencion(ExencionResponsabilidad.TipoExencion tipoExencion) {
        this.tipoExencion = tipoExencion; }

    public Double getPorcentajeExencion() {
        return porcentajeExencion; }

    public void setPorcentajeExencion(Double porcentajeExencion) {
        this.porcentajeExencion = porcentajeExencion; }

    public String getTerminosExencion() {
        return terminosExencion; }

    public void setTerminosExencion(String terminosExencion) {
        this.terminosExencion = terminosExencion; }

    public String getVersionExencion() {
        return versionExencion; }

    public void setVersionExencion(String versionExencion) {
        this.versionExencion = versionExencion; }

    public boolean isExencionAceptada() {
        return exencionAceptada; }

    public void setExencionAceptada(boolean exencionAceptada) {
        this.exencionAceptada = exencionAceptada; }

    @AssertTrue(message = "El porcentaje de exención debe estar entre 1 y 100 para el acuerdo parcial")
    public boolean isPorcentajeExencionValido() {
        if (tipoExencion != ExencionResponsabilidad.TipoExencion.ACUERDO_PARCIAL_PERDIDA) {
            return true;
        }
        return porcentajeExencion != null && porcentajeExencion >= 1 && porcentajeExencion <= 100;
    }

    @AssertTrue(message = "Debe aceptar los términos de la exención de responsabilidad")
    public boolean isExencionAceptadaValida() {
        if (tipoExencion == null || tipoExencion == ExencionResponsabilidad.TipoExencion.SIN_EXENCION) {
            return true;
        }
        return exencionAceptada;
    }

    // Clase interna para los detalles
    public static class DetallePedidoRequest {

        @NotBlank(message = "El ID del ejemplar es obligatorio")
        private String idProducto;

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private int cantidad;

        @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
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
