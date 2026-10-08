# Mapeo de la Rúbrica - Entrega 1: Ecommerce de Jardinería

Este documento detalla cómo y dónde se cumple cada uno de los mínimos exigidos por la rúbrica de la Entrega 1 dentro de nuestro código fuente[cite: 10].

## 1. Nicho y lenguaje ubicuo
*   **Exigencia:** Nicho propio definido + mínimo 5 términos (ninguno genérico).
*   **Ubicación:** `glosario-lenguaje-ubicuo.md`
*   **Evidencia:** Nuestro nicho es un vivero/jardinería especializada. Los términos definidos incluyen: *Ejemplar, Sustrato, Maceta, Ficha de Cuidado, Kit de Jardinería*. No utilizamos Producto, Comprador, Vendedor, ni Compra.

## 2. Reglas de negocio
*   **Exigencia:** Mínimo 5 reglas innegociables propias del nicho.
*   **Ubicación:** `glosario-lenguaje-ubicuo.md` y código de dominio.
*   **Evidencia:** Las reglas están documentadas y aplicadas. Ejemplos incluyen: "La maceta debe ser compatible con el diámetro de la planta", "Una planta enferma no puede ser vendida" y "Un pedido requiere una dirección de entrega válida para proceder".

## 3. Entidades
*   **Exigencia:** Mínimo 3-4, con constructor privado, sin setters, equals/hashCode por identidad.
*   **Ubicación:** `domain/cliente/Cliente.java`, `domain/kit/ArticuloKit.java`, `domain/ejemplar/Ejemplar.java`.
*   **Evidencia:** Las 3 entidades carecen de métodos *setters*. Su instanciación se hace a través de métodos de fábrica estáticos (ej. `crear()`), y la validación de igualdad (`equals`/`hashCode`) se realiza exclusivamente mediante su atributo de identidad (`idKit`, `idArticulo`, `idCliente`).

## 4. Value Objects
*   **Exigencia:** Mínimo 5-6, combinando enums y records con validación en el constructor.
*   **Ubicación:**
    *   Objetos con validación: `domain/kit/Precio.java` (valida que no sea negativo), `domain/cliente/Email.java` (valida formato).
    *   Enums: `domain/pedido/EstadoPedido.java`, `domain/ejemplar/EtapaCrecimiento.java`, `domain/ejemplar/EstadoDeSalud.java`, `domain/especie/NivelRiesgo.java`.
*   **Evidencia:** Tenemos 6 Value Objects inmutables que encapsulan lógica de formato y estado sin tener identidad propia.

## 5. Agregados principales
*   **Exigencia:** Mínimo 2 agregados identificados con mínimo 3 invariantes documentadas/protegidas cada uno.
*   **Ubicación:** `domain/kit/KitJardineria.java` y `domain/pedido/Pedido.java`.
*   **Evidencia:** Son conceptos genuinamente distintos (armado físico de plantas vs. transacción de compra).
    *   **Invariantes KitJardineria:** 1) Requiere planta, maceta y sustrato. 2) La maceta debe ser compatible con la planta. 3) No se puede cambiar a una maceta más pequeña.
    *   **Invariantes Pedido:** 1) Debe contener al menos un detalle. 2) Requiere dirección de entrega no vacía. 3) Solo se puede pagar si está en estado `PENDIENTE_PAGO`.

## 6. Excepción de dominio
*   **Exigencia:** 1 excepción propia usada consistentemente.
*   **Ubicación:** `domain/common/ReglaDominioException.java`
*   **Evidencia:** Es la única excepción que lanzamos en la capa de dominio cuando se rompe una regla de negocio o invariante, heredando de `RuntimeException` para no ensuciar las firmas de los métodos.

## 7. Pruebas unitarias
*   **Exigencia:** Mínimo 8 en total (2 de VO, 2 de Entidad, 4 de invariantes). Deben pasar en verde.
*   **Ubicación:** `test/../domain/kit/KitJardineriaTest.java` y `test/../domain/pedido/PedidoTest.java`.
*   **Evidencia:** Ejecutando `./gradlew test` pasan correctamente.
    *   2 de VO: Igualdad de Precio y fallo por precio negativo.
    *   2 de Entidad: Igualdad por ID en ArticuloKit.
    *   4 de Invariantes: Maceta incompatible (Kit), rechazo de cambio (Kit), pedido sin detalles (Pedido), y transición de estado inválida (Pedido).

## 8. Casos de uso
*   **Exigencia:** Mínimo 6-8 identificados y documentados.
*   **Ubicación:** `docs/casos-uso-dtos.md`
*   **Evidencia:** Se documentaron 7 casos de uso cubriendo interacciones del comprador (ej. *ConfigurarKitUseCase*, *ProcesarCompraUseCase*) y del vendedor (ej. *RegistrarNuevoEjemplarUseCase*), especificando el repositorio requerido.

## 9. Repository
*   **Exigencia:** Mínimo 2 interfaces, 1 implementación en memoria (HashMap).
*   **Ubicación:** `domain/repository/KitJardineriaRepository.java`, `application/ports/output/PedidoRepositoryPort.java` y `infrastructure/persistence/KitJardineriaRepositoryEnMemoria.java`.
*   **Evidencia:** Contamos con las dos interfaces exigidas (una para cada raíz) y la implementación en memoria con `HashMap` funciona perfectamente, manteniendo la capa de dominio libre de dependencias de Spring.

## 10. DTOs (Request/Response)
*   **Exigencia:** Mínimo 3 diseñados y documentados.
*   **Ubicación:** `docs/casos-uso-dtos.md`
*   **Evidencia:** Están documentados 2 Request (`ConfigurarKitRequestDTO`, `ProcesarCompraRequestDTO`) y 1 Response (`ResumenPedidoResponseDTO`), justificando por qué sus campos son necesarios y a qué operación mapean para no exponer las entidades al exterior.

## 11. Diagrama del agregado
*   **Exigencia:** Mínimo 1 diagrama mostrando raíz, límite y qué queda dentro/fuera.
*   **Ubicación:** `docs/diagrama-agregado.md`
*   **Evidencia:** Diagrama desarrollado en Mermaid renderizado nativamente en GitHub[cite: 10], mostrando a `Pedido` como raíz, `DetallePedido` y `EstadoPedido` dentro del límite, y `Cliente` junto con `Ejemplar` fuera del límite (referenciados solo por ID).

## 12. Estructura de paquetes
*   **Exigencia:** domain/application/infrastructure creada y subida al repositorio[cite: 10].
*   **Ubicación:** Todo el proyecto (`src/main/java/com/uniquindio/ecommerce/`).
*   **Evidencia:** Arquitectura de software organizada estrictamente en capas (Domain, Application, Infrastructure). Se trabajó mediante ramas integradas (`feature-jeffry-procesar-compra`, `feature-nuevo-valueobjets-entidades`, etc.) garantizando los commits distribuidos.

## 13. Tablero de trabajo (Jira o Trello)
*   **Exigencia:** Opcional (Suma puntos extra).
*   **Ubicación:** [https://proyecto-p-avanzada-ecommerce-jardineria.atlassian.net?continue=https%3A%2F%2Fproyecto-p-avanzada-ecommerce-jardineria.atlassian.net%2Fwelcome%2Fsoftware&atlOrigin=eyJpIjoiYjI1YTM0MjE4N2E2NGIwNmFkZWE5ZGVmZWNiYjY5NWIiLCJwIjoiaiJ9]
*   **Evidencia:** Organizado por tarjetas asignadas por casos de uso.