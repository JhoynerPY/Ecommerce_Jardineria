# Mapeo de Cambios - Fase 5: Consultas JPQL Avanzadas

## Resumen de Implementación

Este documento detalla todos los archivos creados y modificados durante la implementación de la **Fase 5 (Consultas JPQL Avanzadas)** del checklist de entrega.

---

## 📁 Archivos Creados

### DTOs para Consultas Avanzadas (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/repository/jpa/custom`)

| Archivo | Descripción |
|---------|-------------|
| `PedidoEstadisticasViveroDTO.java` | DTO para estadísticas completas de pedidos por vivero (total, por estado, ventas, promedios) |
| `PlantaBusquedaDTO.java` | DTO para resultados de búsqueda de plantas con info de especie, precio, vivero |

---

## 📁 Archivos Modificados

### Repositorios JPA con JPQL Avanzado (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/repository/jpa`)

#### 1. `EjemplarJpaRepository.java` - Búsqueda de plantas
**Nuevas consultas JPQL agregadas:**
- `buscarPlantasPorNombreYPrecio()` - **Paginación + filtros dinámicos** por nombre de especie (común/científico), rango de precio, disponibilidad, etapa de crecimiento. Retorna `PlantaBusquedaDTO` con JOIN a Especie y Vivero
- `buscarPorNombreEspecie()` - Paginación simple por nombre común o científico
- `buscarPorRangoPrecio()` - Paginación por rango de precio (BETWEEN)
- `contarDisponiblesPorEspecie()` - **GROUP BY** especie con conteo de disponibles
- `estadisticasPrecioPorEspecie()` - **GROUP BY** especie con AVG, MIN, MAX de precios

#### 2. `PedidoJpaRepository.java` - Estadísticas por vivero (JOIN + GROUP BY)
**Nuevas consultas JPQL agregadas:**
- `getEstadisticasCompletasPorVivero()` - **LEFT JOIN Vivero-Pedido + GROUP BY** con estadísticas completas: total pedidos, conteo por estado (PENDIENTE_PAGO, PAGADO, ENVIADO, ENTREGADO, RECHAZADO), total ventas, promedio venta, monto pendiente cobro
- `getEstadisticasPedidosEntregadosPorVivero()` - **INNER JOIN + GROUP BY** solo pedidos ENTREGADOS (delegados por vivero local), ordenado por total ventas DESC
- `getTotalVentasPorVivero()` - Ventas totales por vivero (estados PAGADO/ENVIADO/ENTREGADO)
- `contarPedidosPorEstado()` - **GROUP BY** estado con conteo
- `getIngresosPorMes()` - **GROUP BY** mes (substring ID) con conteo y suma ventas
- `getTopViverosPorPedidos()` - Top viveros por número de pedidos
- `getPromedioItemsPorPedidoPorVivero()` - Promedio de items por pedido por vivero

#### 3. `ArticuloKitJpaRepository.java` - Catálogo artículos kit
**Nuevas consultas JPQL agregadas:**
- `buscarPorRangoPrecio()` - Paginación por rango de precio
- `buscarPorNombreYPrecio()` - Paginación combinada nombre + rango precio
- `getEstadisticasPrecios()` - AVG, MIN, MAX global
- `contarPorRangoPrecio()` - Conteo por rango

#### 4. `ViveroJpaRepository.java` - Estadísticas viveros
**Nuevas consultas JPQL agregadas:**
- `getViverosConClientesAsignados()` - **LEFT JOIN + GROUP BY** vivero-cliente con conteo
- `findByRadioCoberturaMin()` - Filtrar por radio mínimo
- `getPromedioRadioCobertura()` - Promedio global

#### 5. `ClienteJpaRepository.java` - Estadísticas clientes
**Nuevas consultas JPQL agregadas:**
- `buscarPorCiudad()` - Paginación por ciudad (LIKE en embedded)
- `getClientesPorVivero()` - **LEFT JOIN + GROUP BY** vivero con conteo clientes
- `countTotalClientes()` / `countClientesConViveroAsignado()` - Conteos globales

#### 6. `EspecieJpaRepository.java` - Estadísticas especies
**Nuevas consultas JPQL agregadas:**
- `findByRequerimientoLuz()` - Filtrar por luz (SOL/SOMBRA_PARCIAL/SOMBRA_TOTAL)
- `contarPorNivelRiesgo()` - **GROUP BY** nivel riesgo
- `contarPorRequerimientoLuz()` - **GROUP BY** requerimiento luz
- `findEspeciesToxicas()` - Filtrar riesgo != NINGUNO

### Interfaces Custom y Implementaciones

#### 7. `PedidoRepositoryCustom.java` + `PedidoRepositoryCustomImpl.java`
**Métodos agregados:**
- `getEstadisticasCompletasPorVivero()` - Implementación con JPQL nativo
- `getEstadisticasPedidosEntregadosPorVivero()` - Implementación con JPQL nativo

