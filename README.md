# Ecommerce Jardineria

Plataforma de e-commerce para intermediación de viveros y venta de plantas/kits de jardinería.

## 🏗️ Arquitectura

Este proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)** con **Lenguaje Ubicuo** del dominio de viveros y jardinería.

```
src/
├── main/
│   ├── java/com/uniquindio/ecommerce/
│   │   ├── domain/                    # Capa de Dominio (pura, sin dependencias externas)
│   │   │   ├── vivero/                # Entidades: Vivero, ViveroRepository
│   │   │   ├── cliente/               # Entidades: Cliente, Email, Direccion
│   │   │   ├── especie/               # Entidades: Especie, FichaDeCuidado, Riesgo
│   │   │   ├── ejemplar/              # Entidades: Ejemplar, Precio, EtapaCrecimiento
│   │   │   ├── kit/                   # Entidades: KitJardineria, ArticuloKit
│   │   │   ├── pedido/                # Entidades: Pedido, DetallePedido, ExencionResponsabilidad
│   │   │   ├── common/                # Value Objects compartidos
│   │   │   └── repository/            # Puertos de salida (Interfaces)
│   │   │
│   │   ├── application/               # Capa de Aplicación (Casos de Uso)
│   │   │   ├── ports/
│   │   │   │   ├── input/             # Use Cases (Puertos de entrada)
│   │   │   │   └── output/            # Repositories, Ports (Puertos de salida)
│   │   │   ├── services/              # Implementación de Casos de Uso
│   │   │   ├── dto/                   # Comandos/Queries
│   │   │   └── testsupport/           # Stubs para testing
│   │   │
│   │   └── infrastructure/            # Capa de Infraestructura (Adaptadores)
│   │       ├── persistence/
│   │       │   ├── entity/            # Entidades JPA
│   │       │   ├── mapper/            # Domain ↔ JPA Mappers
│   │       │   ├── repository/
│   │       │   │   ├── jpa/           # Spring Data JPA Repositories
│   │       │   │   │   └── custom/    # Queries complejas (Criteria API)
│   │       │   │   └── ...            # Adaptadores implementando puertos
│   │       │   └── ...
│   │       └── security/
│   │           ├── config/            # Spring Security + JWT
│   │           ├── jwt/               # Utilidades JWT
│   │           ├── controller/        # Endpoints REST
│   │           ├── service/           # AuthService, UserDetailsService
│   │           ├── repository/        # UsuarioRepository
│   │           ├── entity/            # UsuarioEntity
│   │           └── dto/               # Login/Register/AuthResponse
│   │
│   └── resources/
│       ├── application.yml            # Configuración principal
│       ├── application-test.yml       # Configuración para tests (H2)
│       └── data.sql                   # Datos iniciales
│
└── test/
    └── java/...                       # Pruebas unitarias e integración
```

## 🚀 Tecnologías

- **Java 21** + **Spring Boot 3.3.5**
- **Spring Data JPA** + **MariaDB** (producción) / **H2** (tests)
- **Spring Security** + **JWT (jjwt 0.12.3)**
- **BCrypt** para encriptación de contraseñas
- **Lombok** para boilerplate
- **JUnit 5** + **Mockito** + **AssertJ** + **MockMvc**
- **Gradle** como build tool

## 🎯 Funcionalidades Implementadas

### Fases 1-2: Dominio y Aplicación
- ✅ Modelo de negocio: Intermediación de viveros cercanos
- ✅ Exención de responsabilidad (Total / Parcial / Sin exención)
- ✅ 6 Casos de uso: RegistrarCliente, CrearPedido, ProcesarCompra, ConfigurarKit, ConsultarEjemplares, etc.
- ✅ Puertos de entrada/salida bien definidos

### Fase 3: Infraestructura y Persistencia JPA
- ✅ Entidades JPA separadas del dominio (12 entidades + 6 embeddables + 7 enums)
- ✅ Mappers bidireccionales Domain ↔ JPA
- ✅ Soft delete con `@SQLDelete` + `@Where`
- ✅ `data.sql` con datos reales: 5 viveros, 7 clientes, 8 especies, 24 ejemplares, 15 artículos kit, 5 kits, 7 pedidos

