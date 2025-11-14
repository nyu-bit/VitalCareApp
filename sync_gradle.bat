@echo off
REM Script para sincronizar Gradle automáticamente
REM Este script ejecuta la sincronización de dependencias

echo.
echo ====================================================
echo Sincronizando Gradle - VitalCareApp
echo ====================================================
echo.

cd /d "C:\Users\esteb\AndroidStudioProjects\VitalCareApp"

echo Ejecutando: gradlew.bat clean build
echo Esto puede tomar algunos minutos...
echo.

call .\gradlew.bat clean build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================================
    echo SINCRONIZACION COMPLETADA EXITOSAMENTE
    echo ====================================================
    echo.
    echo Los siguientes cambios han sido aplicados:
    echo - Dependencias de Google Maps descargadas
    echo - Todas las referencias resueltas
    echo - Proyecto listo para compilar
    echo.
    pause
) else (
    echo.
    echo ====================================================
    echo ERROR DURANTE LA SINCRONIZACION
    echo ====================================================
    echo.
    echo Por favor intenta:
    echo 1. Abre Android Studio
    echo 2. Presiona Ctrl+Alt+Y para sincronizar
    echo 3. O ejecuta: .\gradlew.bat --refresh-dependencies
    echo.
    pause
)

