package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.EjemplarRepositoryPort;
import com.uniquindio.ecommerce.domain.ejemplar.Ejemplar;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EjemplarJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EjemplarRepositoryAdapter implements EjemplarRepositoryPort {

    private final EjemplarJpaRepository jpaRepository;

    public EjemplarRepositoryAdapter(EjemplarJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Ejemplar> buscarPorId(String id) {
        return jpaRepository.findByIdAndDeletedFalse(id).map(JpaMapper::toDomain);
    }

    @Override
    public Ejemplar guardar(Ejemplar ejemplar) {
        EjemplarEntity entity = JpaMapper.toEntity(ejemplar);
        EjemplarEntity saved = jpaRepository.save(entity);
        return JpaMapper.toDomain(saved);
    }
}
