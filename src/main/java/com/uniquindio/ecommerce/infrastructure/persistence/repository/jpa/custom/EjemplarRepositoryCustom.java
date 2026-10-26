package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EjemplarRepositoryCustom {
    Page<EjemplarEntity> findWithAdvancedFilters(
            String especieId,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento etapaCrecimiento,
            Boolean disponible,
            Double precioMin,
            Double precioMax,
            Pageable pageable
    );
}