---

## 🔍 Detalle de Consultas JPQL Clave

### 1. Búsqueda paginada de plantas por nombre y precio (Ejemplar)
```sql
SELECT NEW PlantaBusquedaDTO(e.id, e.especie.nombreComun, e.especie.nombreCientifico,
       e.etapaCrecimiento, e.precio.valor, e.precio.moneda, e.disponibilidad,
       v.nombre, v.id)
FROM EjemplarEntity e
JOIN e.especie esp
LEFT JOIN ViveroEntity v ON v.id = (subquery)
WHERE e.deleted = false
  AND (:nombreEspecie IS NULL OR LOWER(esp.nombreComun) LIKE ... OR LOWER(esp.nombreCientifico) LIKE ...)
  AND (:precioMin IS NULL OR e.precio.valor >= :precioMin)
  AND (:precioMax IS NULL OR e.precio.valor <= :precioMax)
  AND (:disponible IS NULL OR e.disponibilidad = :disponible)
ORDER BY esp.nombreComun ASC, e.precio.valor ASC
```

### 2. Estadísticas completas por vivero (Pedido) - JOIN + GROUP BY
```sql
SELECT NEW PedidoEstadisticasViveroDTO(
    v.id, v.nombre,
    COUNT(p),
    SUM(CASE WHEN p.estado = 'PENDIENTE_PAGO' THEN 1 ELSE 0 END),
    SUM(CASE WHEN p.estado = 'PAGADO' THEN 1 ELSE 0 END),
    SUM(CASE WHEN p.estado = 'ENVIADO' THEN 1 ELSE 0 END),
    SUM(CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END),
    SUM(CASE WHEN p.estado = 'RECHAZADO' THEN 1 ELSE 0 END),
    SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)),
    AVG((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)),
    SUM(CASE WHEN p.estado = 'PENDIENTE_PAGO' THEN (SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d) ELSE 0 END)
)
FROM ViveroEntity v
LEFT JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false
WHERE v.deleted = false
GROUP BY v.id, v.nombre
ORDER BY v.nombre
```

### 3. Pedidos delegados/entregados por vivero local - INNER JOIN + GROUP BY
```sql
SELECT NEW PedidoEstadisticasViveroDTO(...)
FROM ViveroEntity v
INNER JOIN PedidoEntity p ON p.idVivero = v.id AND p.deleted = false
WHERE v.deleted = false AND p.estado = 'ENTREGADO'
GROUP BY v.id, v.nombre
ORDER BY SUM((SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)) DESC
```

---

## ✅ Checklist Fase 5

- [x] **Paginación con filtros dinámicos JPQL** - 6 repositorios con `Page<T>` y parámetros opcionales
- [x] **Búsqueda por nombre/precio de plantas** - `EjemplarJpaRepository.buscarPlantasPorNombreYPrecio()`, `ArticuloKitJpaRepository.buscarPorNombreYPrecio()`
- [x] **Consultas JOIN + GROUP BY para estadísticas** - `PedidoJpaRepository` con 7 consultas de agregación por vivero
- [x] **Pedidos delegados por vivero local** - `getEstadisticasPedidosEntregadosPorVivero()` con INNER JOIN y filtro ENTREGADO
- [x] **DTOs tipados para resultados** - `PedidoEstadisticasViveroDTO`, `PlantaBusquedaDTO`
- [x] **Compilación exitosa** - `./gradlew compileJava` ✓
- [x] **Tests pasando** - `./gradlew test` ✓

---

## 🔄 Patrones Utilizados

| Patrón | Ejemplo |
|--------|---------|
| **Parámetros opcionales** | `(:param IS NULL OR column = :param)` |
| **LIKE case-insensitive** | `LOWER(column) LIKE LOWER(CONCAT('%', :param, '%'))` |
| **Rango BETWEEN** | `column BETWEEN :min AND :max` |
| **JOIN + GROUP BY** | `FROM Vivero v LEFT JOIN Pedido p ON ... GROUP BY v.id` |
| **Subqueries en SELECT** | `(SELECT SUM(d.precioUnitario * d.cantidad) FROM p.detalles d)` |
| **CASE WHEN en agregación** | `SUM(CASE WHEN estado = 'X' THEN 1 ELSE 0 END)` |
| **Constructor DTO en JPQL** | `SELECT NEW com.pkg.DTO(campos...)` |
| **Paginación nativa** | `Page<T>` con `Pageable` |

---

## 📝 Notas Técnicas

1. **Soft delete transparente**: Todas las queries incluyen `AND deleted = false`
2. **Embedded fields**: Acceso via path `c.direccion.ciudad`, `e.precio.valor`
3. **Enums en JPQL**: Comparación directa `p.estado = 'ENTREGADO'`
4. **Performance**: Subqueries correlacionadas para totales por pedido
5. **Tipo seguro**: DTOs con `@Data @AllArgsConstructor` para `NEW DTO()` en JPQL