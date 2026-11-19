package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.ClienteRepositoryPort;
import com.uniquindio.ecommerce.domain.cliente.Cliente;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.ClienteJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = JpaMapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return JpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return jpaRepository.findByEmail_ValorAndDeletedFalse(email)
                .map(JpaMapper::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail_ValorAndDeletedFalse(email);
    }
}