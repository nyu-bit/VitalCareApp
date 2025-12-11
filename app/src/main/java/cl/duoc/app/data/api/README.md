# API - Interfaces Retrofit y DTOs

Esta carpeta contiene todas las interfaces Retrofit y DTOs para comunicarse con los microservicios de VitalCare.

## Estructura

### Interfaces Retrofit

#### 1. **VitalesApi.kt**
Interfaz para el servicio de Signos Vitales (Base URL: `http://10.0.2.2:8081/`)

**Métodos disponibles:**
- `getAllVitales()` - GET /vitales → Obtiene todos los signos vitales
- `getVitalesByPaciente(id)` - GET /vitales/paciente/{id} → Obtiene signos vitales de un paciente
- `createVitales(signos)` - POST /vitales → Crea nuevo registro
- `deleteVitales(id)` - DELETE /vitales/{id} → Elimina un registro

**DTO:** `SignosVitalesDto`

#### 2. **UbicacionApi.kt**
Interfaz para el servicio de Ubicación (Base URL: `http://10.0.2.2:8082/`)

**Métodos disponibles:**
- `getAllUbicaciones()` - GET /ubicacion → Obtiene todas las ubicaciones
- `getUbicacionesByPaciente(id)` - GET /ubicacion/paciente/{id} → Obtiene ubicaciones de un paciente
- `createUbicacion(ubicacion)` - POST /ubicacion → Crea nueva ubicación

**DTO:** `UbicacionDto`

#### 3. **AlertasApi.kt**
Interfaz para el servicio de Alertas (Base URL: `http://10.0.2.2:8083/`)

**Métodos disponibles:**
- `getAllAlertas()` - GET /alertas → Obtiene todas las alertas
- `getAlertasByPaciente(id)` - GET /alertas/paciente/{id} → Obtiene alertas de un paciente
- `createAlerta(alerta)` - POST /alertas → Crea nueva alerta
- `updateAlerta(id, alerta)` - PUT /alertas/{id} → Actualiza una alerta
- `deleteAlerta(id)` - DELETE /alertas/{id} → Elimina una alerta

**DTO:** `AlertaDto`

### Datos Transfer Objects (DTOs)

Todos los DTOs están definidos en sus respectivos archivos:

- **SignosVitalesDto** - Representa un registro de signos vitales
- **UbicacionDto** - Representa una ubicación geográfica
- **AlertaDto** - Representa una alerta del sistema

## Características

✅ Todas las funciones son **suspend functions** para uso con corrutinas  
✅ Soporte completo de **Retrofit** con **GsonConverterFactory**  
✅ DTOs con valores por defecto apropiados  
✅ Documentación completa en JavaDoc  

## Uso

Para obtener una interfaz y realizar una llamada:

```kotlin
val vitalesApi = RetrofitInstance.getVitalesApi()
val signos = vitalesApi.getAllVitales()
```

Para más ejemplos, consultar **API_USAGE_GUIDE.kt**

## Configuración

La configuración de Retrofit se realiza en `RetrofitInstance.kt`, que es un Singleton que proporciona acceso a los tres APIs con sus respectivas URLs base.

