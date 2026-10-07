package com.uniquindio.ecommerce.domain.especie;

import java.util.Objects;

//Clase aclarar nivel de riesgo y resultado correspondiente
public record Riesgo(NivelRiesgo nivel, String descripcion) {

    //Excepcion de valores nulos o vacios (Pendiente)

    public static Riesgo ninguno(){
        return new Riesgo(NivelRiesgo.NINGUNO, "Sin riesgo de toxicidad");
    }

    public boolean esToxico(){
        return nivel != NivelRiesgo.NINGUNO;
    }

}
