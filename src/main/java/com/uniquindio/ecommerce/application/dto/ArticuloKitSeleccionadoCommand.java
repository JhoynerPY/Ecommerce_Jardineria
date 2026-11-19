package com.uniquindio.ecommerce.application.dto;

public class ArticuloKitSeleccionadoCommand {

    private final String articuloId;
    private final int cantidad;

    public ArticuloKitSeleccionadoCommand(String articuloId, int cantidad) {
        this.articuloId = articuloId;
        this.cantidad = cantidad;
    }

    public String getArticuloId() {
        return articuloId;
    }

    public int getCantidad() {
        return cantidad;
    }
}