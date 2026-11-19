package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfigurarKitRequest {

    @NotBlank(message = "El ID de la planta es obligatorio")
    private String idPlanta;

    @NotBlank(message = "El ID de la maceta es obligatorio")
    private String idMaceta;

    @NotBlank(message = "El ID del sustrato es obligatorio")
    private String idSustrato;

    public String getIdPlanta() {
        return idPlanta; }

    public void setIdPlanta(String idPlanta) {
        this.idPlanta = idPlanta; }

    public String getIdMaceta() {
        return idMaceta; }

    public void setIdMaceta(String idMaceta) {
        this.idMaceta = idMaceta; }

    public String getIdSustrato() {
        return idSustrato; }

    public void setIdSustrato(String idSustrato) {
        this.idSustrato = idSustrato; }
}