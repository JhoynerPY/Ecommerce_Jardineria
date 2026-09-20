package com.uniquindio.ecommerce.domain.kit;

public class ArticuloKit {
    private String id;
    private String nombre;
    private double precio;
    private double diametro; // Útil para validar compatibilidad entre planta y maceta

    public ArticuloKit(String id, String nombre, double precio, double diametro) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.diametro = diametro;
    }

    public double getPrecio() { return precio; }
    public double getDiametro() { return diametro; }
}