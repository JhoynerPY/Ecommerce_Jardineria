package com.uniquindio.ecommerce.application.dto;

public class ProcesarCompraCommand {
    private final String idPedido;
    private final String metodoPago;

    public ProcesarCompraCommand(String idPedido, String metodoPago) {
        this.idPedido = idPedido;
        this.metodoPago = metodoPago;
    }

    public String getIdPedido() { return idPedido; }
    public String getMetodoPago() { return metodoPago; }
}
