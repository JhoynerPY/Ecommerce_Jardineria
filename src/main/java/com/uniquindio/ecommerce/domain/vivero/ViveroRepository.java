package com.uniquindio.ecommerce.domain.vivero;

import java.util.List;
import java.util.Optional;

public interface ViveroRepository {
    Optional<Vivero> buscarPorId(String id);
    List<Vivero> buscarActivos();
    Optional<Vivero> buscarViveroMasCercano(double latitud, double longitud);
    void almacenar(Vivero vivero);
}