package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ActualizarEstadoSaludRequest {

    @NotBlank(message = "El ID del ejemplar es obligatorio")
    private String idEjemplar;

    @NotBlank(message = "El nuevo estado es obligatorio (SANO, MARCHITO, CON_PLAGA, etc.)")
    @Pattern(
            regexp = "SANO|EN_RECUPERACION|CON_PLAGA|MARCHITO",
            message = "El estado de salud debe ser SANO, EN_RECUPERACION, CON_PLAGA o MARCHITO")
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
