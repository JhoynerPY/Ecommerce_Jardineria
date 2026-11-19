package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiesgoEmbeddable {
    @Enumerated(EnumType.STRING)
    private NivelRiesgo nivel;
    private String descripcion;
}
