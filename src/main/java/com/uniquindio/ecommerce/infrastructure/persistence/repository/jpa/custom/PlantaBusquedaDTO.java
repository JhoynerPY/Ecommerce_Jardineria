package com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantaBusquedaDTO {
    private String ejemplarId;
    private String especieNombreComun;
    private String especieNombreCientifico;
    private String etapaCrecimiento;
    private Double precio;
    private String moneda;
    private Boolean disponible;
    private String viveroNombre;
    private String viveroId;
}