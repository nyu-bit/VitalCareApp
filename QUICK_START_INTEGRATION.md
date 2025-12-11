# 🚀 GUÍA DE INTEGRACIÓN RÁPIDA

## ✅ Lo que ya está hecho

- [x] **Interfaces Retrofit creadas** (3 APIs con todos los métodos)
- [x] **DTOs generados** (SignosVitalesDto, UbicacionDto, AlertaDto)
- [x] **Suspend functions implementadas** (Todas las funciones soportan corrutinas)
- [x] **RetrofitInstance configurado** (Singleton con buildClient())
- [x] **Repositories creados** (3 específicos + 1 combinado)
- [x] **ViewModels implementados** (3 específicos + 1 combinado)
- [x] **Composables de ejemplo** (Listos para copiar y usar)
- [x] **Dependencias agregadas** (Retrofit, Gson, OkHttp)
- [x] **Documentación completa** (Guías y ejemplos)

---

## 🎯 Paso 1: Verificar Arquitectura

```
app/src/main/java/cl/duoc/app/
├── data/
│   ├── api/
│   │   ├── VitalesApi.kt ✅
│   │   ├── UbicacionApi.kt ✅
│   │   ├── AlertasApi.kt ✅
│   ├── repository/
│   │   ├── ApiRepositories.kt ✅
│   │   ├── ViewModels.kt ✅
│   └── RetrofitInstance.kt ✅
└── ui/screens/examples/
    └── ApiExamplesComposables.kt ✅
```

---

## 🔧 Paso 2: Integrar en tu Pantalla

### Opción A: Usando composable de ejemplo (Recomendado)

```kotlin
import cl.duoc.app.ui.screens.examples.VitalesScreenExample

@Composable
fun MiPantalla() {
    VitalesScreenExample(pacienteId = "paciente123")
}
```

### Opción B: Crear tu propio composable

```kotlin
@Composable
fun MiPantalla(pacienteId: String) {
    val viewModel = VitalesViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.loadVitalesByPaciente(pacienteId)
    }

    when (uiState) {
        is VitalesUiState.Loading -> { /* Loading UI */ }
        is VitalesUiState.Success -> { /* Success UI */ }
        is VitalesUiState.Error -> { /* Error UI */ }
    }
}
```

---

## 💡 Paso 3: Usar en ViewModel Existente

```kotlin
class MyViewModel : ViewModel() {
    private val vitalesRepository = VitalesRepository()

    fun cargarSignos(pacienteId: String) {
        viewModelScope.launch {
            try {
                val resultado = vitalesRepository.getVitalesByPaciente(pacienteId)
                resultado.fold(
                    onSuccess = { signos ->
                        // Procesar datos
                    },
                    onFailure = { error ->
                        // Manejar error
                    }
                )
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
```

---

## 📝 Paso 4: Crear Nuevos Registros

### Crear Signo Vital

```kotlin
val nuevoSigno = SignosVitalesDto(
    pacienteId = "paciente123",
    frecuenciaCardiaca = 72,
    presionArterialSistolica = 120,
    presionArterialDiastolica = 80,
    saturacionOxigeno = 98,
    temperatura = 37.5,
    notas = "Registro normal"
)

viewModel.saveVitales(nuevoSigno)
```

### Crear Ubicación

```kotlin
val nuevaUbicacion = UbicacionDto(
    pacienteId = "paciente123",
    latitud = -33.8688,
    longitud = -71.5203,
    direccion = "Calle Principal 123",
    ciudad = "Santiago",
    pais = "Chile"
)

viewModel.saveUbicacion(nuevaUbicacion)
```

### Crear Alerta

```kotlin
val nuevaAlerta = AlertaDto(
    pacienteId = "paciente123",
    titulo = "Presión Arterial Alta",
    mensaje = "La presión está fuera de rango",
    severidad = "Alto",
    tipo = "Signos Vitales"
)

viewModel.saveAlerta(nuevaAlerta)
```

---

## 🔍 Paso 5: Manejo de Errores

### En Repository

