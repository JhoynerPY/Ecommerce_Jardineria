package com.uniquindio.ecommerce.infrastructure.security.dto;

public class CancelarPedidoResponse {

    private String idPedido;
    private String estado;
    private String mensaje;

    public CancelarPedidoResponse(String idPedido, String estado, String mensaje) {
        this.idPedido = idPedido;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }
}
