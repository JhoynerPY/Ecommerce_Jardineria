#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT/.start-all-logs"
mkdir -p "$LOG_DIR"

FRONTEND_DIR="${1:-${FRONTEND_DIR:-}}"
BACKEND_PORT=8080
FRONTEND_PORT=4200
BACKEND_URL="http://localhost:8080/api/kits/articulos"
FRONTEND_URL="http://localhost:4200"
DB_CONTAINER="mariadb-jardineria"
DB_VOLUME="mariadb-jardineria-data"
DB_NAME="jardineria_db"
DB_USER="root"
DB_PASSWORD="root"

STARTED_BACKEND=false
STARTED_FRONTEND=false
BACKEND_PID=""
FRONTEND_PID=""

die() {
  echo "ERROR: $*" >&2
  exit 1
}

wait_http() {
  local url="$1"
  local timeout="$2"
  local deadline=$((SECONDS + timeout))
  while [ "$SECONDS" -lt "$deadline" ]; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
  done
  return 1
}

resolve_frontend() {
  local candidate
  for candidate in "$FRONTEND_DIR" "$ROOT/jardineria-front" "$ROOT/frontend" "$ROOT/front"; do
    [ -n "$candidate" ] || continue
    if [ -f "$candidate/package.json" ] && [ -f "$candidate/angular.json" ] &&
       grep -q '@angular/core' "$candidate/package.json"; then
      (cd "$candidate" && pwd)
      return 0
    fi
  done

  local found
  found="$(find "$ROOT" -maxdepth 3 -name package.json -type f \
    -exec grep -l '@angular/core' {} \; 2>/dev/null | head -n 1)"
  if [ -n "$found" ]; then
    dirname "$found"
    return 0
  fi

  die "No se encontró el frontend Angular. Ejecuta: FRONTEND_DIR=/ruta/al/frontend ./start-all.sh"
}

db_count() {
  local table="$1"
  docker exec "$DB_CONTAINER" mariadb "-u$DB_USER" "-p$DB_PASSWORD" \
    -N -B "$DB_NAME" -e "SELECT COUNT(*) FROM $table;" 2>/dev/null |
    tail -n 1 | tr -d '[:space:]' || echo "-1"
}

cleanup() {
  if [ "${STARTED_FRONTEND:-false}" = true ] && [ -n "${FRONTEND_PID:-}" ]; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
  if [ "${STARTED_BACKEND:-false}" = true ] && [ -n "${BACKEND_PID:-}" ]; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi
}
trap cleanup INT TERM EXIT

echo "==> Resolviendo ruta del frontend"
FRONTEND_DIR="$(resolve_frontend)"
echo "Frontend: $FRONTEND_DIR"

echo "==> Verificando Docker"
docker info >/dev/null 2>&1 || die "Docker no está corriendo. Abre Docker Desktop y vuelve a ejecutar start-all.sh."

echo "==> Preparando MariaDB ($DB_CONTAINER)"
if docker ps -a --filter "name=^/$DB_CONTAINER$" --format '{{.Names}}' | grep -q "$DB_CONTAINER"; then
  docker start "$DB_CONTAINER" >/dev/null
else
  docker run -d --name "$DB_CONTAINER" \
    --restart unless-stopped \
    -e "MARIADB_ROOT_PASSWORD=$DB_PASSWORD" \
    -e "MARIADB_DATABASE=$DB_NAME" \
    -p "3307:3306" \
    -v "${DB_VOLUME}:/var/lib/mysql" \
    mariadb:11 >/dev/null
fi

DB_READY=false
for _ in $(seq 1 60); do
  if docker exec "$DB_CONTAINER" mariadb "-u$DB_USER" "-p$DB_PASSWORD" -e 'SELECT 1' >/dev/null 2>&1; then
    DB_READY=true
    break
  fi
  sleep 2
done
[ "$DB_READY" = true ] || die "MariaDB no aceptó conexiones a tiempo."

echo "==> Preparando backend con perfil mariadb"
if wait_http "$BACKEND_URL" 2; then
  echo "El backend ya estaba corriendo; se reutilizará."
else
  export SPRING_PROFILES_ACTIVE=mariadb
  "$ROOT/gradlew" bootRun --console=plain >"$LOG_DIR/backend.log" 2>&1 &
  BACKEND_PID=$!
  STARTED_BACKEND=true
  wait_http "$BACKEND_URL" 180 || die "El backend no respondió a tiempo. Revisa $LOG_DIR/backend.log"
fi

echo "==> Verificando carga de datos iniciales"
ESPECIES_COUNT="$(db_count especies)"
if [ "$ESPECIES_COUNT" = "0" ]; then
  echo "La base está vacía. Cargando src/main/resources/data.sql..."
  docker exec -i "$DB_CONTAINER" mariadb "-u$DB_USER" "-p$DB_PASSWORD" "$DB_NAME" \
    < "$ROOT/src/main/resources/data.sql"
elif [ "$ESPECIES_COUNT" = "-1" ]; then
  echo "ADVERTENCIA: no se pudo consultar la tabla especies; se omite data.sql." >&2
else
  echo "La base ya tiene datos ($ESPECIES_COUNT especies); se omite data.sql."
fi

echo "==> Preparando frontend Angular"
if wait_http "$FRONTEND_URL" 2; then
  echo "El frontend ya estaba corriendo; se reutilizará."
else
  command -v npm >/dev/null 2>&1 || die "npm no está instalado o no está en PATH."
  if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo "Instalando dependencias del frontend..."
    (cd "$FRONTEND_DIR" && npm install)
  fi
  (cd "$FRONTEND_DIR" && npm start) >"$LOG_DIR/frontend.log" 2>&1 &
  FRONTEND_PID=$!
  STARTED_FRONTEND=true
  wait_http "$FRONTEND_URL" 180 || die "El frontend no respondió a tiempo. Revisa $LOG_DIR/frontend.log"
fi

echo
echo "Ecosistema listo:"
echo "  Frontend:  $FRONTEND_URL"
echo "  Backend:   http://localhost:8080"
echo "  Swagger:   http://localhost:8080/swagger-ui.html"
echo "  MariaDB:   localhost:3307 ($DB_CONTAINER)"
echo
echo "Presiona Ctrl+C o ejecuta ./stop-all.sh para detener todo."

while true; do
  sleep 5
done