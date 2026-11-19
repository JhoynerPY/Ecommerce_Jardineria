package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import java.util.List;
import java.util.Optional;

public interface KitJardineriaRepository {
    Optional<KitJardineria> obtenerKitPorIdentificador(String id);
    void almacenarKit(KitJardineria kit);
    List<KitJardineria> obtenerKits();
}