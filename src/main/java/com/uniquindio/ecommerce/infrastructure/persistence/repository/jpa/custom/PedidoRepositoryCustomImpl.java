package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PedidoRepositoryCustomImpl implements PedidoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<PedidoEntity> findWithAdvancedFilters(
            String idCliente,
            String idVivero,
            EstadoPedido estado,
            String fechaDesde,
            String fechaHasta,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PedidoEntity> query = cb.createQuery(PedidoEntity.class);
        Root<PedidoEntity> root = query.from(PedidoEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (idCliente != null && !idCliente.isBlank()) {
            predicates.add(cb.equal(root.get("idCliente"), idCliente));
        }
        if (idVivero != null && !idVivero.isBlank()) {
            predicates.add(cb.equal(root.get("idVivero"), idVivero));
        }
        if (estado != null) {
            predicates.add(cb.equal(root.get("estado"), estado));
        }
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("id"), fechaDesde));
        }
        if (fechaHasta != null && !fechaHasta.isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(root.get("id"), fechaHasta));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("id")));

        TypedQuery<PedidoEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<PedidoEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PedidoEntity> countRoot = countQuery.from(PedidoEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<ViveroReporteDTO> getReportePorVivero() {
        String jpql = "SELECT NEW com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.ViveroReporteDTO(" +
                "v.id, v.nombre, " +
                "COUNT(p), " +
                "SUM(CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END), " +
                "AVG((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)), " +
                "SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d))" +
                ") " +
                "FROM ViveroEntity v " +
                "LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false " +
                "WHERE v.deleted = false " +
                "GROUP BY v.id, v.nombre " +
                "ORDER BY v.nombre";

        return entityManager.createQuery(jpql, ViveroReporteDTO.class).getResultList();
    }

    @Override
    public List<TopProductoDTO> getTopProductosVendidos(int limit) {
        String jpql = "SELECT NEW com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.TopProductoDTO(" +
                "d.idProducto, " +
                "CASE " +
                "   WHEN d.idProducto LIKE 'ejemplar-%' THEN (SELECT e.especie.nombreComun FROM EjemplarEntity e WHERE e.id = d.idProducto) " +
                "   WHEN d.idProducto LIKE 'kit-%' THEN (SELECT CONCAT('Kit: ', k.planta.nombre) FROM KitJardineriaEntity k WHERE k.id = d.idProducto) " +
                "   ELSE d.idProducto " +
                "END, " +
                "SUM(d.cantidad), " +
                "SUM(d.precioUnitario * d.cantidad)" +
                ") " +
                "FROM DetallePedidoEntity d " +
                "JOIN d.pedido p " +
                "WHERE d.deleted = false AND p.deleted = false AND p.estado = 'ENTREGADO' " +
                "GROUP BY d.idProducto " +
                "ORDER BY SUM(d.cantidad) DESC";

        TypedQuery<TopProductoDTO> query = entityManager.createQuery(jpql, TopProductoDTO.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}