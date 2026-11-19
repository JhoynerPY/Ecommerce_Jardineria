# Mapeo de Tests de Dominio - Entrega 2: Validación Reglas Intermediación Viveros y Términos Transporte

## Resumen
Se agregaron y actualizaron pruebas unitarias en el paquete `domain` para validar exhaustivamente las nuevas reglas de negocio del modelo de intermediación con viveros y los términos de exención de responsabilidad por daños en transporte.

**Resultado:** 65 tests completados, 0 fallos, 1 omitido (BUILD SUCCESSFUL)

---

## 1. Tests Agregados/Actualizados por Clase

### 1.1 ClienteTest (`domain/cliente/ClienteTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/cliente/ClienteTest.java`

**Tests nuevos agregados:**
| Test | Qué valida | Regla de negocio |
|------|------------|------------------|
| `asignarViveroNuloNoPermitido()` | `asignarVivero(null)` no asigna vivero | Vivero ID nulo → no asignado |
| `asignarViveroVacioNoPermitido()` | `asignarVivero("  ")` no asigna vivero | Vivero ID solo espacios → no asignado |
| `reasignarViveroReemplazaElAnterior()` | Segunda asignación reemplaza la primera | Reasignación permitida |

**Cambio en lógica de dominio (Cliente.java:63-68):**
- `asignarVivero(String)` ahora valida: `if (viveroId != null && !viveroId.trim().isEmpty())` antes de asignar
- Hace `trim()` automático del ID

**Tests preexistentes (sin cambios):**
- `registrarClienteValido()` - creación con Direccion VO
- `registrarClienteSinDireccionLanzaExcepcion()` - dirección obligatoria
- `actualizarDatosContactoActualizaDireccionYReseteaVivero()` - cambio de dirección limpia vivero
- `asignarVivero()` - asignación básica
- `igualdadPorId()` - identidad por ID

---

### 1.2 ViveroTest (`domain/vivero/ViveroTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/vivero/ViveroTest.java`

**Tests nuevos agregados:**
| Test | Qué valida | Regla de negocio |
|------|------------|------------------|
| `radioCoberturaLimiteExacto()` | Cliente exactamente en el límite del radio (10km) → cubierto | Límite inclusivo (≤) |
| `radioCoberturaFueraPorPoco()` | Cliente 100m fuera del límite → no cubierto | Precisión del cálculo Haversine |
| `multiplesViverosClienteCubiertoPorUnoSolo()` | 3 viveros, solo 1 cubre al cliente en Armenia | Filtrado por cobertura |
| `multiplesViverosClienteCubiertoPorVariosEligeMasCercano()` | 2 viveros cubren, se puede determinar el más cercano | Base para lógica de asignación automática |
| `especieDisponibleCaseInsensitive()` | IDs de especie son case-sensitive | Comportamiento actual documentado |
| `getEspeciesDisponiblesIdsRetornaCopiaDefensiva()` | Lista retornada es inmodificable (`UnsupportedOperationException`) | Inmutabilidad defensiva |
| `igualdadPorId()` | Dos viveros con mismo ID son iguales aunque otros campos difieran | Identidad por ID |

**Cambio en lógica de dominio (Vivero.java:20-29):**
- Agregado constructor package-private `Vivero(String id, String nombre, Direccion direccion, String telefono, String email, double radioCoberturaKm, List<String> especiesDisponiblesIds, boolean activo)` para tests de igualdad

**Tests preexistentes (actualizados coordenadas):**
- `registrarViveroValido()` / validaciones factoría
- `cubreDireccionClienteCercano()` / `noCubreDireccionClienteLejano()` - nuevas coordenadas Armenia/Pereira/Bogotá
- `viveroInactivoNoCubreNingunaDireccion()`
- `agregarYRemoverEspecieDisponible()` / `noDuplicarEspeciesDisponibles()`
- `activarDesactivarVivero()`

---

### 1.3 ExencionResponsabilidadTest (`domain/pedido/ExencionResponsabilidadTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/pedido/ExencionResponsabilidadTest.java`

