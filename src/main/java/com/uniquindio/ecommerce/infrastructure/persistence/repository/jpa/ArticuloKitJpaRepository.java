package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ArticuloKitRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ArticuloKitJpaRepository extends JpaRepository<ArticuloKitEntity, String>, ArticuloKitRepositoryCustom {

    Optional<ArticuloKitEntity> findByIdAndDeletedFalse(String id);

    @Query("SELECT a FROM ArticuloKitEntity a WHERE a.deleted = false " +
           "AND (:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:minDiametro IS NULL OR a.diametro >= :minDiametro) " +
           "AND (:maxDiametro IS NULL OR a.diametro <= :maxDiametro)")
    Page<ArticuloKitEntity> findWithFilters(@Param("nombre") String nombre,
                                             @Param("minDiametro") Double minDiametro,
                                             @Param("maxDiametro") Double maxDiametro,
                                             Pageable pageable);
}