# Mapeo de Cambios - Fase 6: Spring Security con JWT

## Resumen de Implementación

Este documento detalla todos los archivos creados y modificados durante la implementación de la **Fase 6 (Seguridad Spring Security + JWT)** del checklist de entrega.

---

## 📁 Archivos Creados

### Configuración de Seguridad (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/config`)

| Archivo | Descripción |
|---------|-------------|
| `SecurityConfig.java` | Configuración principal de Spring Security: filtros JWT, CORS, reglas de autorización por roles, BCrypt |

### JWT (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/jwt`)

| Archivo | Descripción |
|---------|-------------|
| `JwtUtil.java` | Utilidad para generar, validar y parsear tokens JWT (HS256, claims con roles) |
| `JwtAuthenticationFilter.java` | Filtro `OncePerRequestFilter` que intercepta requests, extrae Bearer token, valida y setea `SecurityContext` |

### Entidades (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/entity`)

| Archivo | Descripción |
|---------|-------------|
| `UsuarioEntity.java` | Entidad JPA que implementa `UserDetails`: email, password (BCrypt), nombre, roles (List<String>), activo, soft delete |

### Repositorios (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/repository`)

| Archivo | Descripción |
|---------|-------------|
| `UsuarioRepository.java` | Spring Data JPA: `findByEmailAndDeletedFalse`, `existsByEmailAndDeletedFalse`, queries por rol |

### Servicios (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/service`)

| Archivo | Descripción |
|---------|-------------|
| `UsuarioDetailsService.java` | Implementa `UserDetailsService` para cargar usuario por email desde BD |
| `AuthService.java` | Lógica de negocio: `registrar()` (valida email único, encripta password, asigna roles), `login()` (autentica con AuthenticationManager, genera JWT) |

### DTOs (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/dto`)

| Archivo | Descripción |
|---------|-------------|
| `RegisterRequest.java` | Email, password (mín 6 chars), nombre, roles opcional (default COMPRADOR) |
| `LoginRequest.java` | Email, password |
| `AuthResponse.java` | Token JWT, email, nombre, roles, tipoToken="Bearer" |

### Controladores (`../../src/main/java/com/uniquindio/ecommerce/infrastructure/security/controller`)

| Archivo | Descripción |
|---------|-------------|
| `AuthController.java` | `POST /api/auth/register` (público), `POST /api/auth/login` (público), `GET /api/auth/validate` (requiere token válido) |

---

## 📁 Archivos Modificados

### `../../build.gradle`
**Dependencias agregadas:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
testImplementation 'org.springframework.boot:spring-boot-starter-security-test'
```

### `../../src/main/resources/application.yml`
**Configuración JWT agregada:**
```yaml
jwt:
  secret: ecommerceJardineriaSecretKey2024ForJWTSigningWithSufficientLength123456789
  expiration: 86400000  # 24 horas en ms
