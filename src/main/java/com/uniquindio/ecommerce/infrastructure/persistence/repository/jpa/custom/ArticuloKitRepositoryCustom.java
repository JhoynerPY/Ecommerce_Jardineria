package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticuloKitRepositoryCustom {
    Page<ArticuloKitEntity> findWithAdvancedFilters(
            String nombre,
            Double minDiametro,
            Double maxDiametro,
            Double precioMin,
            Double precioMax,
            Pageable pageable
    );
}