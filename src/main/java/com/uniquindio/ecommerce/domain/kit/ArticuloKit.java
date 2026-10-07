package com.uniquindio.ecommerce.domain.kit;

import java.util.Objects;

public class ArticuloKit {
    private String id;
    private String nombre;
    private Precio precio;
    private double diametro; // Útil para validar compatibilidad entre planta y maceta

    public ArticuloKit(String id, String nombre, Precio precio, double diametro) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.diametro = diametro;
    }

    public String getId() {
        return id;
    }

    public double getDiametro() { return diametro; }

    // Entidad: Igualdad por IDENTIDAD (solo importa el ID)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticuloKit que = (ArticuloKit) o;
        return Objects.equals(id, que.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Precio getPrecio() { return precio; }
}