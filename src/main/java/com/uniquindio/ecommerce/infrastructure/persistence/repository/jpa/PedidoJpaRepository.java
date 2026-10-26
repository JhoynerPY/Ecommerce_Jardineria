package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PedidoRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, String>, PedidoRepositoryCustom {

    Optional<PedidoEntity> findByIdAndDeletedFalse(String id);

    List<PedidoEntity> findByIdClienteAndDeletedFalse(String idCliente);

    List<PedidoEntity> findByIdViveroAndDeletedFalse(String idVivero);

    List<PedidoEntity> findByEstadoAndDeletedFalse(EstadoPedido estado);

    @Query("SELECT p FROM PedidoEntity p WHERE p.deleted = false " +
           "AND (:idCliente IS NULL OR p.idCliente = :idCliente) " +
           "AND (:idVivero IS NULL OR p.idVivero = :idVivero) " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:fechaDesde IS NULL OR p.id >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR p.id <= :fechaHasta)")
    Page<PedidoEntity> findWithFilters(@Param("idCliente") String idCliente,
                                        @Param("idVivero") String idVivero,
                                        @Param("estado") EstadoPedido estado,
                                        @Param("fechaDesde") String fechaDesde,
                                        @Param("fechaHasta") String fechaHasta,
                                        Pageable pageable);
}