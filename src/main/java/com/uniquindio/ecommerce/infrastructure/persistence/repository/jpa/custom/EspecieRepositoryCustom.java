package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EspecieRepositoryCustom {
    Page<EspecieEntity> findWithAdvancedFilters(
            String nombreComun,
            String nombreCientifico,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo nivelRiesgo,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz requerimientoLuz,
            Pageable pageable
    );
}