# Mapeo de Cambios - Fases 3 y 4

## Resumen de Implementación

Este documento detalla todos los archivos creados y modificados durante la implementación de la **Fase 3 (Infraestructura y Persistencia JPA)** y **Fase 4 (Consultas Avanzadas)** del checklist de entrega.

---

## 📁 Estructura de Archivos Creados

### Capa Infrastructure - Entidades JPA (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/entity`)

| Archivo | Descripción | Entidad de Dominio Mapeada |
|---------|-------------|---------------------------|
| `DireccionEmbeddable.java` | Value Object embebido para direcciones | `Direccion` (record) |
| `EmailEmbeddable.java` | Value Object embebido para emails | `Email` (domain) |
| `PrecioEmbeddable.java` | Value Object embebido para precios | `Precio` (kit & ejemplar) |
| `FichaDeCuidadoEmbeddable.java` | Value Object embebido para ficha de cuidado | `FichaDeCuidado` (record) |
| `RiesgoEmbeddable.java` | Value Object embebido para riesgo | `Riesgo` (record) |
| `ExencionResponsabilidadEmbeddable.java` | Value Object embebido para exención | `ExencionResponsabilidad` (record) |
| `RequerimientoLuz.java` | Enum JPA para requerimiento de luz | `RequerimientoLuz` (domain enum) |
| `NivelRiesgo.java` | Enum JPA para nivel de riesgo | `NivelRiesgo` (domain enum) |
| `TipoExencion.java` | Enum JPA para tipo de exención | `ExencionResponsabilidad.TipoExencion` |
| `EstadoPedido.java` | Enum JPA para estado de pedido | `EstadoPedido` (domain enum) |
| `EtapaCrecimiento.java` | Enum JPA para etapa de crecimiento | `EtapaCrecimiento` (domain enum) |
| `EstadoDeSalud.java` | Enum JPA para estado de salud | `EstadoDeSalud` (domain enum) |
| `ViveroEntity.java` | Entidad JPA principal para viveros | `Vivero` |
| `ClienteEntity.java` | Entidad JPA principal para clientes | `Cliente` |
| `EspecieEntity.java` | Entidad JPA principal para especies | `Especie` |
| `EjemplarEntity.java` | Entidad JPA principal para ejemplares | `Ejemplar` |
| `ArticuloKitEntity.java` | Entidad JPA para artículos de kit | `ArticuloKit` |
| `KitJardineriaEntity.java` | Entidad JPA para kits de jardinería | `KitJardineria` |
| `PedidoEntity.java` | Entidad JPA principal para pedidos | `Pedido` |
| `DetallePedidoEntity.java` | Entidad JPA para detalles de pedido | `DetallePedido` |

**Características comunes de todas las entidades:**
- ✅ `@SQLDelete` para soft delete (actualiza `deleted = true`)
- ✅ `@Where(clause = "deleted = false")` para filtrar automáticamente
- ✅ Campo `deleted` boolean para borrado lógico
- ✅ Separación completa de entidades de dominio (clean architecture)

---

### Capa Infrastructure - Mappers (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/mapper`)

| Archivo | Descripción |
|---------|-------------|
| `JpaMapper.java` | Mapper centralizado bidireccional (Domain ↔ JPA) para todas las entidades |

**Métodos principales:**
- `toEntity()/toDomain()` para cada entidad: Vivero, Cliente, Especie, Ejemplar, ArticuloKit, KitJardineria, Pedido
- Conversión de Value Objects embebidos: Direccion, Email, Precio, FichaDeCuidado, Riesgo, ExencionResponsabilidad
- Conversión de Enums entre domain e infrastructure

---

### Capa Infrastructure - Repositorios JPA (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/repository/jpa`)

