package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
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
public class ViveroRepositoryCustomImpl implements ViveroRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ViveroEntity> findWithAdvancedFilters(
            String nombre,
            Boolean activo,
            Double latitud,
            Double longitud,
            Double radioMaximo,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViveroEntity> query = cb.createQuery(ViveroEntity.class);
        Root<ViveroEntity> root = query.from(ViveroEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (nombre != null && !nombre.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        if (activo != null) {
            predicates.add(cb.equal(root.get("activo"), activo));
        }
        if (latitud != null && longitud != null) {
            // Calcular distancia usando fórmula de Haversine en JPQL
            // Para simplicidad, filtramos en memoria o usamos query nativa
            // Aquí agregamos el filtro de radio si se proporciona
            if (radioMaximo != null) {
                predicates.add(cb.le(
                    cb.function("haversine_distance", Double.class,
                        cb.literal(latitud), cb.literal(longitud),
                        root.get("direccion").get("latitud"), root.get("direccion").get("longitud")
                    ),
                    cb.literal(radioMaximo)
                ));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("nombre")));

        TypedQuery<ViveroEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ViveroEntity> result = typedQuery.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ViveroEntity> countRoot = countQuery.from(ViveroEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<ViveroEntity> findViverosCercanosConCobertura(double latitud, double longitud) {
        String jpql = "SELECT v FROM ViveroEntity v WHERE v.deleted = false AND v.activo = true " +
                "AND (6371 * acos(cos(radians(:lat)) * cos(radians(v.direccion.latitud)) * " +
                "cos(radians(v.direccion.longitud) - radians(:lon)) + " +
                "sin(radians(:lat)) * sin(radians(v.direccion.latitud)))) <= v.radioCoberturaKm " +
                "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(v.direccion.latitud)) * " +
                "cos(radians(v.direccion.longitud) - radians(:lon)) + " +
                "sin(radians(:lat)) * sin(radians(v.direccion.latitud))))";

        TypedQuery<ViveroEntity> query = entityManager.createQuery(jpql, ViveroEntity.class);
        query.setParameter("lat", latitud);
        query.setParameter("lon", longitud);
        return query.getResultList();
    }
}