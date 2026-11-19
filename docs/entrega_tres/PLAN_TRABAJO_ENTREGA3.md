# 🌿 Plan de Trabajo - Entrega 3: Frontend Angular Conectado al Backend
**Proyecto:** E-commerce de Jardinería con Intermediación de Viveros Locales  
**Universidad del Quindío — Programación Avanzada**  
**Carpeta Frontend:** `jardineria-front/` | **Carpeta Backend:** raíz (`src/`)

> **Reglas estrictas para el Agente de IA en TODAS las tareas:**
> 1. **Lenguaje Ubicuo:** Usar SIEMPRE `Ejemplar`, `Especie`, `FichaDeCuidado`, `Riesgo`, `EtapaCrecimiento`, `EstadoDeSalud`, `Vivero`, `KitJardineria`, `Pedido`, `ExencionResponsabilidad`. **PROHIBIDO** usar `Producto`, `Item`, `Stock`, `Advertencia`.
> 2. **Arquitectura Hexagonal en el Backend:** Nunca importar Spring/JPA dentro de `domain/`. Controladores delgados que solo validan `@Valid` y delegan a puertos de entrada (`UseCase`).
> 3. **Angular 21 Moderno en `jardineria-front/`:** Componentes standalone **sin sufijo `.component`** (ej. `catalogo.ts`), uso de `signal()`, `computed()`, `inject(...)`, control de flujo `@if` / `@for`, y **cero URLs ni datos quemados** en los componentes.
> 4. **Identidad Visual (Paleta 60-30-10):** Base `#F6F4EC`, Primario `#234E36`, Acento `#C96A3B`, sobre **Bootstrap 5**.
> 5. Al finalizar cada tarea, el agente debe marcar `- [x]` en este archivo y esperar confirmación antes de pasar a la siguiente.

---

## FASE 0: Preparación, CORS en Backend y Buenas Prácticas Base
**Rama sugerida:** `feature/cors-config-base`  
**Criterios cubiertos:** 2.1 Conexión y CORS (15 pts) + Extra Buenas Prácticas (+2 pts)

### Tarea 0.1 — Habilitar CORS en el Backend (`SecurityConfig.java`)
- [x] Configurar `CorsConfigurationSource` en Spring Security permitiendo `http://localhost:4200`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Spring Boot con Arquitectura Hexagonal. Revisa `src/main/java/com/uniquindio/ecommerce/infrastructure/security/config/SecurityConfig.java`. Habilita CORS en `securityFilterChain` con un bean `CorsConfigurationSource` que permita el origen `http://localhost:4200`, los métodos `GET, POST, PUT, PATCH, DELETE, OPTIONS`, los encabezados `Authorization, Content-Type` y `allowCredentials(true)`. Asegúrate de no romper los endpoints ni los tests existentes ejecutando `./gradlew test`. Al terminar, marca como completada la Tarea 0.1 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `./gradlew test` pasa en verde sin errores.

### Tarea 0.2 — Buenas prácticas base Angular 21 en `jardineria-front/`
**Rama sugerida:** `feature/angular-base-setup`
**Criterios cubiertos:** Extra Buenas Prácticas (+2 pts)

- [x] Crear `src/environments/environment.ts` y `src/environments/environment.development.ts` con `apiUrl: 'http://localhost:8080/api'`.
- [x] Configurar `fileReplacements` en `angular.json` para desarrollo/producción.
- [x] Crear carpetas `src/app/modelos/`, `src/app/servicios/`, `src/app/interceptors/`, `src/app/guards/`.
- [x] Registrar `provideHttpClient(withInterceptors([]))` en `src/app/app.config.ts`.
- [x] Verificar que `npm run build` compila dentro de `jardineria-front`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: crea `src/environments/environment.ts` y `src/environments/environment.development.ts` con `apiUrl: 'http://localhost:8080/api'`; configura `fileReplacements` en `angular.json` para que desarrollo use `environment.development.ts`; crea las carpetas `src/app/modelos/`, `src/app/servicios/`, `src/app/interceptors/`, `src/app/guards/`; registra `provideHttpClient(withInterceptors([]))` en `src/app/app.config.ts` (listo para el interceptor JWT). Verifica que `npm run build` compila sin errores. Al terminar, marca como completada la Tarea 0.2 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 1.1 — Identidad visual Bootstrap 5 con paleta 60-30-10 en `jardineria-front/`
**Rama sugerida:** `feature/bootstrap-tema`
**Criterios cubiertos:** Extra Identidad Visual

