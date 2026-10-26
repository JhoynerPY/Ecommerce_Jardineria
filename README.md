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