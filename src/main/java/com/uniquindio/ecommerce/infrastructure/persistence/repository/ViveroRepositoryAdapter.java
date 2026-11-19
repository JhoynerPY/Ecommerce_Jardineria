package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.ViveroRepositoryPort;
import com.uniquindio.ecommerce.domain.vivero.Vivero;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ViveroEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ViveroJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ViveroRepositoryAdapter implements ViveroRepositoryPort {

    private final ViveroJpaRepository jpaRepository;

    public ViveroRepositoryAdapter(ViveroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Vivero> buscarPorId(String id) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(JpaMapper::toDomain);
    }

    @Override
    public List<Vivero> buscarActivos() {
        return jpaRepository.findByActivoTrueAndDeletedFalse().stream()
                .map(JpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Vivero> buscarViveroMasCercano(double latitud, double longitud) {
        List<ViveroEntity> viveros = jpaRepository.findViverosCubriendoDireccion(latitud, longitud);
        return viveros.stream()
                .findFirst()
                .map(JpaMapper::toDomain);
    }

    @Override
    public void guardar(Vivero vivero) {
        ViveroEntity entity = JpaMapper.toEntity(vivero);
        jpaRepository.save(entity);
    }
}