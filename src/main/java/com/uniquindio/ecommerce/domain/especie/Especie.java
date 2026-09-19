package com.uniquindio.ecommerce.domain.especie;

import java.util.Objects;

public class Especie {

    private Long id;
    private String nombreComun;
    private String nombreCientifico;
    private FichaDeCuidado fichaDeCuidado;
    private Riesgo riesgo;

    public Especie(Long id, String nombreComun, String nombreCientifico,
                   FichaDeCuidado fichaDeCuidado, Riesgo riesgo) {
        this.id = id;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.fichaDeCuidado = fichaDeCuidado;
        this.riesgo = riesgo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
