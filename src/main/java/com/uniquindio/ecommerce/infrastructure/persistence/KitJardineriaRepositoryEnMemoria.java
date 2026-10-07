package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KitJardineriaRepositoryEnMemoria implements KitJardineriaRepository {

    // Base de datos simulada en memoria
    private final Map<String, KitJardineria> baseDeDatos = new HashMap<>();

    @Override
    public Optional<KitJardineria> obtenerKitPorIdentificador(String id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public void almacenarKit(KitJardineria kit) {
        baseDeDatos.put(kit.getIdKit(), kit);
    }
}