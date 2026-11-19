package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioEmbeddable {
    private BigDecimal valor;
    private String moneda;
}
