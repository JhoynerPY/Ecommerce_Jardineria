package com.uniquindio.ecommerce.application;

import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.domain.ejemplar.EjemplarRepository;

import java.util.List;

public class ConsultarEjemplaresDisponibles {

    private final EjemplarRepository ejemplarRepository;

    public ConsultarEjemplaresDisponibles(EjemplarRepository ejemplarRepository){
        this.ejemplarRepository = ejemplarRepository;
    }

    //Verifica, guarda y devuelve una lista con solo con los ejemplares disponibles para la venta
    //Pasados por metodo estaDisponibleParaVenta
    public List<Ejemplar> ejecutar(){
        return ejemplarRepository.obtenerTodos().stream().filter(Ejemplar::estaDisponibleParaVenta).toList();
    }

    
}
