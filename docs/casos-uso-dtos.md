# Documentación de Casos de Uso y DTOs

## Casos de Uso Identificados
| Caso de Uso | Actor | Repositorio Necesario | Descripción |
| :--- | :--- | :--- | :--- |
| **ConfigurarKitUseCase** | Comprador | `KitJardineriaRepository`, `CatalogoArticulosPort` | Permite al usuario armar un kit combinando planta, maceta y sustrato. |
| **ProcesarCompraUseCase** | Comprador | `PedidoRepositoryPort` | Genera un pedido a partir de los elementos seleccionados y define la entrega. |
| **ConsultarEjemplaresDisponibles** | Comprador | `EjemplarRepository` | Muestra al cliente las plantas aptas para la venta y con buen estado de salud. |
| **CancelarPedidoUseCase** | Comprador | `PedidoRepositoryPort` | Permite cancelar una compra si el pedido aún se encuentra en estado `PENDIENTE_PAGO`. |
| **RegistrarNuevoEjemplarUseCase** | Vendedor | `EjemplarRepository` | Ingresa un nuevo ejemplar al vivero, asignando estado de salud y etapa de crecimiento. |
| **ActualizarEstadoSaludUseCase** | Vendedor | `EjemplarRepository` | Permite al vendedor registrar si una planta enfermó o entró en cuarentena. |
| **DefinirFichaCuidadoUseCase** | Vendedor | `EspecieRepository` | Crea o edita los requerimientos de luz y riesgo de toxicidad para una especie. |

## DTOs (Request / Response) Diseñados

### 1. ConfigurarKitRequestDTO (Request)
* **Operación:** Mapea a la creación de un `KitJardineria` en `ConfigurarKitUseCase`.
* **Campos:** `idPlanta` (String), `idMaceta` (String), `idSustrato` (String), `idCliente` (String).
* **Justificación:** El dominio no debe recibir JSONs. Este DTO captura únicamente los identificadores crudos que la interfaz envía para que la capa de aplicación busque las entidades reales antes de instanciar el agregado.

### 2. ProcesarCompraRequestDTO (Request)
* **Operación:** Mapea a la creación de un `Pedido` en `ProcesarCompraUseCase`.
* **Campos:** `idCliente` (String), `detalles` (List<DetalleDTO>), `direccionEntrega` (String).
* **Justificación:** Agrupa los datos necesarios para formalizar la transacción y obliga a la capa externa a proveer una dirección de entrega válida antes de construir la raíz del agregado.

### 3. ResumenPedidoResponseDTO (Response)
* **Operación:** Respuesta de confirmación tras finalizar el `ProcesarCompraUseCase`.
* **Campos:** `idPedido` (String), `totalPagado` (double), `estadoActual` (String).
* **Justificación:** Evita exponer las entidades del dominio (`Pedido`) a la interfaz. Oculta lógica interna y entrega una vista plana con la información estrictamente necesaria para el cliente.