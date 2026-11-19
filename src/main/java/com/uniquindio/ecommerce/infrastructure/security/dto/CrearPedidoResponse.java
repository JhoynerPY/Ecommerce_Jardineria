package com.uniquindio.ecommerce.infrastructure.security.dto;

public class CrearPedidoResponse {

    private String idPedido;
    private String estado;
    private double total;
    private String mensaje;
    private String idVivero;
    private String nombreVivero;

    public CrearPedidoResponse(String idPedido, String estado, double total, String mensaje,
                               String idVivero, String nombreVivero) {
        this.idPedido = idPedido;
        this.estado = estado;
        this.total = total;
        this.mensaje = mensaje;
        this.idVivero = idVivero;
        this.nombreVivero = nombreVivero;
    }

    public String getIdPedido() {
        return idPedido; }

    public String getEstado() {
        return estado; }

    public double getTotal() {
        return total; }

    public String getMensaje() {
        return mensaje; }

    public String getIdVivero() {
        return idVivero; }

    public String getNombreVivero() {
        return nombreVivero; }

}
