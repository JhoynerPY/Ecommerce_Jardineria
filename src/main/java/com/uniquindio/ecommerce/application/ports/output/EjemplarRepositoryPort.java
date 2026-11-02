package com.uniquindio.ecommerce.application.ports.output;

import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;

import java.util.Optional;

public interface EjemplarRepositoryPort {
    Optional<Ejemplar> buscarPorId(String id);
    Ejemplar guardar(Ejemplar ejemplar);
}
