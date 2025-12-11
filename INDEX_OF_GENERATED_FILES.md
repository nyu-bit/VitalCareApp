# 📑 ÍNDICE DE ARCHIVOS GENERADOS

## 🗂️ Estructura Completa

### 📁 Archivos en Raíz del Proyecto
```
VitalCareApp1/
├── GENERATED_APIS_FINAL_SUMMARY.md          ← Resumen completo (LEER PRIMERO)
├── QUICK_START_INTEGRATION.md               ← Guía rápida de integración
├── COMPLETE_INTEGRATION_GUIDE.md            ← Guía técnica de arquitectura
├── RETROFIT_APIS_SUMMARY.md                 ← Resumen de APIs
└── (otros archivos del proyecto)
```

### 📁 `app/src/main/java/cl/duoc/app/data/api/`
**INTERFACES RETROFIT Y DTOs**

| Archivo | Contenido |
|---------|----------|
| **VitalesApi.kt** | Interface para microservicio de Signos Vitales + DTO |
| **UbicacionApi.kt** | Interface para microservicio de Ubicación + DTO |
| **AlertasApi.kt** | Interface para microservicio de Alertas + DTO |
| **API_USAGE_GUIDE.kt** | Ejemplos básicos de uso |
| **ExamplesAndPatterns.kt** | Patrones avanzados y mejores prácticas |
| **README.md** | Documentación de la carpeta |

### 📁 `app/src/main/java/cl/duoc/app/data/`
**CONFIGURACIÓN DE RETROFIT**

| Archivo | Descripción |
|---------|-------------|
| **RetrofitInstance.kt** | Singleton con buildClient() y métodos getXxxApi() |

### 📁 `app/src/main/java/cl/duoc/app/data/repository/`
**REPOSITORIES Y VIEWMODELS**

| Archivo | Contenido |
|---------|----------|
| **ApiRepositories.kt** | 4 repositories: Vitales, Ubicación, Alertas, PacienteData |
| **ViewModels.kt** | 4 ViewModels: Vitales, Ubicación, Alertas, PacienteData |

### 📁 `app/src/main/java/cl/duoc/app/ui/screens/examples/`
**EJEMPLOS DE COMPOSABLES**

| Archivo | Contenido |
|---------|----------|
| **ApiExamplesComposables.kt** | 8 composables listos para usar |

---

## 🎯 GUÍA RÁPIDA - POR NECESIDAD

### 💡 Necesito entender la arquitectura
```
1. Lee: COMPLETE_INTEGRATION_GUIDE.md
2. Diagrama: Ve la sección "Flujo de Datos Completo"
3. Ejemplo: Revisa "Flujo de Ejecución - Ejemplo Práctico"
```

### 🚀 Necesito integrar rápidamente
```
1. Lee: QUICK_START_INTEGRATION.md
2. Sigue los pasos del 1 al 9
3. Copia un Composable de ApiExamplesComposables.kt
```

### 📚 Necesito ver ejemplos de código
```
1. data/api/API_USAGE_GUIDE.kt - Ejemplos básicos
2. data/api/ExamplesAndPatterns.kt - Patrones avanzados
3. ui/screens/examples/ApiExamplesComposables.kt - Composables completos
```

### 🔧 Necesito información sobre una API específica
```
VitalesApi:
  - Interfaz: data/api/VitalesApi.kt
  - Repository: data/repository/ApiRepositories.kt (VitalesRepository)
  - ViewModel: data/repository/ViewModels.kt (VitalesViewModel)
  - Ejemplo UI: ui/screens/examples/ApiExamplesComposables.kt (VitalesScreenExample)

UbicacionApi:
  - Interfaz: data/api/UbicacionApi.kt
  - Repository: data/repository/ApiRepositories.kt (UbicacionRepository)
  - ViewModel: data/repository/ViewModels.kt (UbicacionViewModel)
  - Ejemplo UI: ui/screens/examples/ApiExamplesComposables.kt (UbicacionScreenExample)

AlertasApi:
  - Interfaz: data/api/AlertasApi.kt
  - Repository: data/repository/ApiRepositories.kt (AlertasRepository)
  - ViewModel: data/repository/ViewModels.kt (AlertasViewModel)
  - Ejemplo UI: ui/screens/examples/ApiExamplesComposables.kt (AlertasScreenExample)
```

