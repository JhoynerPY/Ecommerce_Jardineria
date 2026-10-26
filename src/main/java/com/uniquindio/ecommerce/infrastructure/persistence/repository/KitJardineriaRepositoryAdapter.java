package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.domain.kit.KitJardineria;
import com.uniquindio.ecommerce.domain.repository.KitJardineriaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.KitJardineriaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.KitJardineriaJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class KitJardineriaRepositoryAdapter implements KitJardineriaRepository {

    private final KitJardineriaJpaRepository jpaRepository;

    public KitJardineriaRepositoryAdapter(KitJardineriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<KitJardineria> obtenerKitPorIdentificador(String id) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(JpaMapper::toDomain);
    }

    @Override
    public void almacenarKit(KitJardineria kit) {
        KitJardineriaEntity entity = JpaMapper.toEntity(kit);
        jpaRepository.save(entity);
    }
}