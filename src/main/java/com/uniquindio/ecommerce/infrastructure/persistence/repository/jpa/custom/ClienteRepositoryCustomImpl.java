package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ClienteEntity;
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
public class ClienteRepositoryCustomImpl implements ClienteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ClienteEntity> findWithAdvancedFilters(
            String nombre,
            String email,
            String viveroAsignadoId,
            String ciudad,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ClienteEntity> query = cb.createQuery(ClienteEntity.class);
        Root<ClienteEntity> root = query.from(ClienteEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (nombre != null && !nombre.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        if (email != null && !email.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("email").get("valor")), "%" + email.toLowerCase() + "%"));
        }
        if (viveroAsignadoId != null && !viveroAsignadoId.isBlank()) {
            predicates.add(cb.equal(root.get("viveroAsignadoId"), viveroAsignadoId));
        }
        if (ciudad != null && !ciudad.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("direccion").get("ciudad")), "%" + ciudad.toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("nombre")));

        TypedQuery<ClienteEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ClienteEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClienteEntity> countRoot = countQuery.from(ClienteEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }
}