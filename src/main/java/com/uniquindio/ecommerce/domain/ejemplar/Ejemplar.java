package com.uniquindio.ecommerce.domain.ejemplar;

import com.uniquindio.ecommerce.domain.especie.Especie;

import java.util.Objects;

public class Ejemplar {

    private final String id;
    private final Especie especie;
    private EtapaCrecimiento etapaCrecimiento;
    private Precio precio;
    private boolean disponibilidad;

    private Ejemplar(String id, Especie especie, EtapaCrecimiento etapaCrecimiento,
                    Precio precio) {
        this.id = id;
        this.especie = especie;
        this.etapaCrecimiento = etapaCrecimiento;
        this.precio = precio;
        this.disponibilidad = true;
    }

    public static Ejemplar registrarEnInventario(String id, Especie especie, EtapaCrecimiento etapaCrecimiento,
                                                 Precio precio){
        return new Ejemplar(id, especie, etapaCrecimiento, precio);
    }

    public String getId() {
        return id;
    }

    public Especie getEspecie() {
        return especie;
    }

    public EtapaCrecimiento getEtapaCrecimiento() {
        return etapaCrecimiento;
    }

    public Precio getPrecio() {
        return precio;
    }

    public void avanzarEtapa(EtapaCrecimiento nuevaEtapa){
        this.etapaCrecimiento = nuevaEtapa;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public boolean estaDisponibleParaVenta(){
        return disponibilidad;
    }

    public void retirarDeVenta(){
        this.disponibilidad = false;
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