- [x] Instalar `bootstrap` y `bootstrap-icons` con npm.
- [x] Registrar `node_modules/bootstrap/dist/css/bootstrap.min.css`, `node_modules/bootstrap-icons/font/bootstrap-icons.css` en `styles` y `node_modules/bootstrap/dist/js/bootstrap.bundle.min.js` en `scripts` de `angular.json`.
- [x] Aplicar paleta en `src/styles.css`: base `#F6F4EC` (`--bs-body-bg`), primario `#234E36` (`--bs-primary`, `.btn-primary`), acento `#C96A3B` (clases `.btn-terracota`, `.bg-terracota`, `.text-terracota`, `.badge-terracota`), texto `#1F2922`.
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: instala `bootstrap` y `bootstrap-icons` con npm; registra los CSS y JS de Bootstrap en `angular.json` (sin copiar archivos a mano); en `src/styles.css` sobrescribe variables de Bootstrap 5 con la paleta base #F6F4EC, primario #234E36, acento #C96A3B, texto #1F2922 y crea clases utilitarias `.btn-terracota`, `.bg-terracota`, `.text-terracota`, `.badge-terracota`. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 1.1 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 1.2 — Layout profesional y página de inicio en `jardineria-front/`
**Rama sugerida:** `feature/layout-inicio`
**Criterios cubiertos:** Frontend Conectado al Backend

