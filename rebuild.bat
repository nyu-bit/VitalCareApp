@echo off
REM Script para limpiar y reconstruir el proyecto VitalCareApp
REM Resuelve el error: IncompatibleClassChangeError

echo.
echo ================================================
echo VitalCareApp - Script de Limpieza y Reconstruccion
echo ================================================
echo.

cd /d %~dp0

echo [1/4] Limpiando cachés de Gradle...
call gradlew clean
if errorlevel 1 (
    echo ERROR en gradlew clean
    exit /b 1
)
echo [1/4] OK - Cachés limpios

echo.
echo [2/4] Eliminando carpeta .gradle...
if exist ".gradle" (
    rmdir /s /q .gradle
    echo [2/4] OK - Carpeta .gradle eliminada
) else (
    echo [2/4] OK - Carpeta .gradle no existía
)

echo.
echo [3/4] Sincronizando Gradle con nuevas versiones...
call gradlew --refresh-dependencies
if errorlevel 1 (
    echo ERROR en refresh-dependencies
    exit /b 1
)
echo [3/4] OK - Dependencias actualizadas

echo.
echo [4/4] Compilando proyecto...
call gradlew build
if errorlevel 1 (
    echo ERROR en compilacion
    exit /b 1
)
echo [4/4] OK - Compilacion exitosa

echo.
echo ================================================
echo EXITO: Proyecto compilado correctamente
echo ================================================
echo.
pause

