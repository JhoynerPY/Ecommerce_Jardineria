package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ViveroRepositoryCustom {
    Page<ViveroEntity> findWithAdvancedFilters(
            String nombre,
            Boolean activo,
            Double latitud,
            Double longitud,
            Double radioMaximo,
            Pageable pageable
    );

    List<ViveroEntity> findViverosCercanosConCobertura(double latitud, double longitud);
}