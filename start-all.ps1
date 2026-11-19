[CmdletBinding()]
param(
    [string]$FrontendDir
)

$ErrorActionPreference = 'Stop'

$Root = $PSScriptRoot
$LogDir = Join-Path $Root '.start-all-logs'
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

$BackendPort = 8080
$FrontendPort = 4200
$BackendUrl = 'http://localhost:8080/api/kits/articulos'
$FrontendUrl = 'http://localhost:4200'
$DbContainer = 'mariadb-jardineria'
$DbVolume = 'mariadb-jardineria-data'
$DbName = 'jardineria_db'
$DbUser = 'root'
$DbPassword = 'root'

$startedBackend = $false
$startedFrontend = $false
$backendProcess = $null
$frontendProcess = $null

function Write-Step {
    param([string]$Message)
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Test-Http {
    param([string]$Url, [int]$TimeoutSeconds)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
            if ($response.StatusCode -eq 200) {
                return $true
            }
        } catch {
        }
        Start-Sleep -Seconds 2
    }
    return $false
}

function Stop-Port {
    param([int]$Port)
    $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    foreach ($connection in $connections) {
        Stop-Process -Id $connection.OwningProcess -Force -ErrorAction SilentlyContinue
    }
}

function Resolve-FrontendDir {
    param([string]$Override)
    $candidates = @()
    if ($Override) {
        $candidates += $Override
    } elseif ($env:FRONTEND_DIR) {
        $candidates += $env:FRONTEND_DIR
    }
    $candidates += @(
        (Join-Path $Root 'jardineria-front'),
        (Join-Path $Root 'frontend'),
        (Join-Path $Root 'front')
    )

    foreach ($candidate in $candidates) {
        if ((Test-Path (Join-Path $candidate 'package.json')) -and (Test-Path (Join-Path $candidate 'angular.json'))) {
            $packageJson = Get-Content -Raw (Join-Path $candidate 'package.json')
            if ($packageJson -match '@angular/core') {
                return (Resolve-Path $candidate).Path
            }
        }
    }

    $found = Get-ChildItem -Path $Root -Recurse -Depth 3 -Filter package.json -File -ErrorAction SilentlyContinue |
        Where-Object { (Get-Content -Raw $_.FullName) -match '@angular/core' } |
        Select-Object -First 1

    if ($found) {
        return $found.DirectoryName
    }

    throw "No se encontró el frontend Angular. Ejecuta: `$env:FRONTEND_DIR = 'C:\ruta\al\frontend'; .\start-all.ps1"
}

function Get-DbCount {
    param([string]$Table)
    $query = "SELECT COUNT(*) FROM $Table;"
    $previous = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    $output = docker exec $DbContainer mariadb "-u$DbUser" "-p$DbPassword" -N -B $DbName -e $query 2>&1
    $exitCode = $LASTEXITCODE
    $ErrorActionPreference = $previous
    if ($exitCode -ne 0) {
        return '-1'
    }
    return (($output | Select-Object -Last 1).ToString().Trim())
}

Write-Step 'Resolviendo ruta del frontend'
$FrontendDir = Resolve-FrontendDir -Override $FrontendDir
Write-Host "Frontend: $FrontendDir"

Write-Step 'Verificando Docker'
docker info 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker no está corriendo. Abre Docker Desktop y vuelve a ejecutar start-all.ps1.'
}

Write-Step "Preparando MariaDB ($DbContainer)"
$containerExists = docker ps -a --filter "name=^/$DbContainer$" --format '{{.Names}}'
if ($containerExists) {
    docker start $DbContainer | Out-Null
} else {
    docker run -d --name $DbContainer `
        --restart unless-stopped `
        -e "MARIADB_ROOT_PASSWORD=$DbPassword" `
        -e "MARIADB_DATABASE=$DbName" `
        -p "3307:3306" `
        -v "${DbVolume}:/var/lib/mysql" `
        mariadb:11 | Out-Null
}

