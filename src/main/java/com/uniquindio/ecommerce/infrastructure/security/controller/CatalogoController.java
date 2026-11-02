package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.EjemplarJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;


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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Usamos la consulta que ya tenían en el repositorio
        Page<Object[]> resultados = ejemplarRepository.buscarPlantasPorNombreYPrecio(
                nombre, precioMin, precioMax, true, null, pageable);

        // Cumplimos la rúbrica devolviendo contenido, total de elementos y total de páginas
        Map<String, Object> response = new HashMap<>();
        response.put("contenido", resultados.getContent());
        response.put("paginaActual", resultados.getNumber());
        response.put("totalElementos", resultados.getTotalElements());
        response.put("totalPaginas", resultados.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
