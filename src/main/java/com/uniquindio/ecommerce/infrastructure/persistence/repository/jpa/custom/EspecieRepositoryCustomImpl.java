package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EspecieEntity;
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
public class EspecieRepositoryCustomImpl implements EspecieRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<EspecieEntity> findWithAdvancedFilters(
            String nombreComun,
            String nombreCientifico,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.NivelRiesgo nivelRiesgo,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.RequerimientoLuz requerimientoLuz,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EspecieEntity> query = cb.createQuery(EspecieEntity.class);
        Root<EspecieEntity> root = query.from(EspecieEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (nombreComun != null && !nombreComun.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nombreComun")), "%" + nombreComun.toLowerCase() + "%"));
        }
        if (nombreCientifico != null && !nombreCientifico.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nombreCientifico")), "%" + nombreCientifico.toLowerCase() + "%"));
        }
        if (nivelRiesgo != null) {
            predicates.add(cb.equal(root.get("riesgo").get("nivel"), nivelRiesgo));
        }
        if (requerimientoLuz != null) {
            predicates.add(cb.equal(root.get("fichaDeCuidado").get("requerimientoLuz"), requerimientoLuz));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("nombreComun")));

        TypedQuery<EspecieEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EspecieEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EspecieEntity> countRoot = countQuery.from(EspecieEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }
}