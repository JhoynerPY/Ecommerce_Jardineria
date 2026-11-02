package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelarPedidoRequest {

    @NotBlank(message = "El ID del pedido es obligatorio")
    private String idpedido;

    @NotBlank(message = "El motivo de cancelacion es obligatorio")
    private String motivoCancelacion;

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
