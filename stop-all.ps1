[CmdletBinding()]
param(
    [switch]$KeepDatabase
)

$ErrorActionPreference = 'Continue'

$DbContainer = 'mariadb-jardineria'

function Stop-Port {
    param([int]$Port)
    $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    foreach ($connection in $connections) {
        Stop-Process -Id $connection.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "Detenido proceso en puerto $Port (PID $($connection.OwningProcess))"
    }
}

Stop-Port -Port 8080
Stop-Port -Port 4200

if (-not $KeepDatabase) {
    Write-Host "Deteniendo contenedor $DbContainer..."
    docker stop $DbContainer 2>$null | Out-Null
} else {
    Write-Host 'MariaDB se mantiene en ejecución.'
}

Write-Host 'Servicios detenidos.' -ForegroundColor Yellow