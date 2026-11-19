#!/usr/bin/env bash
set -euo pipefail

DB_CONTAINER="mariadb-jardineria"
KEEP_DB=false
if [ "${1:-}" = "--keep-db" ]; then
  KEEP_DB=true
fi

stop_port() {
  local port="$1"
  if command -v lsof >/dev/null 2>&1; then
    local pids
    pids="$(lsof -ti "tcp:$port" 2>/dev/null || true)"
    if [ -n "$pids" ]; then
      echo "$pids" | xargs kill -9 2>/dev/null || true
      echo "Detenido proceso en puerto $port"
    fi
  elif command -v fuser >/dev/null 2>&1; then
    fuser -k "$port/tcp" 2>/dev/null || true
  fi
}

stop_port 8080
stop_port 4200

if [ "$KEEP_DB" = false ]; then
  echo "Deteniendo contenedor $DB_CONTAINER..."
  docker stop "$DB_CONTAINER" >/dev/null 2>&1 || true
else
  echo "MariaDB se mantiene en ejecución."
fi

echo "Servicios detenidos."