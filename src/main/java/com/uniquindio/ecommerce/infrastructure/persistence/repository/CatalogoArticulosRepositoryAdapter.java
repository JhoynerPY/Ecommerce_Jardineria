package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.CatalogoArticulosPort;
import com.uniquindio.ecommerce.domain.kit.ArticuloKit;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ArticuloKitJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class CatalogoArticulosRepositoryAdapter implements CatalogoArticulosPort {

    private final ArticuloKitJpaRepository jpaRepository;

    public CatalogoArticulosRepositoryAdapter(ArticuloKitJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ArticuloKit obtenerArticulo(String idArticulo) {
        return jpaRepository.findByIdAndDeletedFalse(idArticulo)
                .map(JpaMapper::toDomain)
                .orElse(null);
    }
}