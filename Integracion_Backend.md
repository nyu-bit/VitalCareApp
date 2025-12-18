# 🔗 Verificación de Integración con Backend - VitalCare

## 1. ARQUITECTURA DE LA INTEGRACIÓN

```
┌─────────────────┐
│  VitalCare App  │ (Cliente - Kotlin Android)
│   (Tutor)       │
└────────┬────────┘
         │
         │ HTTP/REST
         │ (Retrofit)
         │
┌────────▼────────┐
│ Spring Boot API │ (Servidor)
│   Backend       │
└────────┬────────┘
         │
         │ JDBC/JPA
         │
┌────────▼────────┐
│   Base de Datos │ (MySQL/PostgreSQL)
│   (Pacientes)   │
└─────────────────┘
```

---

## 2. ENDPOINTS NECESARIOS DEL BACKEND

Tu backend en Spring Boot debe tener estos endpoints:

### 2.1 Autenticación

```
POST /api/auth/login
Content-Type: application/json

{
  "email": "tutor@example.com",
  "password": "password123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tutorId": "tutor_123",
  "name": "Juan Pérez"
}
```

### 2.2 Obtener Pacientes del Tutor

```
GET /api/tutors/{tutorId}/patients
Authorization: Bearer {token}

Response 200:
{
  "patients": [
    {
      "id": "patient_1",
      "name": "Abuela María",
      "age": 75,
      "condition": "Alzheimer",
      "lastVitals": {
        "heartRate": 72,
        "bloodPressure": "120/80",
        "temperature": 36.5,
        "timestamp": "2024-12-18T18:30:00Z"
      },
      "riskLevel": "MODERATE"
    },
    {
      "id": "patient_2",
      "name": "Tío Carlos",
      "age": 68,
      "condition": "Epilepsia",
      "lastVitals": {...},
      "riskLevel": "HIGH"
    }
  ]
}
```

### 2.3 Actualizar Vitales del Paciente

```
PUT /api/patients/{patientId}/vitals
Authorization: Bearer {token}
Content-Type: application/json

{
  "heartRate": 75,
  "bloodPressure": "119/79",
  "temperature": 36.4,
  "oxygenLevel": 98,
  "timestamp": "2024-12-18T18:35:00Z"
}

Response 200:
{
  "success": true,
  "message": "Vitals updated successfully"
}
```

### 2.4 Obtener Alertas

```
GET /api/patients/{patientId}/alerts
Authorization: Bearer {token}

Response 200:
{
  "alerts": [
    {
      "id": "alert_1",
      "type": "HIGH_HEART_RATE",
      "severity": "WARNING",
      "message": "Heart rate above 100",
      "timestamp": "2024-12-18T18:20:00Z",
      "read": false
    }
  ]
}
```

### 2.5 Crear Alerta (Desde Smartwatch)

```
POST /api/patients/{patientId}/alerts
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "FALL_DETECTED",
  "severity": "CRITICAL",
  "message": "Caída detectada - ubicación: Sala",
  "location": {
    "latitude": -33.8688,
    "longitude": -51.2093
  }
}

Response 201:
{
  "id": "alert_new_1",
  "created": true
}
```

---

## 3. VERIFICAR ENDPOINTS DESDE POSTMAN/CURL

### 3.1 Probar login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tutor@example.com",
    "password": "password123"
  }'

# Response esperado:
# {
#   "token": "eyJhbGc...",
#   "tutorId": "tutor_123"
# }
```

### 3.2 Probar obtener pacientes

```bash
# Primero obtén el token del login
TOKEN="eyJhbGc..."
TUTOR_ID="tutor_123"

curl -X GET http://localhost:8080/api/tutors/$TUTOR_ID/patients \
  -H "Authorization: Bearer $TOKEN"

# Response esperado: lista de pacientes
```

### 3.3 Probar actualizar vitales

```bash
TOKEN="eyJhbGc..."
PATIENT_ID="patient_1"

curl -X PUT http://localhost:8080/api/patients/$PATIENT_ID/vitals \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "heartRate": 75,
    "bloodPressure": "119/79",
    "temperature": 36.4
  }'
```

---

## 4. CONFIGURACIÓN EN ANDROID (Retrofit)

### 4.1 Dependencia en build.gradle.kts

```kotlin
dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 4.2 Modelo de respuesta (Kotlin)

```kotlin
data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val condition: String,
    val lastVitals: Vitals,
    val riskLevel: String
)

data class Vitals(
    val heartRate: Int,
    val bloodPressure: String,
    val temperature: Double,
    val oxygenLevel: Int,
    val timestamp: String
)

data class Alert(
    val id: String,
    val type: String,
    val severity: String,
    val message: String,
    val timestamp: String,
    val read: Boolean
)
```

### 4.3 Interfaz del servicio (Retrofit)