- [x] Reemplazar `src/app/app.html` por layout con `<nav class="navbar navbar-expand-lg navbar-dark">` en Verde Bosque (#234E36), botón hamburguesa funcional, enlaces con `routerLink`/`routerLinkActive` (Inicio `/`, Catálogo de Ejemplares `/catalogo`, Armar Kit `/kits`, Iniciar Sesión `/login`, Registrarse `/registro`), `<main class="container py-4">` con `<router-outlet />` y `<footer>` con 'Universidad del Quindío - Programación Avanzada'.
- [x] Actualizar `src/app/app.ts` importando `RouterOutlet`, `RouterLink`, `RouterLinkActive` y usando `signal` para título y footer.
- [x] Maquetar `src/app/componentes/inicio/inicio.html` e `inicio.css` con Bootstrap 5 (`row`, `col-md-*`, `card`, `shadow-sm`, `rounded-3`, `btn`) reflejando intermediación con Viveros locales cercanos, Ficha de Cuidado garantizada y alertas de Riesgo de toxicidad para mascotas/niños. Cero palabras prohibidas (`Producto`, `Item`, `Stock`).
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: reemplaza `src/app/app.html` por un layout profesional con navbar en Verde Bosque (#234E36), botón hamburguesa, enlaces routerLink a /, /catalogo, /kits, /login, /registro; main con router-outlet y footer con 'Universidad del Quindío - Programación Avanzada'. Actualiza `src/app/app.ts` importando RouterOutlet, RouterLink, RouterLinkActive y usando signal para título y footer. Maqueta `src/app/componentes/inicio/inicio.html` e `inicio.css` con Bootstrap 5 (row, col-md-*, card, shadow-sm, rounded-3, btn) sobre intermediación con Viveros locales cercanos, Ficha de Cuidado garantizada y alertas de Riesgo de toxicidad para mascotas/niños. Cero palabras prohibidas. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 1.2 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 2.1 — Servicio y componente de avisos globales en `jardineria-front/`
**Rama sugerida:** `feature/avisos-globales`
**Criterios cubiertos:** Frontend Conectado al Backend + Interfaz de usuario

- [x] Crear `src/app/servicios/aviso.service.ts` con `signal<Aviso[]>` para mantener avisos con tipo (`success | danger | warning | info`) y mensaje, métodos `mostrarExito(msg)`, `mostrarError(msg)`, `mostrarInfo(msg)` y `cerrar(id)`, y desaparición automática tras 4 segundos (`setTimeout`).
- [x] Crear componente standalone `src/app/componentes/avisos/` (`avisos.ts`, `avisos.html`, `avisos.css`, sin sufijo `.component`) que renderice alertas flotantes de Bootstrap (`alert`, `shadow`, `position-fixed top-0 end-0 m-3`) usando `@if` y `@for`, con botón de cierre manual.
- [x] Insertar `<app-avisos />` en `src/app/app.html` para disponibilidad global en la SPA.
- [x] Registrar `Avisos` en los `imports` de `src/app/app.ts`.
- [x] Verificar compilación con `npm run build`.

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 2.2 — Autenticación JWT en `jardineria-front/`
**Rama sugerida:** `feature/auth-jwt`
**Criterios cubiertos:** Frontend Conectado al Backend + Seguridad

- [x] Crear `src/app/modelos/auth.model.ts` con interfaces TypeScript idénticas a los DTOs del backend (`LoginRequest`, `RegisterRequest`, `AuthResponse`) más `UsuarioAutenticado`.
- [x] Crear `src/app/servicios/auth.service.ts` usando `inject(HttpClient)` y `environment.apiUrl` con:
  - `login(credenciales)` -> POST `${environment.apiUrl}/auth/login`, guarda token en `localStorage`, actualiza señales (`estaAutenticado`, `rolActual`, `correoUsuario`).
  - `registrar(datos)` -> POST `${environment.apiUrl}/auth/register`.
  - `cerrarSesion()` -> elimina token de `localStorage`, limpia señales al instante y redirige a `/login`.
  - `obtenerToken()` y método seguro para decodificar payload Base64 del JWT y extraer rol (`COMPRADOR`, `VENDEDOR_VIVERO`, `ADMIN`) y correo (claim `"roles"`, subject = email).
- [x] Actualizar `src/app/app.html` navbar para reaccionar en vivo a `authService.estaAutenticado()` y `authService.rolActual()`: sin sesión muestra 'Iniciar Sesión' y 'Registrarse'; con sesión muestra correo/rol y 'Cerrar Sesión'.
- [x] Inyectar `AuthService` en `src/app/app.ts`.
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: inspecciona los DTOs del backend (`LoginRequest`, `RegisterRequest`, `AuthResponse`) y `AuthController` + `JwtUtil` para conocer rutas exactas (`/api/auth/login`, `/api/auth/register`), campos y el claim de rol (`roles`). Crea `src/app/modelos/auth.model.ts` con interfaces TypeScript idénticas a los DTOs. Crea `src/app/servicios/auth.service.ts` usando `inject(HttpClient)` y `environment.apiUrl` con `login`, `registrar`, `cerrarSesion`, `obtenerToken` y decodificación segura del payload Base64 del JWT para extraer rol y correo. Actualiza `src/app/app.html` navbar para reaccionar en vivo a `estaAutenticado()` y `rolActual()`. Inyecta `AuthService` en `src/app/app.ts`. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 2.2 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 1.3 — Home (Inicio) basado en mockup `inicio_raiz_local.txt`
**Rama sugerida:** `feature/home-mockup`
**Criterios cubiertos:** Frontend Conectado al Backend + Identidad Visual

- [x] Reorganizar `src/app/componentes/inicio/` (`inicio.ts`, `inicio.html`, `inicio.css`) para reflejar el mockup `docs/mockups/inicio_raiz_local.txt`.
- [x] Crear `src/app/modelos/home.model.ts` con interfaces del dominio (`ViveroAliado`, `MetricaHome`, `BloqueValor`, `EjemplarDestacado`) usando lenguaje ubicuo.
- [x] Hero section con badge "Marketplace de Proximidad", headline "Plantas vivas desde el vivero más cercano a tu hogar", botones Explorar Catálogo (`/catalogo`) y Configurar Kit (`/kits`), y barra de métricas (+45 viveros aliados, 24h, 4.9/5).
- [x] 3 bloques de valor: Vivero Cercano Asignado, Ficha de Cuidado Garantizada, Alerta de Riesgo para Mascotas y Niños.
- [x] Banda viveros regionales con botón "Registrar mi Vivero" (`/registro`).
- [x] Uso de `signal()` y `computed()` para datos reactivos; cero URLs ni datos quemados en componentes; cero palabras prohibidas (`Producto`, `Item`, `Stock`, `Advertencia`).
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: lee el mockup `docs/mockups/inicio_raiz_local.txt` y reorganiza `src/app/componentes/inicio/` (`inicio.ts`, `inicio.html`, `inicio.css`) para reflejar ese diseño real usando Bootstrap 5 (no Tailwind), paleta 60-30-10, lenguaje ubicuo (`Ejemplar`, `Vivero`, `FichaDeCuidado`, `Riesgo`, etc.), `signal()` y `computed()`, y cero URLs ni datos quemados en componentes. Crea `src/app/modelos/home.model.ts` con las interfaces del dominio. Incluye hero con métricas, 3 bloques de valor y banda de viveros regionales. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 1.3 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 2.3 — Componentes de autenticación Login y Registro en `jardineria-front/`
**Rama sugerida:** `feature/login-registro`
**Criterios cubiertos:** Frontend Conectado al Backend + Interfaz de usuario + Seguridad

- [x] Implementar `src/app/componentes/login/` (`login.ts`, `login.html`, `login.css`) con `ReactiveFormsModule` (`FormBuilder`), validaciones (correo requerido y con formato, contraseña requerida), `signal` de carga y alternancia de visibilidad de la contraseña.
- [x] Integrar `login.ts` con `AuthService.login()` y `AuthService.guardarSesion()`; mostrar `AvisoService.mostrarExito(...)` y navegar a `/catalogo` tras autenticarse.
- [x] Traducir errores HTTP a mensajes del dominio visible en pantalla y en aviso: 401 credenciales incorrectas, 400 datos inválidos, 0 sin conexión con el servidor.
- [x] Implementar `src/app/componentes/registro/` (`registro.ts`, `registro.html`, `registro.css`) con `ReactiveFormsModule` y campos alineados a `RegisterRequest`: `nombre` (mín. 3), `email`, `password` (mín. 6) y `roles` (`COMPRADOR` / `VENDEDOR_VIVERO`).
- [x] Integrar `registro.ts` con `AuthService.registrar()` + `guardarSesion()`, aviso de éxito y redirección a `/catalogo`; traducir errores HTTP (409 correo ya registrado, 400 validación, 0 sin conexión).
- [x] Maquetar ambos formularios como tarjeta de dos paneles (panel editorial verde + formulario) según `docs/images/login.png`, con Bootstrap 5, paleta 60-30-10, `@if` para validaciones y lenguaje ubicuo.
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: implementa `src/app/componentes/login/` (`login.ts`, `login.html`, `login.css`) y `src/app/componentes/registro/` (`registro.ts`, `registro.html`, `registro.css`) con `ReactiveFormsModule`, validaciones, `signal` de carga y traducción de errores HTTP. Conecta login con `AuthService.login()` + `guardarSesion()` y registro con `AuthService.registrar()` + `guardarSesion()`, mostrando avisos con `AvisoService` y redirigiendo a `/catalogo` en caso de éxito. El registro debe reflejar los campos de `RegisterRequest` (nombre, email, password mínimo 6, roles COMPRADOR/VENDEDOR_VIVERO). Maqueta ambos formularios como la tarjeta de dos paneles de `docs/images/login.png` con Bootstrap 5, paleta 60-30-10 y lenguaje ubicuo. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 2.3 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 2.4 — Interceptor JWT y guards funcionales en `jardineria-front/`
**Rama sugerida:** `feature/interceptor-guards`
**Criterios cubiertos:** Frontend Conectado al Backend + Seguridad (Extra +3 pts con `rolGuard`)

- [x] Crear `src/app/interceptors/auth.interceptor.ts` (`HttpInterceptorFn`): si `AuthService.obtenerToken()` devuelve token, clona la petición con `Authorization: Bearer <token>` (ningún servicio ni componente escribe el header a mano).
- [x] En `catchError`, ante 401 que no sea `POST /auth/login`: llamar a `AuthService.cerrarSesion()`, mostrar con `AvisoService` "Tu sesión ha vencido o no es válida. Inicia sesión nuevamente." y redirigir a `/login`.
- [x] Registrar `authInterceptor` en `provideHttpClient(withInterceptors([authInterceptor]))` dentro de `src/app/app.config.ts`.
- [x] Crear `src/app/guards/auth.guard.ts`: permite el paso solo con sesión activa; si no, avisa y redirige a `/login`.
- [x] Crear `src/app/guards/no-auth.guard.ts`: impide entrar a `/login` y `/registro` con sesión activa y redirige a `/catalogo`.
- [x] Crear `src/app/guards/rol.guard.ts` (Extra +3 pts): fábrica `rolGuard(rolesPermitidos: string[])` que valida el rol del JWT (normalizando `ROLE_` y mayúsculas); si no coincide, muestra aviso de acceso denegado y redirige a `/catalogo`.
- [x] Aplicar `noAuthGuard` a las rutas `/login` y `/registro` en `src/app/app.routes.ts`.
- [x] Corregir tonos azules residuales de Bootstrap (`.btn-primary`, `.btn-outline-primary`, enlaces y checkbox) sobrescribiendo las variables compiladas en `src/styles.css`, ya que cambiar `--bs-primary` en `:root` no recolorea esos componentes.
- [x] Verificar compilación con `npm run build`.

**Prompt para el Agente:**
> "Actúa como desarrollador senior de Angular 21. En `jardineria-front/`: crea el interceptor funcional `src/app/interceptors/auth.interceptor.ts` (`HttpInterceptorFn`) que, si `AuthService` tiene token, clona la petición agregando `Authorization: Bearer <token>` (ningún servicio ni componente debe escribir el header a mano); con `catchError`, si el backend responde 401 y no es la petición de login, llama a `AuthService.cerrarSesion()`, muestra con `AvisoService` 'Tu sesión ha vencido o no es válida. Inicia sesión nuevamente.' y redirige a `/login`. Registra `authInterceptor` en `provideHttpClient(withInterceptors([authInterceptor]))` en `src/app/app.config.ts`. Crea los guards funcionales `src/app/guards/auth.guard.ts` (solo con sesión, si no avisa y va a `/login`), `src/app/guards/no-auth.guard.ts` (impide `/login` y `/registro` con sesión, redirige a `/catalogo`) y `src/app/guards/rol.guard.ts` (fábrica `rolGuard(rolesPermitidos: string[])` que verifica el rol del JWT; si no coincide, avisa acceso denegado y redirige a `/catalogo`). Aplica `noAuthGuard` a `/login` y `/registro` en `src/app/app.routes.ts`. Verifica que `npm run build` compila. Al terminar, marca como completada la Tarea 2.4 en `docs/entrega_tres/PLAN_TRABAJO_ENTREGA3.md`."

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

---

## FASE 3: Catálogo de Ejemplares Conectado al Backend

### Tarea 3.1 — Modelo y servicio de catálogo con búsqueda paginada

**Rama sugerida:** `feature/catalogo-servicio`  
**Criterios cubiertos:** Frontend Conectado al Backend

- [x] Ajustar `CatalogoController` para mapear los resultados de `buscarPlantasPorNombreYPrecio` a `PlantaBusquedaDTO` y exponer el filtro `etapa`.
- [x] Crear `src/app/modelos/catalogo.model.ts` con `PlantaBusquedaDTO`, `FiltrosCatalogo` y `PaginaResponse<T>` respetando los nombres exactos del backend (`contenido`, `paginaActual`, `totalElementos`, `totalPaginas`).
- [x] Crear `src/app/servicios/catalogo.service.ts` con `HttpClient`, `HttpParams` y `environment.apiUrl`, método `buscarEjemplares(filtros, page, size)` contra `GET /api/catalogo/ejemplares`.
- [x] Verificar compilación con `npm run build`.

**Verificación:** `npm run build` compila en verde dentro de `jardineria-front`.

### Tarea 3.2 — Catálogo con cuadrícula, filtros con debounce y paginación real

**Rama sugerida:** `feature/catalogo-ui`  
**Criterios cubiertos:** Frontend Conectado al Backend + Interfaz de usuario

- [x] Maquetar `src/app/componentes/catalogo/` (`catalogo.ts`, `catalogo.html`, `catalogo.css`) conectado 100% a `CatalogoService`.
- [x] Implementar filtros reactivos con `signal` y `effect` con debounce de 350 ms para nombre de especie, etapa de crecimiento y rango de precio, reseteando a página 0 al filtrar.
- [x] Implementar paginación real del servidor con botones Anterior/Siguiente, texto `Página X de Y` y `size = 6`.
- [x] Renderizar cada ejemplar en tarjeta Bootstrap (`row-cols-1 row-cols-md-2 row-cols-lg-3 g-4`, `card h-100 shadow-sm rounded-3`) con especie, etapa en badge Terracota, precio en COP, disponibilidad y botón para iniciar pedido.
- [x] Mostrar spinner de carga y estado vacío con el mensaje `No se encontraron ejemplares con esos criterios`.
- [x] Verificar compilación con `npm run build`.

**Verificación:** En F12 → Network, al buscar y cambiar de página se envían peticiones con `page` y `size` a `localhost:8080`; la respuesta incluye `contenido` y `totalPaginas`. Los registros con `deleted = true` no aparecen porque la query filtra `e.deleted = false`.

---

## FASE 4: Operación del Negocio y Pedidos

### Tarea 4.1 — Modelo y servicio de pedidos + vista protegida `/pedidos`
**Rama sugerida:** `feature/operacion-negocio`
**Criterios cubiertos:** Frontend Conectado al Backend + Interfaz de usuario + Seguridad + Reglas de negocio

- [x] Extender `CrearPedidoRequest` y `CrearPedidoResponse` con exención de responsabilidad, porcentaje (1–100), términos, versión, aceptación y datos del vivero asignado.
- [x] Resolver el `idCliente` desde el usuario autenticado (`Principal` + `ClienteRepositoryPort.buscarPorEmail`) y validar cantidad mínima, porcentaje y aceptación de la exención en el backend.
- [x] Agregar manejadores de error 400/404 para cuerpos inválidos y recursos no encontrados; registrar el cliente seed `comprador@test.com` en `data.sql`.
- [x] Crear `src/app/modelos/pedido.model.ts` y `src/app/servicios/pedido.service.ts` con `crearPedido`, `cancelarPedido`, `procesarPago` y los DTOs exactos del backend.
- [x] Implementar `src/app/componentes/pedidos/` con `FormArray` de ejemplares, selección de exención, porcentaje condicional, aceptación obligatoria y confirmación con vivero y total.
- [x] Precargar el ejemplar desde `queryParam` y navegar desde el catálogo a `/pedidos`; proteger la ruta con `authGuard` y agregar “Mis Pedidos” al navbar.
- [x] Traducir errores HTTP 400, 403, 404, 422 y 0 a mensajes de dominio visibles en pantalla y con `AvisoService`.
- [x] Verificar `npm run build`, `.\gradlew.bat test` y flujo real de creación de pedido autenticado como `COMPRADOR`.

**Verificación:** `POST /api/pedidos` responde 201 con `idPedido`, `estado`, `total`, `idVivero` y `nombreVivero`; responde 400 para porcentaje fuera de rango, cantidad 0 o exención sin aceptar.

### Tarea 4.2 — Servicio y vista protegida `/inventario-vivero`
**Rama sugerida:** `feature/operacion-negocio`
**Criterios cubiertos:** Frontend Conectado al Backend + Interfaz de usuario + Seguridad (rol VENDEDOR/ADMIN)

- [x] Crear `src/app/modelos/inventario.model.ts` con `ActualizarEstadoSaludRequest` y `ActualizarEstadoSaludResponse`, alineados al backend.
- [x] Crear `src/app/servicios/inventario.service.ts` con `actualizarEstadoSalud(idEjemplar, request)` contra `PUT /api/inventario/ejemplar/{id}/estado-salud`.
- [x] Implementar `src/app/componentes/inventario-vivero/` con formulario reactivo de ID y estado (`SANO`, `EN_RECUPERACION`, `CON_PLAGA`, `MARCHITO`), validaciones, estado de carga y confirmación con disponibilidad.
- [x] Traducir errores HTTP 400, 403, 404, 422 y 0 a mensajes de dominio visibles en pantalla y con `AvisoService`.
- [x] Registrar la ruta `/inventario-vivero` con `[authGuard, rolGuard(['VENDEDOR', 'ADMIN'])]`; el guard normaliza `VENDEDOR` a la autoridad real `VENDEDOR_VIVERO` y el navbar muestra “Gestión de Vivero” solo para vendedor o administrador.
- [x] Verificar `npm run build`, `.\gradlew.bat test` y flujo real: VENDEDOR_VIVERO actualiza estado con 200, COMPRADOR recibe 403, ejemplar inexistente 404 y estado inválido 400.

**Verificación:** `PUT /api/inventario/ejemplar/{id}/estado-salud` responde 200 con `idEjemplar`, `estadoSalud`, `disponibleParaVenta` y `mensaje`; `MARCHITO` y `CON_PLAGA` retiran el ejemplar de la venta.
