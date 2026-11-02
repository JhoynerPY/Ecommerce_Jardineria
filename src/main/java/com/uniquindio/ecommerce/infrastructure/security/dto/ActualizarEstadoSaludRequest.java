package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarEstadoSaludRequest {

    @NotBlank(message = "El ID del ejemplar es obligatorio")
    private String idEjemplar;

    @NotBlank(message = "El nuevo estado es obligatorio (SANO, MARCHITO, CON_PLAGA, etc.)")
    private String nuevoEstado;

    public String getIdEjemplar() {
        return idEjemplar; }

    public void setIdEjemplar(String idEjemplar) {
        this.idEjemplar = idEjemplar; }

    public String getNuevoEstado() {
        return nuevoEstado; }

    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado; }

}
