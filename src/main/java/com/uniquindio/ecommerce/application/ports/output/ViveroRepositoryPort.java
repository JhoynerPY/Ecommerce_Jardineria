package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.vivero.Vivero;
import java.util.List;
import java.util.Optional;

public interface ViveroRepositoryPort {
    Optional<Vivero> buscarPorId(String id);
    List<Vivero> buscarActivos();
    Optional<Vivero> buscarViveroMasCercano(double latitud, double longitud);
    void guardar(Vivero vivero);
}
