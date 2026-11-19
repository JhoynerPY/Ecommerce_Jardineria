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

### Backend (Spring Boot)

Requisitos: **Java 21** y Gradle (usar el wrapper incluido).

**Opción A — H2 en memoria (perfil por defecto)**
```bash
# Windows PowerShell
.\gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```
- API: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa` (sin contraseña)
- `data.sql` se carga automáticamente al iniciar.

**Opción B — MariaDB 11 con Docker**
```bash
docker run --name mariadb-jardineria \
  -e MARIADB_ROOT_PASSWORD=root \
  -e MARIADB_DATABASE=jardineria_db \
  -p 3307:3306 \
  -v mariadb-jardineria-data:/var/lib/mysql \
  -d mariadb:11

# Windows PowerShell
.\gradlew.bat bootRun --args=--spring.profiles.active=mariadb

# Linux / macOS
./gradlew bootRun --args='--spring.profiles.active=mariadb'
```
- URL: `jdbc:mariadb://localhost:3307/jardineria_db`
- Usuario: `root` / contraseña: `root`
- El perfil `mariadb` usa `ddl-auto=update` y `spring.sql.init.mode=never`.

**Tests**
```bash
.\gradlew.bat test
```

### Frontend (Angular)
```bash
cd jardineria-front
npm install
npm start
```
- Aplicación: `http://localhost:4200`
- Build de producción: `npm run build`

> La sección **Entrega 3** incluye el detalle de usuarios de prueba, pantallas y funcionalidades.

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

## 🌱 Entrega 3 — Frontend Angular y Funcionalidad de Negocio

### Nicho de negocio

**Raíz Local** es un marketplace de proximidad para viveros y venta de plantas, macetas, sustratos y kits de jardinería. El sistema conecta compradores con viveros aliados, calcula cobertura por distancia, gestiona pedidos con exención de responsabilidad y permite armar kits personalizados con descuento por paquete.

### Integrantes

- **Jhoyner Santiago Páez Yate**
- **Jeffry Ceballos**

Programación Avanzada — Universidad del Quindío.

### Cómo ejecutar el proyecto completo

#### 🚀 Ejecución Rápida (Todo en Uno: MariaDB + Backend + Frontend)

**Windows PowerShell**
```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\start-all.ps1
```

**macOS / Linux**
```bash
chmod +x start-all.sh stop-all.sh
./start-all.sh
```

Los scripts verifican Docker, levantan MariaDB 11 en el puerto `3307`, inician el backend con el perfil `mariadb` en `8080`, cargan `data.sql` solo si la base está vacía y arrancan Angular en `4200`.

Si el frontend está en otra ruta:
```powershell
$env:FRONTEND_DIR = "C:\ruta\al\frontend"
.\start-all.ps1
```
```bash
FRONTEND_DIR=/ruta/al/frontend ./start-all.sh
```

Para detener todo (backend, frontend y contenedor MariaDB):
```powershell
.\stop-all.ps1
```
```bash
./stop-all.sh
```

URLs: Frontend `http://localhost:4200`, Backend `http://localhost:8080`, Swagger `http://localhost:8080/swagger-ui.html`.

**1. Backend (raíz del repositorio)**

```bash
# H2 en memoria (por defecto)
.\gradlew.bat bootRun

# MariaDB 11 con Docker
docker run --name mariadb-jardineria \
  -e MARIADB_ROOT_PASSWORD=root \
  -e MARIADB_DATABASE=jardineria_db \
  -p 3307:3306 \
  -v mariadb-jardineria-data:/var/lib/mysql \
  -d mariadb:11
.\gradlew.bat bootRun --args=--spring.profiles.active=mariadb
```

**2. Frontend**

```bash
cd jardineria-front
npm install
npm start
```

Abrir `http://localhost:4200`. El backend debe estar corriendo en `http://localhost:8080`.

### Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| `admin@raizlocal.com` | `Admin123*` | `ADMIN` |
| `vendedor@raizlocal.com` | `Vendedor123*` | `VENDEDOR_VIVERO` |
| `comprador@raizlocal.com` | `Comprador123*` | `COMPRADOR` |
| `comprador@test.com` | `password123` | `COMPRADOR` |
| `vendedor@test.com` | `password123` | `VENDEDOR_VIVERO` |
| `admin@test.com` | `password123` | `ADMIN` (+ `COMPRADOR`, `VENDEDOR_VIVERO`) |
| `maria.gonzalez@email.com` | `password123` | `COMPRADOR` |
| `contacto@eljardin.com` | `password123` | `VENDEDOR_VIVERO` |

> En el código el rol de vendedor es `VENDEDOR_VIVERO`; la rúbrica lo abrevia como `VENDEDOR`. Los tres primeros usuarios son creados por `DataInitializer` con contraseñas cifradas mediante `PasswordEncoder`.

### Pantallas y rutas de Angular

| Ruta | Pantalla | Acceso |
|---|---|---|
| `/` | Inicio | Pública |
| `/login` | Iniciar sesión | Pública (redirige al catálogo si ya hay sesión) |
| `/registro` | Registro | Pública (redirige al catálogo si ya hay sesión) |
| `/catalogo` | Catálogo de ejemplares | Pública |
| `/pedidos` | Mis pedidos / crear pedido | Privada (`authGuard`) |
| `/kits` | Configurador de kits de jardinería | Privada (`authGuard`) |
| `/perfil` | Mi perfil, pedidos y kits guardados | Privada (`authGuard`) |
| `/inventario-vivero` | Gestión de vivero / estado de salud | Privada por rol (`VENDEDOR_VIVERO` / `ADMIN`) |
| `**` | Redirección a inicio | — |

### Funcionalidades de la Entrega 3

- Autenticación JWT, guards funcionales y `rolGuard`.
- Catálogo paginado con filtros y búsqueda contra el backend.
- Creación de pedidos con exención de responsabilidad y validaciones.
- Panel de vendedor para actualizar `EstadoDeSalud` de ejemplares.
- Configurador de kits con selección múltiple de ArticuloKit, cantidades, cálculo en vivo, descuento y persistencia real.
- Perfil MariaDB listo para Docker; perfil por defecto H2 intacto para desarrollo y pruebas.

---

## 👥 Autores

- **Jhoyner Santiago Páez Yate**
- **Jeffry Ceballos**

Equipo de desarrollo — Programación Avanzada — Universidad del Quindío.

## 📄 Licencia

Proyecto académico — Entrega 3