```

### `../../src/main/resources/data.sql`
**Usuarios de prueba agregados (password: "password123" encriptado con BCrypt):**
```sql
INSERT INTO usuarios (id, email, password, nombre, roles, activo, deleted) VALUES
('usuario-001', 'comprador@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S', 'Comprador Test', '["COMPRADOR"]', true, false),
('usuario-002', 'vendedor@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S', 'Vendedor Test', '["VENDEDOR_VIVERO"]', true, false),
('usuario-003', 'admin@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S', 'Admin Test', '["ADMIN","COMPRADOR","VENDEDOR_VIVERO"]', true, false),
('usuario-004', 'maria.gonzalez@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S', 'María González', '["COMPRADOR"]', true, false),
('usuario-005', 'contacto@eljardin.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S', 'Vivero El Jardín', '["VENDEDOR_VIVERO"]', true, false);
```

---

## 🔐 Flujo de Autenticación y Autorización

### Registro (`POST /api/auth/register`)
```
1. Request: {email, password, nombre, roles?}
2. Validar email único en BD
3. Encriptar password con BCrypt (strength=12)
4. Crear UsuarioEntity con roles (default: ["COMPRADOR"])
5. Guardar en BD
6. Generar JWT con claims: {sub: email, roles: [...]}
7. Response: {token, email, nombre, roles, tipoToken: "Bearer"}
```

### Login (`POST /api/auth/login`)
```
1. Request: {email, password}
2. AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
3. DaoAuthenticationProvider -> UsuarioDetailsService.loadUserByUsername()
4. BCryptPasswordEncoder.matches(rawPassword, encodedPassword)
5. Si éxito -> generar JWT con roles del usuario
8. Response: {token, email, nombre, roles, tipoToken: "Bearer"}
```

### Request Autenticado
```
1. Header: Authorization: Bearer <jwt>
2. JwtAuthenticationFilter.doFilterInternal()
3. Extraer token, validar firma y expiración
4. JwtUtil.extractUsername() -> email
5. UsuarioDetailsService.loadUserByUsername(email)
6. Si válido -> UsernamePasswordAuthenticationToken en SecurityContext
7. Controller accede a principal con roles
```

### Autorización por Roles
```java
// En SecurityConfig
.requestMatchers("/api/comprador/**").hasRole("COMPRADOR")
.requestMatchers("/api/vendedor/**").hasRole("VENDEDOR_VIVERO")
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// En Controllers con @PreAuthorize
@PreAuthorize("hasRole('VENDEDOR_VIVERO')")
@GetMapping("/api/vendedor/pedidos")
```

---

## 🛡️ Configuración de Seguridad Detallada

### Endpoints Públicos
- `/api/auth/**` - Registro y login
- `/api/public/**` - Endpoints públicos futuros
- `/h2-console/**` - Consola H2 (dev)
- `/swagger-ui/**`, `/v3/api-docs/**` - Documentación API

### Endpoints Protegidos por Rol
| Ruta | Rol Requerido |
|------|---------------|
| `/api/comprador/**` | `COMPRADOR` |
| `/api/vendedor/**` | `VENDEDOR_VIVERO` |
| `/api/admin/**` | `ADMIN` |

### Configuración de Sesión
- **Stateless**: `SessionCreationPolicy.STATELESS` (no usa HttpSession)
- **CSRF**: Deshabilitado (API REST stateless)
- **CORS**: Configurado para permitir todos los orígenes/métodos/headers

### Encriptación de Contraseñas
- **Algoritmo**: BCrypt
- **Strength**: 12 (costo computacional alto)
- **Automático**: `PasswordEncoder` bean inyectado en `DaoAuthenticationProvider`

---

## 🔑 JWT Claims Structure

```json
{
  "sub": "usuario@email.com",
  "roles": ["COMPRADOR", "VENDEDOR_VIVERO"],
  "iat": 1700000000,
  "exp": 1700086400
}
```

- **Expiración**: 24 horas (configurable via `jwt.expiration`)
- **Algoritmo**: HS256
- **Clave**: 256+ bits (configurable via `jwt.secret`)

---

## ✅ Checklist Fase 6

- [x] **Spring Security configurado** - `SecurityConfig` con filter chain
- [x] **JWT implementado** - `JwtUtil` + `JwtAuthenticationFilter`
- [x] **Registro de usuarios** - `POST /api/auth/register` con validación
- [x] **Login** - `POST /api/auth/login` con `AuthenticationManager`
- [x] **BCrypt para passwords** - `BCryptPasswordEncoder(12)` bean
- [x] **Roles COMPRADOR y VENDEDOR_VIVERO** - En `UsuarioEntity.roles` y `SecurityConfig`
- [x] **Protección de rutas por roles** - `hasRole()` en `authorizeHttpRequests`
- [x] **UserDetailsService personalizado** - `UsuarioDetailsService` carga desde BD
- [x] **Soft delete en usuarios** - `@SQLDelete` + `deleted = false` en queries
- [x] **Compilación exitosa** - `./gradlew compileJava` ✓
- [x] **Tests pasando** - `./gradlew test` ✓

---

## 🔄 Integración con Capa de Aplicación

Los endpoints de la aplicación (Casos de Uso) pueden protegerse añadiendo:
```java
@RestController
@RequestMapping("/api/comprador")
@PreAuthorize("hasRole('COMPRADOR')")
public class CompradorController { ... }

@RestController
@RequestMapping("/api/vendedor")
@PreAuthorize("hasRole('VENDEDOR_VIVERO')")
public class VendedorController { ... }
```

El `SecurityContext` expone el `Authentication` con `getAuthorities()` que contiene los roles del JWT para decisiones de autorización programáticas.