### Fase 4: Consultas Avanzadas
- ✅ Paginación con filtros dinámicos (Criteria API + JPQL)
- ✅ Búsqueda de plantas por nombre/especie/precio/disponibilidad
- ✅ Estadísticas por vivero: JOIN + GROUP BY (ventas, pedidos por estado, promedios)
- ✅ Top productos vendidos, ingresos por mes

### Fase 5: Seguridad
- ✅ Spring Security + JWT (HS256, 24h expiración)
- ✅ Registro/Login con validación
- ✅ BCrypt strength 12
- ✅ Roles: `COMPRADOR`, `VENDEDOR_VIVERO`, `ADMIN`
- ✅ Protección de rutas por rol

### Fase 6-7: Pruebas Avanzadas
- ✅ Pruebas unitarias: Dominio, Servicios, JWT, AuthService (Mockito)
- ✅ `@DataJpaTest` para repositorios
- ✅ `MockMvc` + JWT para controladores
- ✅ Verificación `verify(..., never())` en casos de error
- ✅ 32+ pruebas unitarias pasando sin BD externa

#### Probar un endpoint con seguridad (401, 403 y 200)

- **Códigos esperados:** se valida que un endpoint responda **401** cuando no hay autenticación, **403** cuando hay identidad pero falta el rol, y **200** cuando el rol autorizado accede; en `RoleBasedAccessControllerTest`, `sinToken_noDebeAccederAEndpointsDeComprador` y `tokenMalformado_debeRetornar401` cubren el 401, `vendedor_sinRolComprador_noDebeAccederAEndpointsDeComprador` cubre el 403 y `comprador_conTokenValido_debeAccederAEndpointsDeComprador` cubre el 200.
- **Simulación sin login real:** `spring-security-test` permite usar `@WithMockUser(roles = "COMPRADOR")` para inyectar un usuario ficticio en el `SecurityContext`, o `jwt()` para construir un `JwtAuthenticationToken` con los claims y authorities deseados, evitando ejecutar el flujo completo de autenticación.
- **Aplicación en este proyecto:** además de la simulación, `RoleBasedAccessControllerTest` genera tokens firmados con `JwtUtil` y los envía en `Authorization: Bearer ...`, de modo que la prueba también ejercita el `JwtAuthenticationFilter` real y no solo el `SecurityContext`.
- **Cobertura del 401:** `AuthControllerIntegrationTest.validate_sinToken_debeRetornar401` y `validate_conTokenInvalido_debeRetornar401`, junto con `authorizationHeaderSinBearer_debeRetornar401`, `authorizationHeaderVacio_debeRetornar401`, `usuarioInactivo_debeRetornar401` y `usuarioEliminadoSoftDelete_debeRetornar401`.
- **Cobertura del 403:** `comprador_sinRolVendedor_noDebeAccederAEndpointsDeVendedor`, `vendedor_sinRolComprador_noDebeAccederAEndpointsDeComprador`, `comprador_sinRolAdmin_noDebeAccederAEndpointsDeAdmin` y `vendedor_sinRolAdmin_noDebeAccederAEndpointsDeAdmin` demuestran que estar autenticado no implica tener permiso.
- **Cobertura del 200:** `admin_conRolComprador_debeAccederAEndpointsDeComprador`, `admin_conRolVendedor_debeAccederAEndpointsDeVendedor`, `admin_conTokenValido_debeAccederAEndpointsDeAdmin` y `vendedor_debeAccederAEndpointEstadisticas` confirman que la matriz de roles deja pasar solo lo permitido.
- **Por qué validar los tres:** si solo se probara el 200, un endpoint abierto por error pasaría desapercibido; los 401 y 403 convierten la seguridad en una especificación ejecutable de acceso por rol.

