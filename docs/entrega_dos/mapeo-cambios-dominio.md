# Mapeo de Cambios - Entrega 2: Modelo de Intermediación con Viveros

## Resumen General
Se transformó el dominio de un e-commerce que despacha directamente a un **modelo de intermediación** que conecta compradores con el vivero local más cercano según su dirección, agregando gestión de exención de responsabilidad por daños en transporte.

---

## 1. Value Objects Creados

### 1.1 Direccion (`domain/common/Direccion.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/common/Direccion.java`

**Lógica implementada:**
- Record inmutable con campos: `calle`, `ciudad`, `departamento`, `codigoPostal`, `pais`, `latitud`, `longitud`
- Validaciones en constructor compacto: campos obligatorios, formato código postal (regex `^\d{5,6}$`), rangos latitud (-90 a 90) y longitud (-180 a 180)
- Método factoría `de()` para creación
- Método `distanciaA(Direccion otra)`: cálculo geodésico Haversine (radio Tierra 6371 km)
- `toString()` formateado legible

**Tests:** `src/test/java/com/uniquindio/ecommerce/domain/common/DireccionTest.java`
- `crearDireccionValida()` - creación exitosa
- `crearDireccionConCalleNulaLanzaExcepcion()` - NullPointerException por `@NotNull` implícito en record
- `crearDireccionConCiudadVaciaLanzaExcepcion()` - IllegalArgumentException
- `crearDireccionConCodigoPostalInvalidoLanzaExcepcion()` - regex falla
- `crearDireccionConLatitudFueraDeRangoLanzaExcepcion()` - validación rango
- `crearDireccionConLongitudFueraDeRangoLanzaExcepcion()` - validación rango
- `distanciaEntreDosDirecciones()` - Armenia a Pereira ~31km
- `distanciaCeroMismaDireccion()` - identidad
- `toStringFormatoCorrecto()` - formato esperado

---

### 1.2 ExencionResponsabilidad (`domain/pedido/ExencionResponsabilidad.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/pedido/ExencionResponsabilidad.java`

**Lógica implementada:**
- Record inmutable con: `tipo` (enum), `terminos`, `aceptadaPorCliente`, `version`
- Enum `TipoExencion`: `EXENCION_TOTAL`, `ACUERDO_PARCIAL_PERDIDA`, `SIN_EXENCION`
- Validaciones: términos y versión no vacíos; si tipo != SIN_EXENCION → `aceptadaPorCliente` debe ser true
- Métodos factoría: `exencionTotal()`, `acuerdoParcialPerdida()`, `sinExencion()`
- `calcularCobertura(double valorTotal)`: retorna 0, 50% o 100% según tipo
- Predicados: `esExencionTotal()`, `esAcuerdoParcial()`, `requiereAceptacion()`

**Tests:** `src/test/java/com/uniquindio/ecommerce/domain/pedido/ExencionResponsabilidadTest.java`
- `crearExencionTotalValida()` / `crearAcuerdoParcialPerdidaValido()` / `crearSinExencion()` - factorías
- `exencionTotalSinAceptarLanzaExcepcion()` / `acuerdoParcialSinAceptarLanzaExcepcion()` - validación aceptación
- `terminosVaciosLanzaExcepcion()` / `versionVaciaLanzaExcepcion()` - validaciones campos
- `calcularCoberturaExencionTotal()` / `calcularCoberturaAcuerdoParcial()` / `calcularCoberturaSinExencion()` - cobertura 0%, 50%, 100%
- `esExencionTotal()` / `esAcuerdoParcial()` / `requiereAceptacion()` - predicados
- `igualdadPorValor()` - igualdad semántica de record

---

## 2. Entidades/Agregados Creados

### 2.1 Vivero (`domain/vivero/Vivero.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/vivero/Vivero.java`

**Lógica implementada:**
- Entidad con identidad (UUID), campos: `nombre`, `direccion` (Direccion VO), `telefono`, `email`, `radioCoberturaKm`, `especiesDisponiblesIds`, `activo`
- Factoría `registrar()`: validaciones nombre no vacío, dirección obligatoria, radio > 0
- `cubreDireccion(Direccion cliente)`: usa `direccion.distanciaA()` ≤ `radioCoberturaKm` y verifica `activo`
- Gestión especies: `agregarEspecieDisponible()`, `removerEspecieDisponible()`, `tieneEspecieDisponible()` (evita duplicados)
- `activar()` / `desactivar()` - ciclo de vida
- Getters defensivos (List.copyOf para especies)
- equals/hashCode por identidad (id)