### 🎨 Necesito un Composable listo para copiar
```
Ir a: ui/screens/examples/ApiExamplesComposables.kt

Opciones:
- VitalesScreenExample() - Pantalla completa para signos vitales
- UbicacionScreenExample() - Pantalla completa para ubicaciones
- AlertasScreenExample() - Pantalla completa para alertas
- PacienteDetailScreenExample() - Pantalla consolidada con 3 tabs
- CreateVitalSignFormExample() - Formulario para crear registros

Copiar la función directamente a tu proyecto
```

### 📝 Necesito usar un Repository
```
Importar:
import cl.duoc.app.data.repository.VitalesRepository
import cl.duoc.app.data.repository.UbicacionRepository
import cl.duoc.app.data.repository.AlertasRepository

Usar en ViewModel:
private val vitalesRepository = VitalesRepository()

O en composable (menos recomendado):
val repository = VitalesRepository()
val result = repository.getVitalesByPaciente("pacienteId")
```

### 🎬 Necesito un ViewModel
```
Importar:
import cl.duoc.app.data.repository.VitalesViewModel

Usar en Composable:
val viewModel = VitalesViewModel()
val uiState by viewModel.uiState.collectAsState()

O con viewModel():
val viewModel: VitalesViewModel = viewModel()
```

### 🐛 Necesito depurar
```
1. Habilitar logging: build.gradle.kts (HttpLoggingInterceptor)
2. Verificar URLs: RetrofitInstance.kt
3. Probar en Postman primero
4. Ver ResponseBody en logcat
5. Verificar JSON coincida con DTO
```

---

## 📋 ÍNDICE DE DTOs

### SignosVitalesDto
```
Archivo: data/api/VitalesApi.kt
Campos:
  - id: String? = null
  - pacienteId: String
  - frecuenciaCardiaca: Int?
  - presionArterialSistolica: Int?
  - presionArterialDiastolica: Int?
  - saturacionOxigeno: Int?
  - temperatura: Double?
  - notas: String?
  - timestamp: Long
```

### UbicacionDto
```
Archivo: data/api/UbicacionApi.kt
Campos:
  - id: String? = null
  - pacienteId: String
  - latitud: Double
  - longitud: Double
  - direccion: String?
  - ciudad: String?
  - pais: String?
  - precision: Float?
  - timestamp: Long
```

### AlertaDto
```
Archivo: data/api/AlertasApi.kt
Campos:
  - id: String? = null
  - pacienteId: String
  - titulo: String
  - mensaje: String
  - severidad: String
  - tipo: String
  - leida: Boolean
  - timestamp: Long
  - idRelacionado: String?
```

---

## 🔗 ÍNDICE DE INTERFACES

### VitalesApi
```
GET /vitales → List<SignosVitalesDto>
GET /vitales/paciente/{id} → List<SignosVitalesDto>
POST /vitales → SignosVitalesDto
DELETE /vitales/{id} → Void?
```

### UbicacionApi
```
GET /ubicacion → List<UbicacionDto>
GET /ubicacion/paciente/{id} → List<UbicacionDto>
POST /ubicacion → UbicacionDto
```

### AlertasApi
```
GET /alertas → List<AlertaDto>
GET /alertas/paciente/{id} → List<AlertaDto>
POST /alertas → AlertaDto
PUT /alertas/{id} → AlertaDto
DELETE /alertas/{id} → Void?
```

---

## 📊 ÍNDICE DE REPOSITORIES

### VitalesRepository
```
- getAllVitales() : Result<List<SignosVitalesDto>>
- getVitalesByPaciente(id) : Result<List<SignosVitalesDto>>
- createVitales(signos) : Result<SignosVitalesDto>
- deleteVitales(id) : Result<Unit>
```

### UbicacionRepository
```
- getAllUbicaciones() : Result<List<UbicacionDto>>
- getUbicacionesByPaciente(id) : Result<List<UbicacionDto>>
- createUbicacion(ubicacion) : Result<UbicacionDto>
```

### AlertasRepository
```
- getAllAlertas() : Result<List<AlertaDto>>
- getAlertasByPaciente(id) : Result<List<AlertaDto>>
- createAlerta(alerta) : Result<AlertaDto>
- updateAlerta(id, alerta) : Result<AlertaDto>
- deleteAlerta(id) : Result<Unit>
```

### PacienteDataRepository
```
- getPacienteCompleteData(id) : Result<PacienteCompleteData>
  (Obtiene datos de los 3 APIs en paralelo)
```

---

## 🎬 ÍNDICE DE VIEWMODELS

### VitalesViewModel
```
UI States: VitalesUiState (Loading | Success | Error)
Métodos:
  - loadAllVitales()
  - loadVitalesByPaciente(id)
  - saveVitales(signos)
  - deleteVitales(id)
```

