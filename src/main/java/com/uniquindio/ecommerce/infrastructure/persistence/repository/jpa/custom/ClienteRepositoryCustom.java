package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepositoryCustom {
    Page<ClienteEntity> findWithAdvancedFilters(
            String nombre,
            String email,
            String viveroAsignadoId,
            String ciudad,
            Pageable pageable
    );
}