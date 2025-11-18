#!/usr/bin/env pwsh

<#
.SYNOPSIS
Script para limpiar y reconstruir el proyecto VitalCareApp
.DESCRIPTION
Resuelve el error: IncompatibleClassChangeError de KSP/Kotlin
.AUTHOR
GitHub Copilot
#>

Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "  VitalCareApp - Limpieza y Reconstruccion" -ForegroundColor Cyan
Write-Host "================================================`n" -ForegroundColor Cyan

# Función para mostrar estado
function Show-Status {
    param(
        [int]$Step,
        [string]$Message,
        [string]$Status = "..."
    )
    Write-Host "[$Step/5] $Message" -ForegroundColor Yellow
    Write-Host "        $Status" -ForegroundColor Gray
}

# Función para mostrar éxito
function Show-Success {
    Write-Host "        ✅ Completado" -ForegroundColor Green
}

# Función para mostrar error
function Show-Error {
    param([string]$Message)
    Write-Host "        ❌ ERROR: $Message" -ForegroundColor Red
    exit 1
}

try {
    # Paso 1: Verificar que estamos en la carpeta correcta
    Show-Status 1 "Verificando carpeta del proyecto" "Buscando build.gradle.kts..."
    if (-not (Test-Path "app/build.gradle.kts")) {
        Show-Error "No se encontró app/build.gradle.kts. Asegúrate de ejecutar el script desde la raíz del proyecto."
    }
    Show-Success

    # Paso 2: Limpiar Gradle
    Show-Status 2 "Limpiando cachés de Gradle" "Ejecutando gradlew clean..."
    & ".\gradlew" clean 2>$null
    if ($LASTEXITCODE -ne 0) {
        Show-Error "gradlew clean falló"
    }
    Show-Success

    # Paso 3: Eliminar carpetas de construcción
    Show-Status 3 "Eliminando carpetas temporales" "Limpiando .gradle, app/build, build..."

    $foldersToDelete = @(".gradle", "app/build", "build")
    foreach ($folder in $foldersToDelete) {
        if (Test-Path $folder) {
            Remove-Item -Recurse -Force $folder -ErrorAction SilentlyContinue
            Write-Host "        Eliminada carpeta: $folder" -ForegroundColor Gray
        }
    }
    Show-Success

    # Paso 4: Actualizar dependencias
    Show-Status 4 "Actualizando dependencias" "Ejecutando gradlew --refresh-dependencies..."
    & ".\gradlew" --refresh-dependencies 2>$null
    if ($LASTEXITCODE -ne 0) {
        Show-Error "gradlew --refresh-dependencies falló"
    }
    Show-Success

    # Paso 5: Compilar
    Show-Status 5 "Compilando proyecto" "Ejecutando gradlew build..."
    & ".\gradlew" build
    if ($LASTEXITCODE -ne 0) {
        Show-Error "gradlew build falló"
    }
    Show-Success

    # Éxito
    Write-Host "`n================================================" -ForegroundColor Green
    Write-Host "  ✅ EXITO: Proyecto compilado correctamente" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Green
    Write-Host "`nAhora puedes ejecutar la aplicación en Android Studio:`n" -ForegroundColor Cyan
    Write-Host "  1. Abre Android Studio" -ForegroundColor White
    Write-Host "  2. Abre el proyecto (si no está abierto)" -ForegroundColor White
    Write-Host "  3. Run → Run 'app'" -ForegroundColor White
    Write-Host "`n" -ForegroundColor Cyan

} catch {
    Show-Error $_.Exception.Message
}

