package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.CarritoComprasPort;
import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CarritoComprasRepositoryAdapter implements CarritoComprasPort {

    private final Map<String, KitJardineria> carritos = new ConcurrentHashMap<>();

    @Override
    public void agregarKit(String idCliente, KitJardineria kit) {
        carritos.put(idCliente + ":" + kit.getIdKit(), kit);
    }
}