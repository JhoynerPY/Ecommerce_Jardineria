package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.DetallePedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DetallePedidoJpaRepository extends JpaRepository<DetallePedidoEntity, String> {

    List<DetallePedidoEntity> findByPedidoIdAndDeletedFalse(String pedidoId);

    @Query("SELECT d.idProducto, SUM(d.cantidad), SUM(d.precioUnitario * d.cantidad) " +
           "FROM DetallePedidoEntity d " +
           "JOIN d.pedido p " +
           "WHERE d.deleted = false AND p.deleted = false " +
           "AND p.estado = 'ENTREGADO' " +
           "GROUP BY d.idProducto " +
           "ORDER BY SUM(d.cantidad) DESC")
    Page<Object[]> getTopProductosVendidos(Pageable pageable);
}