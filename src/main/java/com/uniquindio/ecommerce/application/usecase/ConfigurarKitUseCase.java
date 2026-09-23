package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;

public class ConfigurarKitUseCase {

    private final KitJardineriaRepository repository;

    // Se inyecta la dependencia del repositorio
    public ConfigurarKitUseCase(KitJardineriaRepository repository) {
        this.repository = repository;
    }

    // Recibe una intención simple, invoca al dominio y guarda[cite: 3]
    public KitJardineria ejecutar(ArticuloKit planta, ArticuloKit maceta, ArticuloKit sustrato) throws Exception {
        // 1. Invoca el comportamiento del dominio (las reglas y excepciones están por dentro de esta clase)
        KitJardineria nuevoKit = new KitJardineria(planta, maceta, sustrato);

        // 2. Usa el repositorio para persistir[cite: 3]
        repository.almacenarKit(nuevoKit);

        return nuevoKit;
    }
}