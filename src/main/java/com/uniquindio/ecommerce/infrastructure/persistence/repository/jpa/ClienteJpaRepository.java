package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ClienteRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String>, ClienteRepositoryCustom {

    Optional<ClienteEntity> findByIdAndDeletedFalse(String id);

    Optional<ClienteEntity> findByEmail_ValorAndDeletedFalse(String email);

    boolean existsByEmail_ValorAndDeletedFalse(String email);

    @Query("SELECT c FROM ClienteEntity c WHERE c.deleted = false " +
           "AND (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:email IS NULL OR LOWER(c.email.valor) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:viveroAsignadoId IS NULL OR c.viveroAsignadoId = :viveroAsignadoId)")
    Page<ClienteEntity> findWithFilters(@Param("nombre") String nombre,
                                         @Param("email") String email,
                                         @Param("viveroAsignadoId") String viveroAsignadoId,
                                         Pageable pageable);

    // Búsqueda por ciudad
    @Query("SELECT c FROM ClienteEntity c WHERE c.deleted = false " +
           "AND LOWER(c.direccion.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')) " +
           "ORDER BY c.nombre ASC")
    Page<ClienteEntity> buscarPorCiudad(@Param("ciudad") String ciudad, Pageable pageable);

    // Clientes por vivero asignado con estadísticas
    @Query("SELECT c.viveroAsignadoId, v.nombre, COUNT(c) " +
           "FROM ClienteEntity c " +
           "LEFT JOIN ViveroEntity v ON v.id = c.viveroAsignadoId " +
           "WHERE c.deleted = false AND c.viveroAsignadoId IS NOT NULL " +
           "GROUP BY c.viveroAsignadoId, v.nombre " +
           "ORDER BY COUNT(c) DESC")
    List<Object[]> getClientesPorVivero();

    // Total de clientes
    @Query("SELECT COUNT(c) FROM ClienteEntity c WHERE c.deleted = false")
    long countTotalClientes();

    // Clientes con vivero asignado
    @Query("SELECT COUNT(c) FROM ClienteEntity c WHERE c.deleted = false AND c.viveroAsignadoId IS NOT NULL")
    long countClientesConViveroAsignado();
}