### UbicacionViewModel
```
UI States: UbicacionUiState (Loading | Success | Error)
Métodos:
  - loadAllUbicaciones()
  - loadUbicacionesByPaciente(id)
  - saveUbicacion(ubicacion)
```

### AlertasViewModel
```
UI States: AlertasUiState (Loading | Success | Error)
Métodos:
  - loadAllAlertas()
  - loadAlertasByPaciente(id)
  - saveAlerta(alerta)
  - updateAlerta(id, alerta)
  - deleteAlerta(id)
```

### PacienteDataViewModel
```
UI States: PacienteDataUiState (Loading | Success | Error)
Métodos:
  - loadPacienteData(id)
```

---

## 🎨 ÍNDICE DE COMPOSABLES

| Composable | Ubicación | Descripción |
|-----------|-----------|-------------|
| **VitalesScreenExample** | ApiExamplesComposables.kt | Pantalla completa para vitales |
| **VitalSignCard** | ApiExamplesComposables.kt | Card para un vital individual |
| **UbicacionScreenExample** | ApiExamplesComposables.kt | Pantalla completa para ubicaciones |
| **UbicacionCard** | ApiExamplesComposables.kt | Card para una ubicación |
| **AlertasScreenExample** | ApiExamplesComposables.kt | Pantalla completa para alertas |
| **AlertaCard** | ApiExamplesComposables.kt | Card para una alerta |
| **PacienteDetailScreenExample** | ApiExamplesComposables.kt | Pantalla con 3 tabs consolidados |
| **CreateVitalSignFormExample** | ApiExamplesComposables.kt | Formulario para crear vitales |

---

## 📚 ORDEN RECOMENDADO DE LECTURA

1. **GENERATED_APIS_FINAL_SUMMARY.md** (Este proyecto en 5 minutos)
2. **QUICK_START_INTEGRATION.md** (Cómo integrar)
3. **data/api/README.md** (Qué hay en cada carpeta)
4. **COMPLETE_INTEGRATION_GUIDE.md** (Arquitectura detallada)
5. **Código fuente** (Explorar los archivos .kt)

---

## 🔍 BÚSQUEDA RÁPIDA

¿Necesitas...?

| Necesito | Buscar en |
|----------|-----------|
| Interfaz de API | `data/api/*.kt` |
| DTO | Dentro de cada `*Api.kt` |
| Lógica de negocio | `data/repository/ApiRepositories.kt` |
| State Management | `data/repository/ViewModels.kt` |
| Componente UI | `ui/screens/examples/ApiExamplesComposables.kt` |
| Configuración de red | `data/RetrofitInstance.kt` |
| Documentación general | `data/api/README.md` |
| Ejemplos de código | `data/api/API_USAGE_GUIDE.kt` |
| Patrones avanzados | `data/api/ExamplesAndPatterns.kt` |
| Guía de integración | `QUICK_START_INTEGRATION.md` |

---

## ✨ CARACTERÍSTICAS POR ARCHIVO

| Archivo | Características |
|---------|-----------------|
| VitalesApi.kt | 4 endpoints, DTO, suspend functions |
| UbicacionApi.kt | 3 endpoints, DTO, suspend functions |
| AlertasApi.kt | 5 endpoints, DTO, suspend functions |
| RetrofitInstance.kt | Singleton, buildClient(), lazy init |
| ApiRepositories.kt | 4 repositories, error handling, Result<T> |
| ViewModels.kt | 4 viewmodels, StateFlow, Sealed classes |
| ApiExamplesComposables.kt | 8 composables, ejemplos completos |

---

## 🎓 PRÓXIMAS ACCIONES

1. **Integración:** Seguir QUICK_START_INTEGRATION.md
2. **Pruebas:** Probar un endpoint con Postman
3. **UI:** Copiar un Composable al proyecto
4. **Datos:** Crear datos de prueba
5. **Caché:** (Futuro) Agregar Room Database
6. **Auth:** (Futuro) Agregar interceptor de token

---

## 📞 CONTACTO RÁPIDO

Si necesitas:
- **Referencia rápida:** QUICK_START_INTEGRATION.md
- **Documentación técnica:** COMPLETE_INTEGRATION_GUIDE.md
- **Ejemplos de código:** ExamplesAndPatterns.kt
- **Composables listos:** ApiExamplesComposables.kt
- **Documentación general:** README.md en cada carpeta

---

**¡Todo está documentado y listo para usar! 🚀**

Última actualización: Diciembre 2025