```kotlin
suspend fun getVitalesByPaciente(pacienteId: String): Result<List<SignosVitalesDto>> {
    return try {
        val signos = vitalesApi.getVitalesByPaciente(pacienteId)
        Result.success(signos)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### En ViewModel

```kotlin
fun loadVitales(pacienteId: String) {
    viewModelScope.launch {
        val result = repository.getVitalesByPaciente(pacienteId)
        _uiState.value = result.fold(
            onSuccess = { signos -> VitalesUiState.Success(signos) },
            onFailure = { error -> VitalesUiState.Error(error.message ?: "Error") }
        )
    }
}
```

### En UI

```kotlin
when (val state = uiState) {
    is VitalesUiState.Loading -> { /* Mostrar loader */ }
    is VitalesUiState.Success -> { /* Mostrar datos */ }
    is VitalesUiState.Error -> { 
        Text("Error: ${state.message}") 
    }
}
```

---

## 🧪 Paso 6: Testing (Opcional)

### Mock del Repository

```kotlin
class FakeVitalesRepository : VitalesRepository() {
    override suspend fun getVitalesByPaciente(pacienteId: String): Result<List<SignosVitalesDto>> {
        return Result.success(listOf(
            SignosVitalesDto(
                pacienteId = pacienteId,
                frecuenciaCardiaca = 72
            )
        ))
    }
}
```

### Test del ViewModel

```kotlin
@Test
fun testLoadVitales() = runTest {
    val viewModel = VitalesViewModel()
    viewModel.loadVitalesByPaciente("paciente123")
    
    val state = viewModel.uiState.value
    assert(state is VitalesUiState.Success)
}
```

---

## 📦 Paso 7: Configuración Adicional

### Agregar Logging (Debug)

```kotlin
// En RetrofitInstance.kt (opcional)
private fun buildClient(baseUrl: String): Retrofit {
    val httpClientBuilder = OkHttpClient.Builder()
    
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        httpClientBuilder.addInterceptor(loggingInterceptor)
    }
    
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .build()
}
```

### Agregar Autenticación (Futura)

```kotlin
// Interceptor de token (agregar después)
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

---

## 🎨 Paso 8: Personalizar UI

### Usar tu propio Card Design

```kotlin
@Composable
fun MiVitalSignCard(signo: SignosVitalesDto) {
    // Reemplazar con tu diseño
    Card {
        Column {
            Text("${signo.frecuenciaCardiaca} bpm")
            Text("${signo.temperatura}°C")
        }
    }
}
```

### Personalizar colores por severidad

```kotlin
val color = when (alerta.severidad) {
    "Crítico" -> Color.Red
    "Alto" -> Color.Orange
    "Medio" -> Color.Yellow
    else -> Color.Green
}
```

---

## 🚨 Paso 9: Depuración

### Ver todos los requests

```kotlin
// Habilitar logging en build.gradle.kts
debugImplementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

### Verificar respuestas JSON

```
GET http://10.0.2.2:8081/vitales
GET http://10.0.2.2:8082/ubicacion
GET http://10.0.2.2:8083/alertas
```

### Usar Postman para probar APIs

```
1. Crear collection para cada microservicio
2. Probar endpoints antes de integrar
3. Guardar ejemplos de response
```

---

## ✅ Checklist Final

- [ ] Copié los archivos API al proyecto
- [ ] Agregué las dependencias de Retrofit
- [ ] Creé al menos un Composable que use VitalesViewModel
- [ ] Probé cargar datos de un paciente
- [ ] Probé crear un nuevo registro
- [ ] Probé eliminar un registro
- [ ] Agregué manejo de errores en mi UI
- [ ] Personalicé el diseño de tarjetas
- [ ] Documenté mi implementación
- [ ] Hice pruebas unitarias (opcional)

---

## 🎓 Recursos Útiles

| Tema | Archivo/Ubicación |
|---|---|
| Ejemplos básicos | `data/api/API_USAGE_GUIDE.kt` |
| Patrones avanzados | `data/api/ExamplesAndPatterns.kt` |
| Composables listos | `ui/screens/examples/ApiExamplesComposables.kt` |
| Documentación general | `data/api/README.md` |
| Guía de integración | `COMPLETE_INTEGRATION_GUIDE.md` |
| Resumen final | `GENERATED_APIS_FINAL_SUMMARY.md` |

---

## 🔗 URLs de Referencia

```
Vitales:    http://10.0.2.2:8081/
Ubicación:  http://10.0.2.2:8082/
Alertas:    http://10.0.2.2:8083/
Weather:    https://api.openweathermap.org/
```

---

## 🆘 Solución de Problemas

### Problema: "No se resuelve VitalesApi"
**Solución:** Verificar que el paquete sea correcto en el import
```kotlin
import cl.duoc.app.data.api.VitalesApi
```

### Problema: "RetrofitInstance retorna null"
**Solución:** Es un Singleton, debe usarse como:
```kotlin
val api = RetrofitInstance.getVitalesApi()
```

### Problema: "Errores de conexión HTTP"
**Solución:** Verificar:
- Emulador tiene internet
- URLs base son correctas
- Servidores están activos
- Firewall permite conexiones

### Problema: "DTO no deserializa correctamente"
**Solución:** Verificar que los nombres de campos en JSON coincidan con los del DTO

---

## 📞 ¿Necesitas ayuda?

1. Consulta los archivos de documentación
2. Revisa los ejemplos en `ApiExamplesComposables.kt`
3. Mira `COMPLETE_INTEGRATION_GUIDE.md` para arquitectura
4. Prueba primero con Postman
5. Usa logs con `HttpLoggingInterceptor`

---

**¡Tu app está lista para conectarse a los microservicios! 🚀**

Todos los componentes están creados, documentados y listos para usar.
Solo necesitas copiar y adaptar al diseño específico de tu app.

