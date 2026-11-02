package com.uniquindio.ecommerce.infrastructure.security.controller;

import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.PedidoJpaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.repository.jpa.custom.TopProductoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final PedidoJpaRepository pedidoRepository;

    public ReporteController(PedidoJpaRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // Endpoint para el Top 5 de productos más vendidos (Ranking con JOIN y GROUP BY)
    @GetMapping("/top-productos")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VENDEDOR_VIVERO')")
    public ResponseEntity<List<TopProductoDTO>> topProductos() {
        // Usa la consulta
        List<TopProductoDTO> top = pedidoRepository.getTopProductosVendidos(5);
        return ResponseEntity.ok(top);
    }

    // Endpoint para estadísticas de ventas por vivero (Ranking con SUM y COUNT)
    @GetMapping("/estadisticas-viveros")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Object[]>> estadisticasViveros() {
        // Usa la consulta
        List<Object[]> stats = pedidoRepository.getTotalVentasPorVivero();
        return ResponseEntity.ok(stats);
    }

    // Endpoint para ingresos por mes
    @GetMapping("/ingresos-mes")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Object[]>> ingresosPorMes() {
        List<Object[]> ingresos = pedidoRepository.getIngresosPorMes();
        return ResponseEntity.ok(ingresos);
    }


}