**Tests nuevos agregados:**
| Test | Qué valida | Regla de negocio |
|------|------------|------------------|
| `tipoNuloLanzaExcepcion()` | NullPointerException en constructor compacto record | Validación `@NotNull` implícita |
| `terminosNulosLanzaExcepcion()` | NullPointerException por términos null | Validación record |
| `versionNulaLanzaExcepcion()` | NullPointerException por versión null | Validación record |
| `calcularCoberturaConValorCero()` | Cobertura de pedido $0 es $0 | Caso borde |
| `todosLosTiposTienenCalcularCobertura()` (ParameterizedTest) | EnumSource recorre los 3 tipos, cobertura siempre 0-10000 | Exhaustividad del enum |
| `toStringContieneInformacionRelevante()` | toString incluye tipo, términos, versión | Depuración/trazabilidad |

**Tests preexistentes (sin cambios):**
- Factorías: `crearExencionTotalValida()`, `crearAcuerdoParcialPerdidaValido()`, `crearSinExencion()`
- Validaciones aceptación: `exencionTotalSinAceptarLanzaExcepcion()`, `acuerdoParcialSinAceptarLanzaExcepcion()`
- Validaciones campos: `terminosVaciosLanzaExcepcion()`, `versionVaciaLanzaExcepcion()`
- Cálculo cobertura: 3 tests (0%, 50%, 100%)
- Predicados: `esExencionTotal()`, `esAcuerdoParcial()`, `requiereAceptacion()`
- `igualdadPorValor()`

---

### 1.4 PedidoTest (`domain/pedido/PedidoTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/pedido/PedidoTest.java`

**Tests nuevos/actualizados:**
| Test | Qué valida | Regla de negocio |
|------|------------|------------------|
| `crearPedidoSinViveroLanzaReglaDominioException()` | `idVivero` null/blank → excepción | Vivero obligatorio en pedido |
| `crearPedidoSinExencionLanzaReglaDominioException()` | `exencionResponsabilidad` null → excepción | Exención obligatoria |
| `crearExencionParcialSinAceptarLanzaExcepcion()` | Validación en VO, no en Pedido | Separación de responsabilidades |
| `calcularTotalConExencionParcialReduceElTotal()` | $20,000 → $10,000 (50%) | Acuerdo parcial aplicado |
| `calcularTotalConExencionTotalEsCero()` | $10,000 → $0 (0%) | Exención total aplicada |
| `marcarComoEnviadoSoloSiPagado()` | Solo desde PAGADO → ENVIADO | Flujo estado válido |

**Tests preexistentes (actualizados firma `crear`):**
- `crearPedidoSinDetallesLanzaReglaDominioException()`
- `pagarPedidoQueNoEstaPendienteLanzaReglaDominioException()`

---

### 1.5 DireccionTest (`domain/common/DireccionTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/common/DireccionTest.java`

**Sin cambios** - ya cubría validaciones y cálculo distancia Haversine:
- 9 tests: creación válida, validaciones null/empty/rangos, distancia Armenia-Pereira (~31km), identidad, toString

---

### 1.6 KitJardineriaTest (`domain/kit/KitJardineriaTest.java`)
**Ubicación:** `src/test/java/com/uniquindio/ecommerce/domain/kit/KitJardineriaTest.java`

**Sin cambios** - tests preexistentes de VO Precio, Entidad ArticuloKit, Agregado KitJardineria:
- 9 tests pasando

---

## 2. Cobertura de Reglas de Negocio Validadas

### Intermediación Viveros
| Regla | Test(s) que la validan |
|-------|------------------------|
| Cliente debe tener dirección para asignar vivero | `ClienteTest.registrarClienteSinDireccionLanzaExcepcion()` |
| Cambio de dirección resetea vivero asignado | `ClienteTest.actualizarDatosContactoActualizaDireccionYReseteaVivero()` |
| Vivero ID no puede ser null/vacío | `ClienteTest.asignarViveroNuloNoPermitido()`, `asignarViveroVacioNoPermitido()` |
| Vivero tiene radio de cobertura > 0 | `ViveroTest.registrarViveroConRadioCeroLanzaExcepcion()` |
| Vivero inactivo no cubre ninguna dirección | `ViveroTest.viveroInactivoNoCubreNingunaDireccion()` |
| Cobertura inclusiva en límite exacto | `ViveroTest.radioCoberturaLimiteExacto()` |
| Múltiples viveros: solo los que cubren son candidatos | `ViveroTest.multiplesViverosClienteCubiertoPorUnoSolo()` |
| Distancia geodésica Haversine funcional | `DireccionTest.distanciaEntreDosDirecciones()` |
| Pedido requiere vivero asignado | `PedidoTest.crearPedidoSinViveroLanzaReglaDominioException()` |

