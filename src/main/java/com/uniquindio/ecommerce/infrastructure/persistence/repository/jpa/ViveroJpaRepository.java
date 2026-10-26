package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ViveroRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ViveroJpaRepository extends JpaRepository<ViveroEntity, String>, ViveroRepositoryCustom {

    Optional<ViveroEntity> findByIdAndDeletedFalse(String id);

    List<ViveroEntity> findByActivoTrueAndDeletedFalse();

    @Query("SELECT v FROM ViveroEntity v WHERE v.deleted = false AND v.activo = true " +
           "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(v.direccion.latitud)) * " +
           "cos(radians(v.direccion.longitud) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(v.direccion.latitud))))")
    List<ViveroEntity> findViverosCercanos(@Param("lat") double lat, @Param("lon") double lon);

    @Query("SELECT v FROM ViveroEntity v WHERE v.deleted = false AND v.activo = true " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(v.direccion.latitud)) * " +
           "cos(radians(v.direccion.longitud) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(v.direccion.latitud)))) <= v.radioCoberturaKm " +
           "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(v.direccion.latitud)) * " +
           "cos(radians(v.direccion.longitud) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(v.direccion.latitud))))")
    List<ViveroEntity> findViverosCubriendoDireccion(@Param("lat") double lat, @Param("lon") double lon);

    @Query("SELECT v FROM ViveroEntity v WHERE v.deleted = false " +
           "AND (:nombre IS NULL OR LOWER(v.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:activo IS NULL OR v.activo = :activo)")
    Page<ViveroEntity> findWithFilters(@Param("nombre") String nombre,
                                        @Param("activo") Boolean activo,
                                        Pageable pageable);

    // Estadísticas de viveros con conteo de clientes asignados
    @Query("SELECT v.id, v.nombre, v.ciudad, COUNT(c) as totalClientes " +
           "FROM ViveroEntity v " +
           "LEFT JOIN ClienteEntity c ON c.viveroAsignadoId = v.id AND c.deleted = false " +
           "WHERE v.deleted = false " +
           "GROUP BY v.id, v.nombre, v.ciudad " +
           "ORDER BY totalClientes DESC")
    List<Object[]> getViverosConClientesAsignados();

    // Viveros con radio de cobertura mayor a un valor
    @Query("SELECT v FROM ViveroEntity v WHERE v.deleted = false AND v.activo = true AND v.radioCoberturaKm >= :radioMin " +
           "ORDER BY v.radioCoberturaKm DESC")
    List<ViveroEntity> findByRadioCoberturaMin(@Param("radioMin") double radioMin);

    // Promedio de radio de cobertura
    @Query("SELECT AVG(v.radioCoberturaKm) FROM ViveroEntity v WHERE v.deleted = false AND v.activo = true")
    Double getPromedioRadioCobertura();
}