```kotlin
interface PatientService {
    
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    
    @GET("tutors/{tutorId}/patients")
    suspend fun getPatients(
        @Path("tutorId") tutorId: String
    ): PatientsResponse
    
    @PUT("patients/{patientId}/vitals")
    suspend fun updateVitals(
        @Path("patientId") patientId: String,
        @Body vitals: Vitals
    ): ApiResponse
    
    @GET("patients/{patientId}/alerts")
    suspend fun getAlerts(
        @Path("patientId") patientId: String
    ): AlertsResponse
    
    @POST("patients/{patientId}/alerts")
    suspend fun createAlert(
        @Path("patientId") patientId: String,
        @Body alert: Alert
    ): ApiResponse
}
```

### 4.4 Repository (Manejo de API)

```kotlin
class PatientRepository(
    private val apiService: PatientService
) {
    
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()
    
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            _authToken.value = response.token
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPatients(tutorId: String): Flow<List<Patient>> = flow {
        try {
            val response = apiService.getPatients(tutorId)
            emit(response.patients)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }
    
    suspend fun updatePatientVitals(patientId: String, vitals: Vitals) {
        apiService.updateVitals(patientId, vitals)
    }
}
```

### 4.5 ViewModel

```kotlin
class PatientMonitorViewModel(
    private val repository: PatientRepository
) : ViewModel() {
    
    private val _patients = MutableLiveData<List<Patient>>()
    val patients: LiveData<List<Patient>> = _patients
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadPatients(tutorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPatients(tutorId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { patientList ->
                    _patients.value = patientList
                    _isLoading.value = false
                }
        }
    }
    
    fun updateVitals(patientId: String, vitals: Vitals) {
        viewModelScope.launch {
            try {
                repository.updatePatientVitals(patientId, vitals)
            } catch (e: Exception) {
                _error.value = "Error updating vitals: ${e.message}"
            }
        }
    }
}
```

---

## 5. TEST DE INTEGRACIÓN

### 5.1 Verificar Conexión en Emulador

```bash
# Dentro del emulador, accede al backend en host
# 10.0.2.2 es la IP del host desde el emulador

# En tu app, usa:
# BASE_URL = "http://10.0.2.2:8080/api"
```

### 5.2 Test Unitario de la API

```kotlin
@RunWith(MockitoJUnitRunner::class)
class PatientServiceTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: PatientService
    
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(PatientService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun getPatients_ReturnsCorrectData() = runTest {
        // Arrange
        val mockResponse = """
            {
              "patients": [
                {
                  "id": "1",
                  "name": "Abuela",
                  "age": 75,
                  "condition": "Alzheimer",
                  "lastVitals": {
                    "heartRate": 72,
                    "bloodPressure": "120/80",
                    "temperature": 36.5,
                    "timestamp": "2024-12-18T18:30:00Z"
                  },
                  "riskLevel": "MODERATE"
                }
              ]
            }
        """
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )
        
        // Act
        val response = apiService.getPatients("tutor_123")
        
        // Assert
        assertThat(response.patients).isNotEmpty()
        assertThat(response.patients[0].name).isEqualTo("Abuela")
    }
}
```

---

## 6. INTERCEPTOR PARA AUTENTICACIÓN

Agrega token automáticamente a cada request:

```kotlin
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = tokenProvider()
        val request = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(request)
    }
}

// Usar en Retrofit:
val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor { authToken })
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080/api")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

---

## 7. MANEJO DE ERRORES

```kotlin
class ApiException(
    val code: Int,
    override val message: String
) : Exception()

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: HttpException) {
        Result.failure(ApiException(e.code(), e.message()))
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## 8. VERIFICACIÓN CHECKLIST

- [ ] Backend compilable en Spring Boot
- [ ] Endpoints responden en Postman
- [ ] Base de datos tiene datos de prueba
- [ ] Token JWT se genera correctamente
- [ ] App conecta a `10.0.2.2:8080` desde emulador
- [ ] Login funciona
- [ ] Obtiene lista de pacientes
- [ ] Actualiza vitales
- [ ] Maneja errores (sin crashear)
- [ ] Tests unitarios pasan
- [ ] APK compila sin errores

---

## 9. CONFIGURACIÓN LOCAL PARA PRESENTACIÓN

Para que funcione durante la presentación:

```kotlin
// En tu BuildConfig o Constants.kt
object ApiConfig {
    const val BASE_URL = if (BuildConfig.DEBUG) {
        "http://10.0.2.2:8080/api"  // Emulador
    } else {
        "http://tu-ip-publica:8080/api"  // Dispositivo real
    }
}

// En Retrofit setup:
val retrofit = Retrofit.Builder()
    .baseUrl(ApiConfig.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

---

## 10. LOGS PARA DEBUGGING

```kotlin
// Agregar interceptor de logging
val httpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    })
    .build()
```

Ver logs en Android Studio:
```
Logcat → Filter: "OkHttp"
```

---

¡Con esto tu app estará completamente integrada con el backend! 🔗✅
