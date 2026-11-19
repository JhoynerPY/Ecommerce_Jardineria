package com.uniquindio.ecommerce.infrastructure.security.dto;

import java.util.List;

public class KitResponse {

    private String idKit;
    private String nombreKit;
    private String idCliente;
    private List<ArticuloKitResponse> articulos;
    private double subtotal;
    private double descuento;
    private double precioTotal;

    public KitResponse() {
    }

    public KitResponse(String idKit,
                       String nombreKit,
                       String idCliente,
                       List<ArticuloKitResponse> articulos,
                       double subtotal,
                       double descuento,
                       double precioTotal) {
        this.idKit = idKit;
        this.nombreKit = nombreKit;
        this.idCliente = idCliente;
        this.articulos = articulos;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.precioTotal = precioTotal;
    }

    public String getIdKit() {
        return idKit; }

    public void setIdKit(String idKit) {
        this.idKit = idKit; }

    public String getNombreKit() {
        return nombreKit; }

    public void setNombreKit(String nombreKit) {
        this.nombreKit = nombreKit; }

    public String getIdCliente() {
        return idCliente; }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente; }

    public List<ArticuloKitResponse> getArticulos() {
        return articulos; }

    public void setArticulos(List<ArticuloKitResponse> articulos) {
        this.articulos = articulos; }

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