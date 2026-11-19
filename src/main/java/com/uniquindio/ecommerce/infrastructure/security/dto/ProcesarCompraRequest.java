package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public class ProcesarCompraRequest {

    @NotBlank
    private String idPedido;
    @NotBlank private String metodoPago;

    public String getIdPedido() {
        return idPedido; }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido; }

    public String getMetodoPago() {
        return metodoPago; }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago; }

}
