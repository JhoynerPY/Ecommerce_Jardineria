package com.uniquindio.ecommerce.application.dto;

public class CancelarPedidoCommand {

    private final String idPedido;
    private final String motivoCancelacion;

    public CancelarPedidoCommand(String idPedido, String motivoCancelacion){
        this.idPedido = idPedido;
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }
}