#### `@DataJpaTest` contra `@SpringBootTest`

- **Alcance reducido:** `@DataJpaTest` configura únicamente JPA, repositorios, entidades y un DataSource embebido H2; no carga controladores, servicios ni seguridad, por lo que arranca rápido y aísla la capa de persistencia.
- **Transaccionalidad:** cada prueba de `@DataJpaTest` corre dentro de una transacción que se revierte al finalizar; la propia anotación ya incluye `@Transactional`, así que no hace falta añadirla manualmente.
- **Alcance completo:** `@SpringBootTest` levanta todo el contexto de la aplicación con sus beans y capas, útil para integración pero más lento y sensible a configuraciones globales como seguridad, filtros y perfiles.
- **En este proyecto:** `EjemplarJpaRepositoryTest` usa `@DataJpaTest` con `TestEntityManager` y valida consultas concretas como `findWithFilters_debeFiltrarPorRangoPrecio`, `estadisticasPrecioPorEspecie_debeRetornarPromedioMinMax` y `softDelete_debeMarcarComoEliminadoSinBorrarFisicamente`; en cambio, `AuthControllerIntegrationTest` y `RoleBasedAccessControllerTest` usan `@SpringBootTest` porque necesitan el flujo completo.
- **Cambio en Spring Boot 4:** la anotación pasó de `org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest` a `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest` y requiere la dependencia `spring-boot-starter-data-jpa-test`; el proyecto actual en Boot 3.3.5 sigue usando el paquete clásico.
- **Uso de `@Import`:** `@DataJpaTest` no escanea adaptadores propios como `EjemplarRepositoryAdapter`, mappers o implementaciones custom; cuando una prueba necesita esos componentes se declaran con `@Import(EjemplarRepositoryAdapter.class)` para incorporarlos al contexto reducido sin cargar toda la aplicación.
- **Regla práctica:** `@DataJpaTest` para probar repositorios y mapeo JPA de forma aislada; `@SpringBootTest` cuando se quiere verificar la integración real entre controlador, caso de uso, persistencia y seguridad.

#### Prueba de integración

- **Recorrido completo:** una prueba de integración entra por el controlador, atraviesa el caso de uso, llega al adaptador de persistencia y termina en la base H2, verificando el comportamiento real y no piezas aisladas.
- **Sin mocks de Mockito:** no se reemplazan dependencias; se usa el cableado real de Spring, de modo que un puerto mal implementado, un bean faltante o un mapeo incorrecto hacen fallar la prueba.
- **Flujo de autenticación:** `AuthControllerIntegrationTest` cubre `register_debeRegistrarNuevoUsuarioYRetornarToken`, `register_conEmailDuplicado_debeRetornar422`, `login_conCredencialesValidas_debeRetornarToken`, `login_conPasswordIncorrecto_debeRetornar401` y `validate_conTokenValido_debeRetornar200` sobre `/api/auth`.
- **Seguridad integrada:** `RoleBasedAccessControllerTest` completa el recorrido al combinar `MockMvc`, tokens JWT reales y el filtro de seguridad, comprobando que el rol se propaga correctamente desde el token hasta la autorización del endpoint.
- **Aislamiento con `@Transactional`:** cada test se ejecuta en una transacción que Spring revierte al terminar, por lo que los usuarios y datos creados en `setUp` no contaminan otros tests ni la base compartida.
- **Sin dependencia del orden:** como cada prueba deja la base como la encontró, se pueden ejecutar en cualquier orden y repetir sin limpiezas manuales de tablas.
- **Valor en arquitectura hexagonal:** la integración demuestra que el hexágono está correctamente conectado: el adaptador de entrada traduce la petición, el caso de uso aplica las reglas y el adaptador de salida persiste, todo con la seguridad y el manejo de errores reales.

## 📦 Endpoints Principales

### Autenticación (Públicos)
```
POST   /api/auth/register     # Registro de usuario
POST   /api/auth/login        # Login, retorna JWT
GET    /api/auth/validate     # Validar token (requiere Bearer)
```