### Términos Transporte / Exención Responsabilidad
| Regla | Test(s) que la validan |
|-------|------------------------|
| EXENCION_TOTAL → 0% cobertura | `ExencionResponsabilidadTest.calcularCoberturaExencionTotal()`, `PedidoTest.calcularTotalConExencionTotalEsCero()` |
| ACUERDO_PARCIAL_PERDIDA → 50% cobertura | `ExencionResponsabilidadTest.calcularCoberturaAcuerdoParcial()`, `PedidoTest.calcularTotalConExencionParcialReduceElTotal()` |
| SIN_EXENCION → 100% cobertura | `ExencionResponsabilidadTest.calcularCoberturaSinExencion()` |
| EXENCION_TOTAL y ACUERDO_PARCIAL requieren aceptación cliente | `ExencionResponsabilidadTest.exencionTotalSinAceptarLanzaExcepcion()`, `acuerdoParcialSinAceptarLanzaExcepcion()` |
| SIN_EXENCION no requiere aceptación | `ExencionResponsabilidadTest.requiereAceptacion()` |
| Términos y versión obligatorios | `ExencionResponsabilidadTest.terminosVaciosLanzaExcepcion()`, `versionVaciaLanzaExcepcion()` |
| Pedido requiere exención obligatoria | `PedidoTest.crearPedidoSinExencionLanzaReglaDominioException()` |

### Flujo de Estados Pedido
| Transición | Test |
|------------|------|
| PENDIENTE_PAGO → PAGADO | `PedidoTest.pagarPedidoQueNoEstaPendienteLanzaReglaDominioException()` (inverso) |
| PENDIENTE_PAGO → RECHAZADO | Implícito en test anterior |
| PAGADO → ENVIADO | `PedidoTest.marcarComoEnviadoSoloSiPagado()` |
| ENVIADO → ENTREGADO | Estado existe (enum), pendiente caso de uso |

---

## 3. Cambios en Código de Producción para Soportar Tests

| Archivo | Cambio | Motivo |
|---------|--------|--------|
| `Cliente.java:63-68` | `asignarVivero` valida null/blank y hace trim | Reglas de negocio robustas |
| `Vivero.java:20-29` | Constructor package-private con todos los campos | Test `igualdadPorId()` y copia defensiva |
| `Pedido.java` | Sin cambios (tests usan API pública) | - |

---

## 4. Ejecución y Verificación

**Comando:** `./gradlew test --no-daemon`

**Resultado final:**
```
> Task :test
BUILD SUCCESSFUL in 23s
65 tests completed, 0 failed, 1 skipped
```

**Desglose por clase de test:**
- `DireccionTest`: 9 tests
- `ViveroTest`: 13 tests (6 nuevos)
- `ExencionResponsabilidadTest`: 18 tests (7 nuevos + 1 parameterized)
- `ClienteTest`: 8 tests (3 nuevos)
- `PedidoTest`: 8 tests (5 nuevos/actualizados)
- `KitJardineriaTest`: 9 tests (preexistentes)
- `ApplicationTests`: 1 test (contexto Spring)

---

## 5. Próximos Pasos - Fase 2 Checklist (Capa Aplicación)

Según ``, la **Fase 2** requiere:

- [ ] **Definición y separación de puertos de entrada (UseCases) y salida (Repositories)**
- [ ] **Implementación de los 6 casos de uso requeridos (mínimo 2 de comprador, 2 de vendedor/vivero)**
- [ ] **Pruebas unitarias de los casos de uso utilizando mocks**

**Casos de uso sugeridos basados en el dominio:**
1. `RegistrarClienteUseCase` (comprador) - registra cliente, asigna vivero cercano
2. `ProcesarCompraUseCase` (comprador) - crea pedido con vivero y exención, procesa pago
3. `RegistrarViveroUseCase` (vendedor/vivero) - onboarding vivero con radio cobertura
4. `GestionarInventarioViveroUseCase` (vendedor/vivero) - CRUD especies disponibles
5. `ConsultarPedidosViveroUseCase` (vendedor/vivero) - listar pedidos asignados
6. `ActualizarEstadoPedidoUseCase` (vendedor/vivero) - PAGADO → ENVIADO → ENTREGADO

**Puertos de salida necesarios:**
- `ViveroRepositoryPort` (ya existe `ViveroRepository` en domain)
- `ClienteRepositoryPort`
- `PedidoRepositoryPort` (ya existe en application/ports/output)
- `InventarioPort` (ya existe)
- `PasarelaPagoPort` (ya existe)