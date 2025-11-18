# ✅ CORRECCIÓN - Unresolved Reference 'systolicPressure'

## 📌 PROBLEMA

```
Unresolved reference 'systolicPressure'
File: AnomalyDetectionService.kt:36:20
```

**Causa:** Los nombres de las propiedades eran incorrectos. El modelo `VitalSigns` usa:
- `bloodPressureSystolic` (no `systolicPressure`)
- `bloodPressureDiastolic` (no `diastolicPressure`)

---

## ✅ SOLUCIÓN APLICADA

**Cambio en AnomalyDetectionService.kt línea 36:**

```kotlin
// ❌ ANTES:
vitalSigns.systolicPressure?.let { systolic ->
    vitalSigns.diastolicPressure?.let { diastolic ->

// ✅ DESPUÉS:
vitalSigns.bloodPressureSystolic?.let { systolic ->
    vitalSigns.bloodPressureDiastolic?.let { diastolic ->
```

---

## 📁 ARCHIVO MODIFICADO

- ✅ `app/src/main/java/cl/duoc/app/data/anomaly/AnomalyDetectionService.kt`
  - Línea 36: `systolicPressure` → `bloodPressureSystolic`
  - Línea 37: `diastolicPressure` → `bloodPressureDiastolic`

---

## 🚀 PRÓXIMO PASO

Compila nuevamente:
```powershell
.\gradlew clean build
```

---

**Status:** ✅ CORREGIDO
**Siguiente:** Compilar y buscar otros errores si los hay


