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
public class FichaDeCuidadoEmbeddable {
    @Enumerated(EnumType.STRING)
    private RequerimientoLuz requerimientoLuz;
    private String frecuenciaRiego;
    private String sustrato;
    private String temporadaFloracion;
}
