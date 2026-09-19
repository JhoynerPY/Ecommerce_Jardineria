package com.uniquindio.ecommerce.domain.especie;

//Ficha tipo guia que tendra cada ejemplar con recomendaciones para su cuidado
public record FichaDeCuidado(
        RequerimientoLuz requerimientoLuz,
        String frecuenciaRiego,
        String tipoTierra,
        String temporadaFloracion) {


}
