package com.uniquindio.ecommerce.domain.ejemplar;

import com.uniquindio.ecommerce.domain.especie.Especie;

import java.util.Objects;

public class Ejemplar {

    private Long id;
    private Especie especie;
    private EtapaCrecimiento etapaCrecimiento;
    private double precio;
    private boolean disponibilidad;

    public Ejemplar(Long id, Especie especie, EtapaCrecimiento etapaCrecimiento,
                    double precio, boolean disponibilidad) {
        this.id = id;
        this.especie = especie;
        this.etapaCrecimiento = etapaCrecimiento;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean estaDisponibleParaVenta(){
        return disponibilidad;
    }

    //Compara los objetos para saber si son iguales por su id
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ejemplar ejemplar = (Ejemplar) o;
        return Objects.equals(id, ejemplar.id);
    }

    //Genera un numero entero que representa el objeto
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
