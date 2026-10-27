package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EjemplarJpaRepository extends JpaRepository<EjemplarEntity, String> {

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

    // Búsqueda avanzada de plantas por nombre de especie y rango de precio con paginación
    @Query("SELECT e.id, e.especie.nombreComun, e.especie.nombreCientifico, e.etapaCrecimiento, e.precio.valor, e.precio.moneda, e.disponibilidad " +
           "FROM EjemplarEntity e " +
           "JOIN e.especie esp " +
           "WHERE e.deleted = false " +
           "AND (:nombreEspecie IS NULL OR LOWER(esp.nombreComun) LIKE LOWER(CONCAT('%', :nombreEspecie, '%')) OR LOWER(esp.nombreCientifico) LIKE LOWER(CONCAT('%', :nombreEspecie, '%'))) " +
           "AND (:precioMin IS NULL OR e.precio.valor >= :precioMin) " +
           "AND (:precioMax IS NULL OR e.precio.valor <= :precioMax) " +
           "AND (:disponible IS NULL OR e.disponibilidad = :disponible) " +
           "AND (:etapaCrecimiento IS NULL OR e.etapaCrecimiento = :etapaCrecimiento) " +
           "ORDER BY esp.nombreComun ASC, e.precio.valor ASC")
    Page<Object[]> buscarPlantasPorNombreYPrecio(
            @Param("nombreEspecie") String nombreEspecie,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("disponible") Boolean disponible,
            @Param("etapaCrecimiento") com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento etapaCrecimiento,
            Pageable pageable);

    // Búsqueda simple por nombre de especie
    @Query("SELECT e FROM EjemplarEntity e " +
           "JOIN e.especie esp " +
           "WHERE e.deleted = false " +
           "AND (LOWER(esp.nombreComun) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(esp.nombreCientifico) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY esp.nombreComun ASC")
    Page<EjemplarEntity> buscarPorNombreEspecie(@Param("nombre") String nombre, Pageable pageable);

    // Búsqueda por rango de precio
    @Query("SELECT e FROM EjemplarEntity e " +
           "WHERE e.deleted = false " +
           "AND e.precio.valor BETWEEN :precioMin AND :precioMax " +
           "ORDER BY e.precio.valor ASC")
    Page<EjemplarEntity> buscarPorRangoPrecio(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax, Pageable pageable);

    // Conteo de plantas disponibles por especie
    @Query("SELECT esp.nombreComun, COUNT(e) FROM EjemplarEntity e " +
           "JOIN e.especie esp " +
           "WHERE e.deleted = false AND e.disponibilidad = true " +
           "GROUP BY esp.nombreComun " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> contarDisponiblesPorEspecie();

    // Promedio de precio por especie
    @Query("SELECT esp.nombreComun, AVG(e.precio.valor), MIN(e.precio.valor), MAX(e.precio.valor) FROM EjemplarEntity e " +
           "JOIN e.especie esp " +
           "WHERE e.deleted = false " +
           "GROUP BY esp.nombreComun " +
           "ORDER BY AVG(e.precio.valor) DESC")
    List<Object[]> estadisticasPrecioPorEspecie();
}