**Tests:** `src/test/java/com/uniquindio/ecommerce/domain/vivero/ViveroTest.java`
- `registrarViveroValido()` - creación exitosa con UUID generado
- `registrarViveroSinNombreLanzaExcepcion()` / `registrarViveroSinDireccionLanzaExcepcion()` / `registrarViveroConRadioCeroLanzaExcepcion()` - validaciones factoría
- `cubreDireccionClienteCercano()` - dentro de radio (Armenia ~3km)
- `noCubreDireccionClienteLejano()` - fuera de radio (Bogotá ~280km)
- `viveroInactivoNoCubreNingunaDireccion()` - estado inactivo bloquea cobertura
- `agregarYRemoverEspecieDisponible()` - CRUD especies
- `noDuplicarEspeciesDisponibles()` - set semántico
- `activarDesactivarVivero()` - ciclo de vida

### 2.2 ViveroRepository (`domain/vivero/ViveroRepository.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/vivero/ViveroRepository.java`

**Puerto de repositorio:**
- `buscarPorId(String id)`
- `buscarActivos()` - solo viveros activos
- `buscarViveroMasCercano(double lat, double lon)` - para asignación automática
- `almacenar(Vivero vivero)`

---

## 3. Entidades Existentes Modificadas

### 3.1 Cliente (`domain/cliente/Cliente.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/cliente/Cliente.java`

**Cambios:**
| Antes | Después |
|-------|---------|
| `direccion: String` | `direccion: Direccion` (VO) |
| Sin vivero asignado | `viveroAsignadoId: String` (nullable) |
| `registrar(..., String direccion)` | `registrar(..., Direccion direccion)` + valida no null |
| `actualizarDatosContacto(String tel, String dir)` | `actualizarDatosContacto(String tel, Direccion dir)` + **resetea viveroAsignadoId a null** |
| - | `asignarVivero(String viveroId)` |
| - | `tieneViveroAsignado(): boolean` |
| Getter `getDireccion(): String` | Getter `getDireccion(): Direccion` |
| - | Getter `getViveroAsignadoId(): String` |

**Tests:** `src/test/java/com/uniquindio/ecommerce/domain/cliente/ClienteTest.java`
- `registrarClienteValido()` - creación con Direccion VO, vivero null
- `registrarClienteSinDireccionLanzaExcepcion()` - validación obligatoriedad
- `actualizarDatosContactoActualizaDireccionYReseteaVivero()` - cambio dirección limpia vivero
- `asignarVivero()` - asignación y flag tieneViveroAsignado
- `igualdadPorId()` - identidad por ID

---

### 3.2 Pedido (`domain/pedido/Pedido.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/pedido/Pedido.java`

**Cambios:**
| Antes | Después |
|-------|---------|
| Constructor: `(idCliente, detalles, direccionEntrega)` | Constructor: `(idCliente, idVivero, detalles, direccionEntrega, exencionResponsabilidad)` |
| `crear(idCliente, detalles, direccion)` | `crear(idCliente, idVivero, detalles, direccionEntrega, exencionResponsabilidad)` |
| Validaciones: detalles + dirección | + **idVivero obligatorio** + **exencionResponsabilidad obligatoria** |
| `calcularTotal()` | `calcularTotal()` (sin cambios) + **`calcularTotalConExencion()`** aplica VO |
| Estados: PENDIENTE_PAGO, PAGADO, RECHAZADO, ENVIADO | + **ENTREGADO** |
| - | `marcarComoEnviado()`: solo desde PAGADO |
| Getters: idPedido, idCliente, detalles, estado, direccionEntrega | + `getIdVivero()`, `getExencionResponsabilidad()` |

**Tests:** `src/test/java/com/uniquindio/ecommerce/domain/pedido/PedidoTest.java`
- `crearPedidoSinDetallesLanzaReglaDominioException()` - invariante detalles
- `crearPedidoSinViveroLanzaReglaDominioException()` - **nuevo: idVivero obligatorio**
- `crearPedidoSinExencionLanzaReglaDominioException()` - **nuevo: exención obligatoria**
- `crearExencionParcialSinAceptarLanzaExcepcion()` - **nuevo: validación en VO, no en Pedido**
- `pagarPedidoQueNoEstaPendienteLanzaReglaDominioException()` - transición inválida
- `calcularTotalConExencionParcialReduceElTotal()` - **nuevo: 50% cobertura**
- `calcularTotalConExencionTotalEsCero()` - **nuevo: 0% cobertura**
- `marcarComoEnviadoSoloSiPagado()` - **nuevo: transición PAGADO → ENVIADO**