$dbReady = $false
for ($attempt = 0; $attempt -lt 60; $attempt++) {
    $previous = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    docker exec $DbContainer mariadb "-u$DbUser" "-p$DbPassword" -e 'SELECT 1' 2>&1 | Out-Null
    $exitCode = $LASTEXITCODE
    $ErrorActionPreference = $previous
    if ($exitCode -eq 0) {
        $dbReady = $true
        break
    }
    Start-Sleep -Seconds 2
}
if (-not $dbReady) {
    throw 'MariaDB no aceptó conexiones a tiempo.'
}

Write-Step 'Preparando backend con perfil mariadb'
if (Test-Http -Url $BackendUrl -TimeoutSeconds 2) {
    Write-Host 'El backend ya estaba corriendo; se reutilizará.'
} else {
    $env:SPRING_PROFILES_ACTIVE = 'mariadb'
    $backendLog = Join-Path $LogDir 'backend.log'
    $backendProcess = Start-Process -FilePath (Join-Path $Root 'gradlew.bat') `
        -ArgumentList 'bootRun', '--console=plain' `
        -WorkingDirectory $Root `
        -RedirectStandardOutput $backendLog `
        -RedirectStandardError "$backendLog.err" `
        -PassThru `
        -WindowStyle Hidden
    $startedBackend = $true
    if (-not (Test-Http -Url $BackendUrl -TimeoutSeconds 180)) {
        throw "El backend no respondió a tiempo. Revisa $backendLog"
    }
}

Write-Step 'Verificando carga de datos iniciales'
$especiesCount = Get-DbCount -Table 'especies'
if ($especiesCount -eq '0') {
    Write-Host 'La base está vacía. Cargando src/main/resources/data.sql...'
    $dataSqlPath = Join-Path $Root 'src\main\resources\data.sql'
    $command = "docker exec -i $DbContainer mariadb -u$DbUser -p$DbPassword $DbName < `"$dataSqlPath`""
    cmd /c $command
} elseif ($especiesCount -eq '-1') {
    Write-Warning 'No se pudo consultar la tabla especies; se omite la carga automática.'
} else {
    Write-Host "La base ya tiene datos ($especiesCount especies); se omite data.sql."
}

Write-Step 'Preparando frontend Angular'
if (Test-Http -Url $FrontendUrl -TimeoutSeconds 2) {
    Write-Host 'El frontend ya estaba corriendo; se reutilizará.'
} else {
    if (-not (Get-Command npm.cmd -ErrorAction SilentlyContinue)) {
        throw 'npm no está instalado o no está en PATH.'
    }

    if (-not (Test-Path (Join-Path $FrontendDir 'node_modules'))) {
        Write-Host 'Instalando dependencias del frontend...'
        Push-Location $FrontendDir
        try {
            & npm.cmd install
            if ($LASTEXITCODE -ne 0) {
                throw 'npm install falló.'
            }
        } finally {
            Pop-Location
        }
    }

    $frontendLog = Join-Path $LogDir 'frontend.log'
    $frontendProcess = Start-Process -FilePath 'npm.cmd' `
        -ArgumentList 'start' `
        -WorkingDirectory $FrontendDir `
        -RedirectStandardOutput $frontendLog `
        -RedirectStandardError "$frontendLog.err" `
        -PassThru `
        -WindowStyle Hidden
    $startedFrontend = $true
    if (-not (Test-Http -Url $FrontendUrl -TimeoutSeconds 180)) {
        throw "El frontend no respondió a tiempo. Revisa $frontendLog"
    }
}

Write-Host ''
Write-Host 'Ecosistema listo:' -ForegroundColor Green
Write-Host "  Frontend:  $FrontendUrl"
Write-Host "  Backend:   http://localhost:8080"
Write-Host "  Swagger:   http://localhost:8080/swagger-ui.html"
Write-Host "  MariaDB:   localhost:3307 ($DbContainer)"
Write-Host ''
Write-Host 'Presiona Ctrl+C o ejecuta .\stop-all.ps1 para detener todo.'

try {
    while ($true) {
        Start-Sleep -Seconds 5
    }
} finally {
    if ($startedFrontend) {
        Stop-Port -Port $FrontendPort
    }
    if ($startedBackend) {
        Stop-Port -Port $BackendPort
    }
}