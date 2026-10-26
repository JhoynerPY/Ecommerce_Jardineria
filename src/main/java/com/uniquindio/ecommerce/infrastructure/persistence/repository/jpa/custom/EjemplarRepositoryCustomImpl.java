package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
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
public class EjemplarRepositoryCustomImpl implements EjemplarRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<EjemplarEntity> findWithAdvancedFilters(
            String especieId,
            com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento etapaCrecimiento,
            Boolean disponible,
            Double precioMin,
            Double precioMax,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EjemplarEntity> query = cb.createQuery(EjemplarEntity.class);
        Root<EjemplarEntity> root = query.from(EjemplarEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (especieId != null && !especieId.isBlank()) {
            predicates.add(cb.equal(root.get("especie").get("id"), especieId));
        }
        if (etapaCrecimiento != null) {
            predicates.add(cb.equal(root.get("etapaCrecimiento"), etapaCrecimiento));
        }
        if (disponible != null) {
            predicates.add(cb.equal(root.get("disponibilidad"), disponible));
        }
        if (precioMin != null) {
            predicates.add(cb.ge(root.get("precio").get("valor"), precioMin));
        }
        if (precioMax != null) {
            predicates.add(cb.le(root.get("precio").get("valor"), precioMax));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("id")));

        TypedQuery<EjemplarEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EjemplarEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EjemplarEntity> countRoot = countQuery.from(EjemplarEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }
}