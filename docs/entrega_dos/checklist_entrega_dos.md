# 📋 Checklist de Cumplimiento - Entrega 2 y Seguimiento (Programación Avanzada)

## 🌿 1. Modelo de Negocio e Intermediación de Viveros
- [x] Actualización del lenguaje ubicuo y entidades de dominio para reflejar el modelo de intermediario (vivero cercano).
- [x] Implementación de la regla de negocio / Value Object para exención o acuerdo parcial de responsabilidad por daños en transporte.
- [x] Pruebas unitarias del dominio actualizadas y pasando en verde.

## ⚙️ 2. Capa de Aplicación (Casos de Uso)
- [ ] Definición y separación de puertos de entrada (UseCases) y salida (Repositories).
- [ ] Implementación de los 6 casos de uso requeridos (mínimo 2 de comprador, 2 de vendedor/vivero).
- [ ] Pruebas unitarias de los casos de uso utilizando mocks.

## 🗄️ 3. Infraestructura y Persistencia (JPA)
- [ ] Creación de entidades JPA separadas de las entidades de dominio puro.
- [ ] Implementación de Mappers bidireccionales (Dominio <-> JPA).
- [ ] Configuración de repositorios Spring Data JPA con soporte de borrado lógico (soft delete).
- [ ] Script `data.sql` con datos iniciales funcionales en `src/main/resources`.

## 📊 4. Consultas Avanzadas
- [ ] Implementación de listados paginados con filtros dinámicos mediante JPQL.
- [ ] Consulta avanzada con `JOIN`, `GROUP BY` y funciones de agregación (ej. reportes por vivero).

## 🔒 5. Seguridad (Spring Security & JWT)
- [ ] Configuración de codificación de contraseñas con BCrypt.
- [ ] Implementación de filtros de autenticación JWT y endpoints de login/registro.
- [ ] Restricción de rutas basada en roles (`COMPRADOR`, `VENDEDOR_VIVERO`).
- [ ] Configuración de seguridad integrada en Swagger / OpenAPI.

## 🧪 6. Pruebas Avanzadas del Backend (Rúbrica)
- [ ] Pruebas de repositorios utilizando `@DataJpaTest`.
- [ ] Pruebas unitarias de controladores utilizando `MockMvc` con tokens JWT.
- [ ] Verificación de casos de error en controllers usando `verify(..., never())`.
- [ ] Verificación general de que todas las pruebas (mínimo 14) corren y pasan con éxito (`./gradlew test`).

## 📝 7. Cierre, Git y Documentación
- [ ] Historial de commits ordenados y limpios por cada avance atómico.
- [ ] Creación de la tabla de correcciones y retroalimentación de la Entrega 1 en el `README.md`.