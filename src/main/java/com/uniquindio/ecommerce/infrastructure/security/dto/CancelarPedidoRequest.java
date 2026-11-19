package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelarPedidoRequest {

    @NotBlank(message = "El ID del pedido es obligatorio")
    private String idPedido;

    @NotBlank(message = "El motivo de cancelacion es obligatorio")
    private String motivoCancelacion;

    public String getIdPedido() { return idPedido; }

    public void setIdPedido(String idPedido) { this.idPedido = idPedido; }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
