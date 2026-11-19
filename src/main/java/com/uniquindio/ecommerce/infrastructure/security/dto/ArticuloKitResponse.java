package com.uniquindio.ecommerce.infrastructure.security.dto;

public class ArticuloKitResponse {

    private String id;
    private String nombre;
    private double precio;
    private String moneda;
    private double diametro;
    private int cantidad;

    public ArticuloKitResponse() {
    }

    public ArticuloKitResponse(String id, String nombre, double precio, String moneda,
                               double diametro, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.moneda = moneda;
        this.diametro = diametro;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id; }

    public void setId(String id) {
        this.id = id; }

    public String getNombre() {
        return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre; }

    public double getPrecio() {
        return precio; }

    public void setPrecio(double precio) {
        this.precio = precio; }

    public String getMoneda() {
        return moneda; }

    public void setMoneda(String moneda) {
        this.moneda = moneda; }

    public double getDiametro() {
        return diametro; }

    public void setDiametro(double diametro) {
        this.diametro = diametro; }

    public int getCantidad() {
        return cantidad; }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad; }
}