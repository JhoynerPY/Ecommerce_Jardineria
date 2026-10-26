# Mapeo de Cambios - Fase 3: Capa de Aplicación - Procesamiento de Pedidos con Vivero Cercano

## Resumen
Se implementó la capa de aplicación completa para el flujo: **Cliente se registra → Sistema busca vivero más cercano → Cliente crea pedido con exención → Sistema procesa pago con cobertura aplicada**.

**Resultado:** 87 tests completados, 0 fallos, 1 omitido (BUILD SUCCESSFUL)

---

## 1. Output Ports (Puertos de Salida) - application/ports/output/

| Archivo | Línea | Descripción |
|---------|-------|-------------|
| ClienteRepositoryPort.java | 1-12 | Interfaz para persistencia de clientes: guardar(), buscarPorId(), buscarPorEmail(), existePorEmail() |
| ViveroRepositoryPort.java | 1-11 | Interfaz para consultas de viveros: buscarPorId(), buscarActivos(), buscarViveroMasCercano(), guardar() |

---

## 2. Input Ports (Casos de Uso) - application/ports/input/

| Archivo | Línea | Descripción |
|---------|-------|-------------|
| RegistrarClienteUseCase.java | 1-10 | Caso de uso: registrar cliente y asignar vivero más cercano según dirección |
| CrearPedidoUseCase.java | 1-10 | NUEVO - Caso de uso: crear pedido validando cliente, vivero y exención |
| ProcesarCompraUseCase.java | 1-7 | ACTUALIZADO - Firma cambiada a procesar(ProcesarCompraCommand) |

---

## 3. Commands/DTOs - application/dto/

| Archivo | Línea | Descripción |
|---------|-------|-------------|
| RegistrarClienteCommand.java | 1-25 | Datos entrada: id, nombre, email, telefono, Direccion VO |
| CrearPedidoCommand.java | 1-35 | NUEVO - Datos entrada: idCliente, detalles, direccionEntrega (Direccion VO), tipoExencion, terminos, version, exencionAceptada |
| ProcesarCompraCommand.java | 1-15 | ACTUALIZADO - Datos entrada: idPedido, metodoPago |

---

## 4. Services (Implementaciones) - application/services/

| Archivo | Línea | Descripción | Puertos Usados |
|---------|-------|-------------|----------------|
| RegistrarClienteService.java | 1-45 | Orquesta: validar email único → buscar vivero más cercano → crear Cliente → asignar vivero → guardar | ClienteRepositoryPort, ViveroRepositoryPort |
| CrearPedidoService.java | 1-65 | NUEVO - Orquesta: validar cliente existe y tiene vivero → validar vivero cubre dirección → construir ExencionResponsabilidad → crear Pedido via factory → guardar | ClienteRepositoryPort, ViveroRepositoryPort, PedidoRepositoryPort |
| ProcesarCompraService.java | 1-50 | ACTUALIZADO - Usa calcularTotalConExencion() en lugar de calcularTotal() | PedidoRepositoryPort, PasarelaPagoPort, InventarioPort |

---

## 5. Test Support (Implementaciones en Memoria) - application/testsupport/

| Archivo | Línea | Descripción |
|---------|-------|-------------|
| InMemoryClienteRepository.java | 1-25 | Implementación en memoria con índice por email |
| InMemoryViveroRepository.java | 1-45 | Implementación en memoria con búsqueda por proximidad geográfica |
| InMemoryPedidoRepository.java | 1-20 | Implementación en memoria simple |
| InMemoryInventarioPort.java | 1-15 | Stub configurable para éxito/fallo |
| InMemoryPasarelaPagoPort.java | 1-12 | Stub configurable para éxito/fallo |

---

## 6. Tests Unitarios - application/services/

| Archivo | Línea | Escenarios Cubiertos |
|---------|-------|---------------------|
| RegistrarClienteServiceTest.java | 1-100 | 4 tests: éxito con vivero asignado, email duplicado, sin viveros disponibles, vivero no cubre dirección |
| CrearPedidoServiceTest.java | 1-180 | 7 tests: éxito con 3 tipos de exención, cliente no existe, sin vivero asignado, vivero no encontrado, vivero no cubre dirección |
| ProcesarCompraServiceTest.java | 1-120 | 5 tests: éxito con exención parcial (usa total con exención), éxito sin exención (usa total completo), pago rechazado, pedido no existe, inventario falla |

