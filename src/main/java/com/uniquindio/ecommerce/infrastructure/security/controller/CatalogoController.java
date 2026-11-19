package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.infrastructure.persistence.entity.EtapaCrecimiento;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EjemplarJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.PlantaBusquedaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final EjemplarJpaRepository ejemplarRepository;

    public CatalogoController(EjemplarJpaRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    @GetMapping("/ejemplares")
    public ResponseEntity<Map<String, Object>> buscarEjemplares(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) EtapaCrecimiento etapa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PlantaBusquedaDTO> resultados = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                        nombre, precioMin, precioMax, true, etapa, pageable)
                .map(this::convertirADTO);

        // Cumplimos la rúbrica devolviendo contenido, total de elementos y total de páginas
        Map<String, Object> response = new HashMap<>();
        response.put("contenido", resultados.getContent());
        response.put("paginaActual", resultados.getNumber());
        response.put("totalElementos", resultados.getTotalElements());
        response.put("totalPaginas", resultados.getTotalPages());

        return ResponseEntity.ok(response);
    }

    private PlantaBusquedaDTO convertirADTO(Object[] row) {
        return new PlantaBusquedaDTO(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                row[3] != null ? row[3].toString() : null,
                row[4] != null ? ((BigDecimal) row[4]).doubleValue() : null,
                (String) row[5],
                (Boolean) row[6],
                null,
                null,
                row[7] != null ? row[7].toString() : null);
    }
}
