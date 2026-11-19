package com.uniquindio.ecommerce.domain.especie;

import java.util.Objects;

public class Especie {

    private final String id;
    private final String nombreComun;
    private final String nombreCientifico;
    private FichaDeCuidado fichaDeCuidado;
    private final Riesgo riesgo;

    private Especie(String id, String nombreComun, String nombreCientifico,
                   FichaDeCuidado fichaDeCuidado, Riesgo riesgo) {
        this.id = id;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.fichaDeCuidado = fichaDeCuidado;
        this.riesgo = riesgo;
    }

    public static Especie agregarAlCatalogo(String id, String nombreComun, String nombreCientifico,
                                            FichaDeCuidado fichaDeCuidado, Riesgo riesgo){

        return new Especie(id, nombreComun, nombreCientifico, fichaDeCuidado, riesgo);
    }

    public String getId() {
        return id;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public FichaDeCuidado getFichaDeCuidado() {
        return fichaDeCuidado;
    }

    public Riesgo getRiesgo() {
        return riesgo;
    }

    public void actualizarFichaDeCuidado (FichaDeCuidado nuevaFicha){
        this.fichaDeCuidado = nuevaFicha;
    }

    //Compara los objetos para saber si son iguales por su id
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Especie especie = (Especie) o;
        return Objects.equals(id, especie.id);
    }

    //Genera un numero entero que representa el objeto
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
