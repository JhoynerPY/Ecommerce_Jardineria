package com.uniquindio.ecommerce.infrastructure.security.dto;

public class KitResponse {

    private String idKit;
    private ArticuloKitResponse planta;
    private ArticuloKitResponse maceta;
    private ArticuloKitResponse sustrato;
    private double subtotal;
    private double descuento;
    private double precioTotal;

    public KitResponse() {
    }

    public KitResponse(String idKit,
                       ArticuloKitResponse planta,
                       ArticuloKitResponse maceta,
                       ArticuloKitResponse sustrato,
                       double subtotal,
                       double descuento,
                       double precioTotal) {
        this.idKit = idKit;
        this.planta = planta;
        this.maceta = maceta;
        this.sustrato = sustrato;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.precioTotal = precioTotal;
    }

    public String getIdKit() {
        return idKit; }

    public void setIdKit(String idKit) {
        this.idKit = idKit; }

    public ArticuloKitResponse getPlanta() {
        return planta; }

    public void setPlanta(ArticuloKitResponse planta) {
        this.planta = planta; }

    public ArticuloKitResponse getMaceta() {
        return maceta; }

    public void setMaceta(ArticuloKitResponse maceta) {
        this.maceta = maceta; }

    public ArticuloKitResponse getSustrato() {
        return sustrato; }

    public void setSustrato(ArticuloKitResponse sustrato) {
        this.sustrato = sustrato; }

    public double getSubtotal() {
        return subtotal; }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal; }

    public double getDescuento() {
        return descuento; }

    public void setDescuento(double descuento) {
        this.descuento = descuento; }

    public double getPrecioTotal() {
        return precioTotal; }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal; }
}