package com.uniquindio.ecommerce.application.dto;

public class ActualizarEstadoSaludCommand {

    private final String idEjemplar;
    private final String nuevoEstado;

    public ActualizarEstadoSaludCommand(String idEjemplar, String nuevoEstado) {
        this.idEjemplar = idEjemplar;
        this.nuevoEstado = nuevoEstado;
    }

    public String getIdEjemplar() {
        return idEjemplar;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }
}
