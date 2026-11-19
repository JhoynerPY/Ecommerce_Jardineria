package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.EspecieRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EspecieJpaRepository extends JpaRepository<EspecieEntity, String>, EspecieRepositoryCustom {

    Optional<EspecieEntity> findByIdAndDeletedFalse(String id);

    List<EspecieEntity> findByDeletedFalse();

    @Query("SELECT e FROM EspecieEntity e WHERE e.deleted = false " +
           "AND (:nombreComun IS NULL OR LOWER(e.nombreComun) LIKE LOWER(CONCAT('%', :nombreComun, '%'))) " +
           "AND (:nombreCientifico IS NULL OR LOWER(e.nombreCientifico) LIKE LOWER(CONCAT('%', :nombreCientifico, '%'))) " +
           "AND (:nivelRiesgo IS NULL OR e.riesgo.nivel = :nivelRiesgo)")
    Page<EspecieEntity> findWithFilters(@Param("nombreComun") String nombreComun,
                                         @Param("nombreCientifico") String nombreCientifico,
                                         @Param("nivelRiesgo") com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo nivelRiesgo,
                                         Pageable pageable);

    // Búsqueda por requerimiento de luz
    @Query("SELECT e FROM EspecieEntity e WHERE e.deleted = false " +
           "AND e.fichaDeCuidado.requerimientoLuz = :requerimientoLuz " +
           "ORDER BY e.nombreComun ASC")
    List<EspecieEntity> findByRequerimientoLuz(@Param("requerimientoLuz") com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz requerimientoLuz);

    // Conteo por nivel de riesgo
    @Query("SELECT e.riesgo.nivel, COUNT(e) FROM EspecieEntity e WHERE e.deleted = false " +
           "GROUP BY e.riesgo.nivel " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> contarPorNivelRiesgo();

    // Conteo por requerimiento de luz
    @Query("SELECT e.fichaDeCuidado.requerimientoLuz, COUNT(e) FROM EspecieEntity e WHERE e.deleted = false " +
           "GROUP BY e.fichaDeCuidado.requerimientoLuz " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> contarPorRequerimientoLuz();

    // Especies tóxicas (nivel riesgo != NINGUNO)
    @Query("SELECT e FROM EspecieEntity e WHERE e.deleted = false AND e.riesgo.nivel != 'NINGUNO' " +
           "ORDER BY e.riesgo.nivel DESC, e.nombreComun ASC")
    List<EspecieEntity> findEspeciesToxicas();
}