---

### 3.3 EstadoPedido (`domain/pedido/EstadoPedido.java`)
**Ubicación:** `src/main/java/com/uniquindio/ecommerce/domain/pedido/EstadoPedido.java`

**Cambio:** Agregado valor `ENTREGADO` al enum.

---

## 4. Documentación Actualizada

### 4.1 Glosario Lenguaje Ubicuo (`glosario-lenguaje-ubicuo.md`)
**Ubicación:** `glosario-lenguaje-ubicuo.md` (raíz del proyecto)

**Nuevas entradas agregadas:**
- **Vivero** - definición, sinónimos (Nursery, ProveedorLocal), anti-patrones (no usar Tienda/Almacén)
- **RadioDeCobertura** - definición, sinónimos, ejemplo
- **Direccion** - definición con coordenadas, sinónimos, ejemplo cálculo distancia
- **ExencionResponsabilidad** - definición legal, sinónimos, ejemplo acuerdo parcial
- **TipoExencion** - clasificación 3 niveles, sinónimos, ejemplo por tipo de planta

**Tabla anti-patrones actualizada:**
| No usar | Usar |
|---------|------|
| Tienda/Almacén | Vivero |
| DespachoDirecto | IntermediacionVivero |
| EnvíoPropio | EntregaViveroCercano |

---

### 4.2 Checklist Entrega 2 (`docs/entrega_dos/checklist_entrega_dos.md`)
**Ubicación:** `docs/entrega_dos/checklist_entrega_dos.md`

**Marcados como completados [x]:**
- Actualización del lenguaje ubicuo y entidades de dominio para reflejar el modelo de intermediario (vivero cercano)
- Implementación de la regla de negocio / Value Object para exención o acuerdo parcial de responsabilidad por daños en transporte
- Pruebas unitarias del dominio actualizadas y pasando en verde

---

## 5. Tests de Dominio Preexistentes (Compatibilidad)

### 5.1 KitJardineriaTest (`domain/kit/KitJardineriaTest.java`)
- **Sin cambios** - pruebas de VO Precio, Entidad ArticuloKit, Agregado KitJardineria siguen pasando

### 5.2 ApplicationTests (`ApplicationTests.java`)
- **Sin cambios** - test de contexto Spring sigue pasando

---

## 6. Verificación Final

**Comando ejecutado:** `./gradlew test --no-daemon`

**Resultado:** `BUILD SUCCESSFUL` - 53 tests completados, 0 fallos, 1 omitido

**Cobertura de dominio:**
- `DireccionTest`: 9 tests
- `ViveroTest`: 9 tests
- `ExencionResponsabilidadTest`: 13 tests
- `ClienteTest`: 5 tests
- `PedidoTest`: 8 tests (actualizados + nuevos)
- `KitJardineriaTest`: 9 tests (preexistentes)
- `ApplicationTests`: 1 test

---

## 7. Arquitectura Hexagonal - Separación de Capas

**Dominio puro (sin dependencias externas):**
```
domain/
├── common/
│   ├── Direccion.java (VO)
│   └── ReglaDominioException.java
├── cliente/
│   ├── Cliente.java (Entidad)
│   └── Email.java (VO)
├── ejemplar/
│   ├── Ejemplar.java, EjemplarRepository.java, ...
├── especie/
│   ├── Especie.java, FichaDeCuidado.java, Riesgo.java, ...
├── kit/
│   ├── KitJardineria.java, ArticuloKit.java, Precio.java, KitJardineriaRepository.java
├── pedido/
│   ├── Pedido.java, DetallePedido.java, EstadoPedido.java, ExencionResponsabilidad.java (VO)
└── vivero/
    ├── Vivero.java (Entidad/Agregado)
    └── ViveroRepository.java (Puerto)
```

**Puertos de aplicación (application/ports/output/):**
- `InventarioPort`, `PasarelaPagoPort`, `PedidoRepositoryPort`, `CatalogoArticulosPort`, `CarritoComprasPort` - **sin cambios**

**Próximos pasos sugeridos (capa aplicación/infraestructura):**
1. Implementar `ViveroRepository` en infraestructura (JPA)
2. Casos de uso: `AsignarViveroUseCase`, `ProcesarCompraConViveroUseCase`
3. Entidades JPA + Mappers bidireccionales
4. Consultas avanzadas por vivero
5. Seguridad JWT con roles COMPRADOR/VENDEDOR_VIVERO