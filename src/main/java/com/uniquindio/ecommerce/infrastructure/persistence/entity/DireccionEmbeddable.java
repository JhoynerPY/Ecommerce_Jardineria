package com.uniquindio.ecommerce.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionEmbeddable {
    private String calle;
    private String ciudad;
    private String departamento;
    private String codigoPostal;
    private String pais;
    private double latitud;
    private double longitud;
}
