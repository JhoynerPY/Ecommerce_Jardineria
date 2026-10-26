package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViveroReporteDTO {
    private String viveroId;
    private String viveroNombre;
    private Long totalPedidos;
    private Long pedidosEntregados;
    private Double promedioVenta;
    private Double totalVentas;
}