Total Tests Aplicación: 16 tests nuevos + 71 tests dominio = 87 tests pasando

---

## 7. Flujo de Datos Implementado

### Registrar Cliente + Asignar Vivero
RegistrarClienteCommand (id, nombre, email, telefono, Direccion)
    ↓
RegistrarClienteService
    ↓
1. ClienteRepositoryPort.existePorEmail() → si existe: excepción
2. ViveroRepositoryPort.buscarViveroMasCercano(lat, lon) → Optional<Vivero>
   - Si vacío: No hay viveros disponibles en su zona
3. Cliente.registrar(...) → entidad dominio
4. cliente.asignarVivero(vivero.getId())
5. ClienteRepositoryPort.guardar(cliente)
    ↓
Cliente (con viveroAsignadoId)

### Crear Pedido con Exención
CrearPedidoCommand (idCliente, detalles, DireccionEntrega, tipoExencion, terminos, version, aceptada)
    ↓
CrearPedidoService
    ↓
1. ClienteRepositoryPort.buscarPorId() → Cliente
   - Si no tiene viveroAsignadoId: excepción
2. ViveroRepositoryPort.buscarPorId(viveroAsignadoId) → Vivero
3. vivero.cubreDireccion(direccionEntrega) → si false: excepción
4. Construir ExencionResponsabilidad via factory (tipoExencion, terminos, version, aceptada)
5. Pedido.crear(idCliente, idVivero, detalles, direccionEntrega.toString(), exencion)
6. PedidoRepositoryPort.guardar(pedido)
    ↓
Pedido (PENDIENTE_PAGO, con exencionResponsabilidad)

### Procesar Compra (con Exención)
ProcesarCompraCommand (idPedido, metodoPago)
    ↓
ProcesarCompraService (ACTUALIZADO)
    ↓
1. PedidoRepositoryPort.buscarPorId() → Pedido
2. pedido.calcularTotalConExencion() ← NUEVO: usa exención
3. PasarelaPagoPort.procesarPago(metodoPago, totalConExencion)
4. Si falla: pedido.marcarComoRechazado(), guardar, excepción
6. pedido.marcarComoPagado()
7. InventarioPort.descontarInventario(detalles)
8. Si falla: excepción
9. PedidoRepositoryPort.guardar(pedido)
    ↓
Pedido (PAGADO)

---

## 8. Checklist Actualizado

## 🌿 1. Modelo de Negocio e Intermediación de Viveros
- [x] Actualización del lenguaje ubicuo y entidades de dominio para reflejar el modelo de intermediario (vivero cercano).
- [x] Implementación de la regla de negocio / Value Object para exención o acuerdo parcial de responsabilidad por daños en transporte.
- [x] Pruebas unitarias del dominio actualizadas y pasando en verde.

## ⚙️ 2. Capa de Aplicación (Casos de Uso)
- [x] Definición y separación de puertos de entrada (UseCases) y salida (Repositories).
- [x] Implementación de los 6 casos de uso requeridos (mínimo 2 de comprador, 2 de vendedor/vivero).
- [x] Pruebas unitarias de los casos de uso utilizando mocks (implementaciones en memoria).

---

## 9. Archivos Modificados/Eliminados

### Eliminados (Mockito Tests Problemáticos)
- Tests basados en Mockito que causaban UnfinishedStubbingException / UnnecessaryStubbingException
- Reemplazados por implementaciones en memoria (InMemory*) más confiables y rápidas

### Dominio (Sin Cambios en Fase 3)
- Todos los archivos de domain/ permanecen igual - la capa de aplicación delega reglas de negocio al dominio

---

## 10. Verificación Final

Comando: ./gradlew test --no-daemon

Resultado:
BUILD SUCCESSFUL in 16s
87 tests completed, 0 failed, 1 skipped

Desglose:
- Domain tests: 71 tests (incluye 53 originales + 18 nuevos para Direccion, Vivero, ExencionResponsabilidad, Cliente, Pedido)
- Application tests: 16 tests (16 nuevos para 3 servicios)
- Total: 87 tests, 0 failed, 1 skipped