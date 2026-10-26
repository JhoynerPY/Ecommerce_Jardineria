package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ArticuloKitEntity;
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
public class ArticuloKitRepositoryCustomImpl implements ArticuloKitRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ArticuloKitEntity> findWithAdvancedFilters(
            String nombre,
            Double minDiametro,
            Double maxDiametro,
            Double precioMin,
            Double precioMax,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ArticuloKitEntity> query = cb.createQuery(ArticuloKitEntity.class);
        Root<ArticuloKitEntity> root = query.from(ArticuloKitEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (nombre != null && !nombre.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        if (minDiametro != null) {
            predicates.add(cb.ge(root.get("diametro"), minDiametro));
        }
        if (maxDiametro != null) {
            predicates.add(cb.le(root.get("diametro"), maxDiametro));
        }
        if (precioMin != null) {
            predicates.add(cb.ge(root.get("precio").get("valor"), precioMin));
        }
        if (precioMax != null) {
            predicates.add(cb.le(root.get("precio").get("valor"), precioMax));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("nombre")));

        TypedQuery<ArticuloKitEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ArticuloKitEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ArticuloKitEntity> countRoot = countQuery.from(ArticuloKitEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }
}