| Archivo | Descripción | Funcionalidades Clave |
|---------|-------------|----------------------|
| `ViveroJpaRepository.java` | Spring Data JPA para Vivero | Soft delete, búsqueda por proximidad (Haversine), filtros paginados |
| `ClienteJpaRepository.java` | Spring Data JPA para Cliente | Soft delete, búsqueda por email, filtros paginados |
| `EspecieJpaRepository.java` | Spring Data JPA para Especie | Soft delete, filtros por nombre, riesgo, luz |
| `EjemplarJpaRepository.java` | Spring Data JPA para Ejemplar | Soft delete, filtro por especie, etapa, disponibilidad, precio |
| `ArticuloKitJpaRepository.java` | Spring Data JPA para ArticuloKit | Soft delete, filtros por nombre, diámetro, precio |
| `KitJardineriaJpaRepository.java` | Spring Data JPA para KitJardineria | Soft delete, filtros por componentes |
| `PedidoJpaRepository.java` | Spring Data JPA para Pedido | Soft delete, filtros avanzados, reporte por vivero |
| `DetallePedidoJpaRepository.java` | Spring Data JPA para DetallePedido | Top productos vendidos con agregación |

---

### Capa Infrastructure - Repositorios Personalizados (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/repository/jpa/custom`)

| Archivo | Descripción |
|---------|-------------|
| `PedidoRepositoryCustom.java` | Interfaz para consultas avanzadas de Pedido |
| `PedidoRepositoryCustomImpl.java` | Implementación con Criteria API y JPQL nativo |
| `ViveroRepositoryCustom.java` | Interfaz para consultas avanzadas de Vivero |
| `ViveroRepositoryCustomImpl.java` | Implementación con búsqueda geoespacial |
| `ClienteRepositoryCustom.java` | Interfaz para consultas avanzadas de Cliente |
| `ClienteRepositoryCustomImpl.java` | Implementación con Criteria API |
| `EspecieRepositoryCustom.java` | Interfaz para consultas avanzadas de Especie |
| `EspecieRepositoryCustomImpl.java` | Implementación con Criteria API |
| `EjemplarRepositoryCustom.java` | Interfaz para consultas avanzadas de Ejemplar |
| `EjemplarRepositoryCustomImpl.java` | Implementación con Criteria API |
| `ArticuloKitRepositoryCustom.java` | Interfaz para consultas avanzadas de ArticuloKit |
| `ArticuloKitRepositoryCustomImpl.java` | Implementación con Criteria API |
| `ViveroReporteDTO.java` | DTO para reporte de viveros (JOIN + GROUP BY) |
| `TopProductoDTO.java` | DTO para top productos vendidos |

---

### Capa Infrastructure - Adaptadores de Repositorio (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/persistence/repository`)

| Archivo | Puerto Implementado | Descripción |
|---------|---------------------|-------------|
| `ViveroRepositoryAdapter.java` | `ViveroRepositoryPort` | Adapter JPA para viveros |
| `ClienteRepositoryAdapter.java` | `ClienteRepositoryPort` | Adapter JPA para clientes |
| `PedidoRepositoryAdapter.java` | `PedidoRepositoryPort` | Adapter JPA para pedidos |
| `CatalogoArticulosRepositoryAdapter.java` | `CatalogoArticulosPort` | Adapter JPA para catálogo |
| `InventarioRepositoryAdapter.java` | `InventarioPort` | Adapter JPA para inventario (descuenta ejemplares) |
| `KitJardineriaRepositoryAdapter.java` | `KitJardineriaRepository` | Adapter JPA para kits |
| `CarritoComprasRepositoryAdapter.java` | `CarritoComprasPort` | Adapter en memoria para carrito |
| `PasarelaPagoRepositoryAdapter.java` | `PasarelaPagoPort` | Adapter simulado para pasarela de pago |

---

### Configuración y Datos Iniciales

| Archivo | Descripción |
|---------|-------------|
| `../../src/main/resources/application.yml` | Configuración Spring Boot + JPA + MariaDB |
| `../../src/main/resources/data.sql` | **Datos iniciales reales**: 5 viveros, 7 clientes, 8 especies, 24 ejemplares, 15 artículos kit, 5 kits, 7 pedidos, 16 detalles |

