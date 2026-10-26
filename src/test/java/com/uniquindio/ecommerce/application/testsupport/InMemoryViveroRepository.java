package com.uniquindio.ecommerce.application.testsupport;

import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.domain.common.Direccion;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryViveroRepository implements ViveroRepositoryPort {

    private final Map<String, Vivero> viveros = new ConcurrentHashMap<>();

    @Override
    public Optional<Vivero> buscarPorId(String id) {
        return Optional.ofNullable(viveros.get(id));
    }

    @Override
    public List<Vivero> buscarActivos() {
        return viveros.values().stream()
            .filter(Vivero::isActivo)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Vivero> buscarViveroMasCercano(double latitud, double longitud) {
        Direccion puntoRef = Direccion.de("Ref", "Ref", "Ref", "00000", "Ref", latitud, longitud);
        return viveros.values().stream()
            .filter(Vivero::isActivo)
            .filter(v -> {
                Direccion dir = v.getDireccion();
                return dir != null && dir.distanciaA(puntoRef) <= v.getRadioCoberturaKm();
            })
            .min((v1, v2) -> Double.compare(
                v1.getDireccion().distanciaA(puntoRef),
                v2.getDireccion().distanciaA(puntoRef)));
    }

    @Override
    public void guardar(Vivero vivero) {
        viveros.put(vivero.getId(), vivero);
    }

    public void limpiar() {
        viveros.clear();
    }
}