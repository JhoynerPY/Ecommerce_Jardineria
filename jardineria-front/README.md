# Raíz Local — Frontend Angular

Frontend de **Raíz Local**, marketplace de proximidad para viveros y venta de plantas, macetas, sustratos y kits de jardinería.

## Integrantes

- **Jhoyner Santiago Páez Yate**
- **Jeffry Ceballos**

Programación Avanzada — Universidad del Quindío.

## Requisitos

- Node.js 20.19+ o 22.12+
- Backend ejecutándose en `http://localhost:8080`

## Ejecutar

```bash
npm install
npm start
```

Abrir `http://localhost:4200`.

Build de producción:

```bash
npm run build
```

## Usuarios de prueba

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

> Para levantar todo el ecosistema desde la raíz: `.\start-all.ps1` o `./start-all.sh`. Para detenerlo: `.\stop-all.ps1` o `./stop-all.sh`.

## Pantallas y rutas

| Ruta | Pantalla | Acceso |
|---|---|---|
| `/` | Inicio | Pública |
| `/login` | Iniciar sesión | Pública |
| `/registro` | Registro | Pública |
| `/catalogo` | Catálogo de ejemplares | Pública |
| `/pedidos` | Mis pedidos / crear pedido | Privada (`authGuard`) |
| `/kits` | Configurador de kits de jardinería | Privada (`authGuard`) |
| `/perfil` | Mi perfil, pedidos y kits guardados | Privada (`authGuard`) |
| `/inventario-vivero` | Gestión de vivero / estado de salud | `VENDEDOR_VIVERO` / `ADMIN` |
| `**` | Redirección a inicio | — |

## Funcionalidades destacadas

- Autenticación JWT con interceptor y guards.
- Catálogo paginado con filtros y búsqueda en backend.
- Creación de pedidos con exención de responsabilidad.
- Panel de vendedor para actualizar `EstadoDeSalud`.
- Configurador de kits con resumen en vivo, descuento del 15% y persistencia real.

## Backend

Consulta el [README principal](../README.md) para ejecutar el backend con H2 o con el perfil MariaDB en Docker.