**Datos en data.sql incluyen:**
- **Viveros**: El Jardín (Armenia), La Floresta (Pereira), Verde Valle (Manizales), Los Andes (Bogotá), Tropical (Cali)
- **Clientes**: 7 clientes distribuidos en las ciudades de los viveros
- **Especies**: Rosa, Orquídea, Helecho, Cactus, Lirio de la paz, Girasol, Anturio, Suculenta
- **Ejemplares**: 24 plantas en diferentes etapas y precios
- **Artículos Kit**: 5 plantas, 5 macetas, 5 sustratos
- **Kits**: 5 combinaciones válidas (planta + maceta + sustrato)
- **Pedidos**: 7 pedidos con diferentes estados y tipos de exención

---

### Archivos Modificados

| Archivo | Cambio |
|---------|--------|
| `Application.java` | Removido `@SpringBootApplication(excludeName = "DataSourceAutoConfiguration")` para habilitar JPA |

---

## ✅ Checklist de Cumplimiento

### Fase 3 - Infraestructura y Persistencia (JPA)
- [x] **Entidades JPA separadas del dominio** - 12 entidades + 6 embeddables + 7 enums
- [x] **Mappers bidireccionales** - `JpaMapper.java` con conversión completa Domain ↔ JPA
- [x] **Repositorios Spring Data JPA con soft delete** - 8 repositorios con `@SQLDelete` y `@Where`
- [x] **Script data.sql** - Datos iniciales funcionales para viveros, clientes, plantas, kits, pedidos

### Fase 4 - Consultas Avanzadas
- [x] **Listados paginados con filtros dinámicos (JPQL/Criteria API)** - 6 repositorios custom con `Page<T> findWithAdvancedFilters(...)`
- [x] **Consulta avanzada JOIN + GROUP BY + Agregación** - `PedidoRepositoryCustomImpl.getReportePorVivero()` y `getTopProductosVendidos()`

---

## 🔄 Fluesto de Datos (Domain ↔ Infrastructure)

```
Domain Layer                          Infrastructure Layer
┌─────────────────┐                   ┌─────────────────┐
│  Vivero         │  ←→ JpaMapper →   │  ViveroEntity   │
│  Cliente        │                   │  ClienteEntity  │
│  Especie        │                   │  EspecieEntity  │
│  Ejemplar       │                   │  EjemplarEntity │
│  ArticuloKit    │                   │  ArticuloKitEnt │
│  KitJardineria  │                   │  KitJardineriaE │
│  Pedido         │                   │  PedidoEntity   │
│  DetallePedido  │                   │  DetallePedEnt  │
└─────────────────┘                   └─────────────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    ▼                        ▼                        ▼
            ┌──────────────┐         ┌──────────────┐         ┌──────────────┐
            │ JpaRepository│         │ Custom Repos │         │  Adapters    │
            │ (CRUD +      │         │ (Advanced    │         │ (Port        │
            │  Queries)    │         │  Queries)    │         │  Impl)       │
            └──────────────┘         └──────────────┘         └──────────────┘
```

---

## 🧪 Pruebas Sugeridas

Para verificar la implementación:

```bash
# Compilar y ejecutar tests
./gradlew test

# Verificar que la aplicación inicia con JPA
./gradlew bootRun
```

La aplicación debería:
1. Conectar a MariaDB (configurar en application.yml)
2. Ejecutar `data.sql` al iniciar
3. Exponer repositorios JPA funcionando con soft delete
4. Permitir consultas paginadas y reportes agregados

---

## 📝 Notas Técnicas

1. **Soft Delete**: Todas las entidades usan `@SQLDelete` + `@Where` para borrado lógico transparente
2. **Geoespacial**: Búsqueda de viveros cercanos usando fórmula Haversine en JPQL
3. **Paginación**: Todas las consultas avanzadas retornan `Page<T>` compatible con Spring Data
4. **Filtros Dinámicos**: Criteria API para construir queries condicionalmente
5. **Agregaciones**: JPQL con `NEW DTO(...)` para reportes tipados
6. **Separación de Capas**: Zero dependencias de JPA en el dominio (clean architecture)