package com.uniquindio.ecommerce.application;

import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.domain.ejemplar.EjemplarRepository;

import java.util.List;

public class ConsultarEjemplaresDisponibles {

    private final EjemplarRepository ejemplarRepository;

    public ConsultarEjemplaresDisponibles(EjemplarRepository ejemplarRepository){
        this.ejemplarRepository = ejemplarRepository;
    }

    public List<Ejemplar> ejecutar(){
        return null;
    }
}
