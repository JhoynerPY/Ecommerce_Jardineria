package com.uniquindio.ecommerce.infrastructure.persistence.repository;

import com.uniquindio.ecommerce.application.ports.output.InventarioPort;
import com.uniquindio.ecommerce.domain.pedido.DetallePedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.EjemplarEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EjemplarJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class InventarioRepositoryAdapter implements InventarioPort {

    private final EjemplarJpaRepository jpaRepository;

    public InventarioRepositoryAdapter(EjemplarJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean descontarInventario(List<DetallePedido> detalles) {
        for (DetallePedido detalle : detalles) {
            // Buscar ejemplares disponibles para el producto
            List<EjemplarEntity> ejemplares = jpaRepository.findByEspecieIdAndDisponibilidadTrueAndDeletedFalse(detalle.getIdProducto());
            if (ejemplares.size() < detalle.getCantidad()) {
                return false; // No hay suficiente inventario
            }
            // Marcar los primeros N ejemplares como no disponibles
            for (int i = 0; i < detalle.getCantidad(); i++) {
                EjemplarEntity ejemplar = ejemplares.get(i);
                ejemplar.setDisponibilidad(false);
                jpaRepository.save(ejemplar);
            }
        }
        return true;
    }
}