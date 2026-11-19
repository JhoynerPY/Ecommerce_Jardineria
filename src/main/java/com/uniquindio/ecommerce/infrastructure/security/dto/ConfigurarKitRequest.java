package com.uniquindio.ecommerce.infrastructure.security.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ConfigurarKitRequest {

    private String idPlanta;

    private String idMaceta;

    private String idSustrato;

    @Size(max = 80, message = "El nombre del kit no puede superar los 80 caracteres")
    private String nombreKit;

    @Valid
    private List<ArticuloKitRequest> articulos;

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

    public List<ArticuloKitRequest> getArticulos() {
        return articulos; }

    public void setArticulos(List<ArticuloKitRequest> articulos) {
        this.articulos = articulos; }

    public static class ArticuloKitRequest {

        @NotBlank(message = "El ID del ArticuloKit es obligatorio")
        private String articuloId;

        @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
        private int cantidad;

        public String getArticuloId() {
            return articuloId; }

        public void setArticuloId(String articuloId) {
            this.articuloId = articuloId; }

        public int getCantidad() {
            return cantidad; }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad; }
    }
}