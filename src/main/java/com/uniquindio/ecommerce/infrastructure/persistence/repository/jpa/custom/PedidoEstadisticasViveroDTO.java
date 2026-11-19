package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEstadisticasViveroDTO {
    private String viveroId;
    private String viveroNombre;
    private Long totalPedidos;
    private Long pedidosPendientes;
    private Long pedidosPagados;
    private Long pedidosEnviados;
    private Long pedidosEntregados;
    private Long pedidosRechazados;
    private Double totalVentas;
    private Double promedioVenta;
    private Double montoPendienteCobro;
}