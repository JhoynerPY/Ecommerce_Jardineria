package com.uniquindio.ecommerce.infrastructure.security.dto;

public class CrearPedidoResponse {

    private String idPedido;
    private String estado;
    private double total;
    private String mensaje;

    public CrearPedidoResponse(String idPedido, String estado, double total, String mensaje) {
        this.idPedido = idPedido;
        this.estado = estado;
        this.total = total;
        this.mensaje = mensaje;
    }


    public String getIdPedido() {
        return idPedido; }

    public String getEstado() {
        return estado; }

    public double getTotal() {
        return total; }

    public String getMensaje() {
        return mensaje; }

}
