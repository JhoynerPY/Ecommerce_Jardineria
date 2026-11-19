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
public class ExencionResponsabilidadEmbeddable {
    @Enumerated(EnumType.STRING)
    private TipoExencion tipo;
    private String terminos;
    private boolean aceptadaPorCliente;
    private String version;
}
