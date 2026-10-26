package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.EjemplarRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EjemplarJpaRepository extends JpaRepository<EjemplarEntity, String>, EjemplarRepositoryCustom {

    Optional<EjemplarEntity> findByIdAndDeletedFalse(String id);

    List<EjemplarEntity> findByDisponibilidadTrueAndDeletedFalse();

    List<EjemplarEntity> findByEspecieAndDeletedFalse(EspecieEntity especie);

    List<EjemplarEntity> findByEspecieIdAndDisponibilidadTrueAndDeletedFalse(String especieId);

    @Query("SELECT e FROM EjemplarEntity e WHERE e.deleted = false " +
           "AND (:especieId IS NULL OR e.especie.id = :especieId) " +
           "AND (:etapaCrecimiento IS NULL OR e.etapaCrecimiento = :etapaCrecimiento) " +
           "AND (:disponible IS NULL OR e.disponibilidad = :disponible)")
    Page<EjemplarEntity> findWithFilters(@Param("especieId") String especieId,
                                          @Param("etapaCrecimiento") com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento etapaCrecimiento,
                                          @Param("disponible") Boolean disponible,
                                          Pageable pageable);

    @Query("SELECT COUNT(e) FROM EjemplarEntity e WHERE e.deleted = false AND e.disponibilidad = true")
    long countDisponibles();
}