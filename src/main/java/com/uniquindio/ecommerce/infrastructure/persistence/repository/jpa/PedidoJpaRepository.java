package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PedidoRepositoryCustom;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PedidoEstadisticasViveroDTO;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ViveroReporteDTO;
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

    // Estadísticas completas por vivero con JOIN y GROUP BY
    @Query("SELECT NEW com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PedidoEstadisticasViveroDTO(" +
           "v.id, v.nombre, " +
           "COUNT(p), " +
           "SUM(CASE WHEN p.estado = 'PENDIENTE_PAGO' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.estado = 'PAGADO' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.estado = 'ENVIADO' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.estado = 'RECHAZADO' THEN 1 ELSE 0 END), " +
           "SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)), " +
           "AVG((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)), " +
           "SUM(CASE WHEN p.estado = 'PENDIENTE_PAGO' THEN (SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d) ELSE 0 END)" +
           ") " +
           "FROM ViveroEntity v " +
           "LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false " +
           "WHERE v.deleted = false " +
           "GROUP BY v.id, v.nombre " +
           "ORDER BY v.nombre")
    List<PedidoEstadisticasViveroDTO> getEstadisticasCompletasPorVivero();

    // Estadísticas de pedidos delegados por vivero local (solo ENTREGADOS)
    @Query("SELECT NEW com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PedidoEstadisticasViveroDTO(" +
           "v.id, v.nombre, " +
           "COUNT(p), " +
           "0, 0, 0, " +
           "COUNT(p), " +
           "0, " +
           "SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)), " +
           "AVG((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)), " +
           "0" +
           ") " +
           "FROM ViveroEntity v " +
           "INNER JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false " +
           "WHERE v.deleted = false AND p.estado = 'ENTREGADO' " +
           "GROUP BY v.id, v.nombre " +
           "ORDER BY SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)) DESC")
    List<PedidoEstadisticasViveroDTO> getEstadisticasPedidosEntregadosPorVivero();

    // Total de ventas por vivero (solo pedidos pagados/entregados)
    @Query("SELECT v.id, v.nombre, " +
           "COUNT(p), " +
           "SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)) " +
           "FROM ViveroEntity v " +
           "LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false AND p.estado IN ('PAGADO', 'ENVIADO', 'ENTREGADO') " +
           "WHERE v.deleted = false " +
           "GROUP BY v.id, v.nombre " +
           "ORDER BY SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)) DESC")
    List<Object[]> getTotalVentasPorVivero();

    // Pedidos por estado agrupados
    @Query("SELECT p.estado, COUNT(p) FROM PedidoEntity p " +
           "WHERE p.deleted = false " +
           "GROUP BY p.estado " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> contarPedidosPorEstado();

    // Ingresos por mes (usando ID como proxy de fecha)
    @Query("SELECT SUBSTRING(p.id, 1, 7) as mes, " +
           "COUNT(p), " +
           "SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)) " +
           "FROM PedidoEntity p " +
           "WHERE p.deleted = false AND p.estado IN ('PAGADO', 'ENTREGADO') " +
           "GROUP BY SUBSTRING(p.id, 1, 7) " +
           "ORDER BY mes DESC")
    List<Object[]> getIngresosPorMes();

    // Top viveros por número de pedidos
    @Query("SELECT v.id, v.nombre, COUNT(p) as totalPedidos " +
           "FROM ViveroEntity v " +
           "LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false " +
           "WHERE v.deleted = false " +
           "GROUP BY v.id, v.nombre " +
           "ORDER BY totalPedidos DESC")
    List<Object[]> getTopViverosPorPedidos(int limit);

    // Promedio de items por pedido por vivero
    @Query("SELECT v.id, v.nombre, AVG((SELECT COUNT(d) FROM p.detalles d)) " +
           "FROM ViveroEntity v " +
           "LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false " +
           "WHERE v.deleted = false " +
           "GROUP BY v.id, v.nombre " +
           "ORDER BY AVG((SELECT COUNT(d) FROM p.detalles d)) DESC")
    List<Object[]> getPromedioItemsPorPedidoPorVivero();
}