package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductoDTO {
    private String productoId;
    private String productoNombre;
    private Long cantidadVendida;
    private Double totalVendido;
}