### Comprador (Requiere rol COMPRADOR)
```
GET    /api/comprador/perfil
GET    /api/comprador/pedidos
```

### Vendedor/Vivero (Requiere rol VENDEDOR_VIVERO)
```
GET    /api/vendedor/dashboard
GET    /api/vendedor/pedidos
GET    /api/vendedor/estadisticas
```

### Admin (Requiere rol ADMIN)
```
GET    /api/admin/usuarios
GET    /api/admin/estadisticas
```

## 🔧 Configuración y Ejecución

### Variables de entorno (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/ecommerce_jardineria
    username: root
    password: root

jwt:
  secret: <clave-secreta-256-bits-minimo>
  expiration: 86400000
```

### Ejecutar
```bash
# Compilar
./gradlew build

# Ejecutar tests unitarios (sin BD)
./gradlew test --tests "com.uniquindio.ecommerce.infrastructure.security.jwt.JwtUtilTest" \
                  --tests "com.uniquindio.ecommerce.infrastructure.security.service.AuthServiceTest" \
                  --tests "com.uniquindio.ecommerce.domain.*" \
                  --tests "com.uniquindio.ecommerce.application.services.*"

# Ejecutar TODOS los tests (requiere MariaDB corriendo)
docker run -d -p 3306:3306 -e MARIADB_ROOT_PASSWORD=root -e MARIADB_DATABASE=ecommerce_jardineria mariadb:latest
./gradlew test

# Ejecutar aplicación
./gradlew bootRun
```

## 📚 Documentación de Entregas

- [Fase 3-4: Infraestructura y Consultas](./MAPEO_CAMBIOS_FASE3_FASE4.md)
- [Fase 5: Consultas JPQL Avanzadas](./MAPEO_CAMBIOS_FASE5.md)
- [Fase 6: Spring Security + JWT](./MAPEO_CAMBIOS_FASE6.md)
- [Fase 7: Pruebas Avanzadas](./MAPEO_CAMBIOS_FASE7.md)

---

## 📋 Tabla de Correcciones - Entrega 1

> **Nota**: Complete esta sección con el feedback específico recibido de la docente en la Entrega 1.

| # | Observación de la Docente | Qué se Modificó | Archivo(s) Afectado(s) | Fase |
|---|---------------------------|-----------------|------------------------|------|
| 1 | *[Pendiente: agregar observación]* | *[Describir cambio realizado]* | `ruta/al/archivo.java` | 1-2 |
| 2 | *[Pendiente: agregar observación]* | *[Describir cambio realizado]* | `ruta/al/archivo.java` | 1-2 |
| 3 | *[Pendiente: agregar observación]* | *[Describir cambio realizado]* | `ruta/al/archivo.java` | 1-2 |
| 4 | *[Pendiente: agregar observación]* | *[Describir cambio realizado]* | `ruta/al/archivo.java` | 1-2 |

### Ejemplo de cómo llenar la tabla:

| # | Observación de la Docente | Qué se Modificó | Archivo(s) Afectado(s) | Fase |
|---|---------------------------|-----------------|------------------------|------|
| 1 | "Falta validación de email en Cliente" | Agregado Value Object `Email` con validación regex y factoría `Email.of()` | `domain/cliente/Email.java`, `domain/cliente/Cliente.java` | 1 |
| 2 | "Pedido no calcula total con exención" | Implementado `calcularTotalConExencion()` aplicando estrategia según tipo de exención | `domain/pedido/Pedido.java`, `domain/pedido/ExencionResponsabilidad.java` | 1 |
| 3 | "Casos de uso no separan puertos entrada/salida" | Refactorizado: interfaces en `application/ports/input|output`, implementaciones en `application/services` | `application/ports/...`, `application/services/*.java` | 2 |

---

## 👥 Autores

- Equipo de desarrollo - Programación Avanzada - Universidad del Quindío

## 📄 Licencia

Proyecto académico - Entrega 2