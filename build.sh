#!/bin/bash
# Script para compilar el proyecto VitalCareApp

cd "C:\Users\esteb\AndroidStudioProjects\VitalCareApp"

echo "====================================="
echo "Compilando VitalCareApp"
echo "====================================="
echo ""

# Limpiar build anterior
echo "Limpiando build anterior..."
./gradlew clean

# Compilar proyecto
echo ""
echo "Compilando proyecto..."
./gradlew build

echo ""
echo "====================================="
echo "Compilación completada"
echo "====================================="

