package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfigurarKitRequest {

    @NotBlank(message = "El ID de la planta es obligatorio")
    private String idPlanta;

    @NotBlank(message = "El ID de la maceta es obligatorio")
    private String idMaceta;

    @NotBlank(message = "El ID del sustrato es obligatorio")
    private String idSustrato;

    @Size(max = 80, message = "El nombre del kit no puede superar los 80 caracteres")
    private String nombreKit;

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

    public String getNombreKit() {
        return nombreKit; }

    public void setNombreKit(String nombreKit) {
        this.nombreKit = nombreKit; }
}