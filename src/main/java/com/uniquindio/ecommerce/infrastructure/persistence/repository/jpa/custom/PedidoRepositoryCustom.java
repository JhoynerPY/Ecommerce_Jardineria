package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EstadoPedido;
import com.uniquindio.ecommerce.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PedidoRepositoryCustom {
    Page<PedidoEntity> findWithAdvancedFilters(
            String idCliente,
            String idVivero,
            EstadoPedido estado,
            String fechaDesde,
            String fechaHasta,
            Pageable pageable
    );

    List<ViveroReporteDTO> getReportePorVivero();

    List<TopProductoDTO> getTopProductosVendidos(int limit);

    List<PedidoEstadisticasViveroDTO> getEstadisticasCompletasPorVivero();

    List<PedidoEstadisticasViveroDTO> getEstadisticasPedidosEntregadosPorVivero();
}