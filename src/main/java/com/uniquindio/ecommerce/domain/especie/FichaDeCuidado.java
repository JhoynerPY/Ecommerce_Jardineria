package com.uniquindio.ecommerce.domain.especie;

import java.util.Objects;

//Ficha tipo guia que tendra cada ejemplar con recomendaciones para su cuidado
public record FichaDeCuidado(
        RequerimientoLuz requerimientoLuz,
        String frecuenciaRiego,
        String sustrato, //Tipo de tierra que debe usar el Ejemplar
        String temporadaFloracion) {

    public FichaDeCuidado{
        Objects.requireNonNull(requerimientoLuz, "El requerimiento de luz no puede estar nulo");
        Objects.requireNonNull(frecuenciaRiego, "La frecuencia de riesgo no puede estar nula");
        Objects.requireNonNull(sustrato, "El sustrato no puede estar nulo");
        Objects.requireNonNull(temporadaFloracion, "La temporada de floración no puede estar nula");

        if (frecuenciaRiego.isBlank()){
            throw new IllegalArgumentException("La frecuencia de riesgo no puede estar vacia");
        }
        if (sustrato.isBlank()){
            throw new IllegalArgumentException("El sustrato no puede estar vacío");
        }
        if (temporadaFloracion.isBlank()){
            throw new IllegalArgumentException("La temporada de floración no puede estar vacía");
        }

    }



}
