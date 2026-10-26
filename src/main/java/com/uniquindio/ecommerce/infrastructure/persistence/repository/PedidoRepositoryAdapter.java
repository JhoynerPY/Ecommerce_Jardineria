package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.PedidoRepositoryPort;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.mapper.JpaMapper;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.PedidoJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoEntity entity = JpaMapper.toEntity(pedido);
        PedidoEntity saved = jpaRepository.save(entity);
        return JpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Pedido> buscarPorId(String idPedido) {
        return jpaRepository.findByIdAndDeletedFalse(idPedido)
                .map(JpaMapper::toDomain);
    }
}