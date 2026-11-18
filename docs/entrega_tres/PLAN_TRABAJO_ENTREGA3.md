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
