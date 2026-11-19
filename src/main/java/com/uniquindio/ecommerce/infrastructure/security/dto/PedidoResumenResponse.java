package com.uniquindio.ecommerce.infrastructure.security.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResumenResponse {

    private String id;
    private LocalDateTime fechaCreacion;
    private String estado;
    private String viveroAsignado;
    private String direccionEntrega;
    private String tipoExencion;
    private Double porcentajeAcuerdo;
    private List<DetallePedidoResponse> detalles;
    private double total;

    public PedidoResumenResponse() {
    }

    public PedidoResumenResponse(String id, LocalDateTime fechaCreacion, String estado,
                                 String viveroAsignado, String direccionEntrega,
                                 String tipoExencion, Double porcentajeAcuerdo,
                                 List<DetallePedidoResponse> detalles, double total) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.viveroAsignado = viveroAsignado;
        this.direccionEntrega = direccionEntrega;
        this.tipoExencion = tipoExencion;
        this.porcentajeAcuerdo = porcentajeAcuerdo;
        this.detalles = detalles;
        this.total = total;
    }

    public String getId() {
        return id; }

    public void setId(String id) {
        this.id = id; }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion; }

    public String getEstado() {
        return estado; }

    public void setEstado(String estado) {
        this.estado = estado; }

    public String getViveroAsignado() {
        return viveroAsignado; }

    public void setViveroAsignado(String viveroAsignado) {
        this.viveroAsignado = viveroAsignado; }

    public String getDireccionEntrega() {
        return direccionEntrega; }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega; }

    public String getTipoExencion() {
        return tipoExencion; }

    public void setTipoExencion(String tipoExencion) {
        this.tipoExencion = tipoExencion; }

    public Double getPorcentajeAcuerdo() {
        return porcentajeAcuerdo; }

    public void setPorcentajeAcuerdo(Double porcentajeAcuerdo) {
        this.porcentajeAcuerdo = porcentajeAcuerdo; }

    public List<DetallePedidoResponse> getDetalles() {
        return detalles; }

    public void setDetalles(List<DetallePedidoResponse> detalles) {
        this.detalles = detalles; }

    public double getTotal() {
        return total; }

    public void setTotal(double total) {
        this.total = total; }
}