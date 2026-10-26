package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ArticuloKitRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
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

    // Búsqueda por rango de precio
    @Query("SELECT a FROM ArticuloKitEntity a WHERE a.deleted = false " +
           "AND a.precio.valor BETWEEN :precioMin AND :precioMax " +
           "ORDER BY a.precio.valor ASC")
    Page<ArticuloKitEntity> buscarPorRangoPrecio(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax, Pageable pageable);

    // Búsqueda por nombre y rango de precio combinados
    @Query("SELECT a FROM ArticuloKitEntity a WHERE a.deleted = false " +
           "AND (:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:precioMin IS NULL OR a.precio.valor >= :precioMin) " +
           "AND (:precioMax IS NULL OR a.precio.valor <= :precioMax) " +
           "ORDER BY a.precio.valor ASC")
    Page<ArticuloKitEntity> buscarPorNombreYPrecio(@Param("nombre") String nombre,
                                                    @Param("precioMin") Double precioMin,
                                                    @Param("precioMax") Double precioMax,
                                                    Pageable pageable);

    // Estadísticas de precios
    @Query("SELECT AVG(a.precio.valor), MIN(a.precio.valor), MAX(a.precio.valor) FROM ArticuloKitEntity a WHERE a.deleted = false")
    Object[] getEstadisticasPrecios();

    // Conteo por rango de precio
    @Query("SELECT COUNT(a) FROM ArticuloKitEntity a WHERE a.deleted = false AND a.precio.valor BETWEEN :min AND :max")
    long contarPorRangoPrecio(@Param("min") Double min, @Param("max") Double max);
}