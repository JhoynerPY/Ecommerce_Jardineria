package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.KitJardineriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface KitJardineriaJpaRepository extends JpaRepository<KitJardineriaEntity, String> {

    Optional<KitJardineriaEntity> findByIdAndDeletedFalse(String id);

    @Query("SELECT k FROM KitJardineriaEntity k WHERE k.deleted = false " +
           "AND (:plantaId IS NULL OR k.planta.id = :plantaId) " +
           "AND (:macetaId IS NULL OR k.maceta.id = :macetaId) " +
           "AND (:sustratoId IS NULL OR k.sustrato.id = :sustratoId)")
    Page<KitJardineriaEntity> findWithFilters(@Param("plantaId") String plantaId,
                                               @Param("macetaId") String macetaId,
                                               @Param("sustratoId") String sustratoId,
                                               Pageable pageable);
}