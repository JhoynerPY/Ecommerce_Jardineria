package com.uniquindio.ecommerce.domain.ejemplar;

import java.util.List;

//Interfaz temporal para trabajar lista de Ejemplar sin base de datos
public interface EjemplarRepository {
    List<Ejemplar> obtenerTodos();
}
