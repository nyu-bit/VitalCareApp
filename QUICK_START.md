# 🚀 Quick Start Guide - VitalCareApp

## ⚡ Configuración Rápida para Desarrolladores

### Requisitos Previos
- Android Studio Koala o superior
- JDK 11+
- Android SDK 36
- Gradle 8.13+

### Instalación (3 minutos)

```bash
# 1. Clonar repositorio
git clone <repo-url>
cd VitalCareApp

# 2. Abrir en Android Studio
# File > Open > seleccionar VitalCareApp

# 3. Gradle se sincronizará automáticamente
# Esperar a que termine
```

---

## 🧪 Ejecutar Tests

### Opción 1: Terminal (Recomendado)
```bash
# Todos los tests
./gradlew testDebugUnitTest

# Tests específicos
./gradlew testDebugUnitTest --tests CitaRepositoryTest
./gradlew testDebugUnitTest --tests ValidatorsTest

# Con reporte detallado
./gradlew testDebugUnitTest --info
```

### Opción 2: Android Studio UI
1. Click derecho en `app/src/test`
2. Seleccionar "Run Tests"
3. Ver resultados en ventana de test

### Ver Reportes
Después de ejecutar tests:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 🏗️ Estructura del Proyecto

```
Data Layer          Domain Layer        Presentation Layer
└─ Room DB          ├─ Validators       ├─ Compose UI
   ├─ DAO           └─ Use Cases        ├─ ViewModels
   ├─ Entity                            └─ Navigation
   └─ Repository
```

---

## 📝 Notas Importantes

### MockKAnnotations en Tests
Todos los tests repository deben incluir:
```kotlin
@Before
fun setup() {
    MockKAnnotations.init(this)  // ⭐ IMPORTANTE
    mockDao = mockk()
    repository = CitaRepository(mockDao)
}
```

### Validación de RUT
Los RUT en tests deben ser válidos:
- ❌ `12345678-9` (incorrecto)
- ✅ `12345678-5` (correcto)

Para calcular DV:
1. Multiplicar cada dígito por [2,3,4,5,6,7,2,3...]
2. Sumar resultados
3. DV = 11 - (suma % 11)

### Flow vs Suspend
- **Flow**: Para datos que cambian → `.todasCitas`
- **Suspend**: Para datos únicos → `.getCitaByIdSync(id)`

---

## 🔄 Workflow Típico

### 1. Crear Nueva Funcionalidad

```kotlin
// 1. Agregar método al DAO
interface CitaDao {
    @Query("SELECT * FROM citas")
    suspend fun getAllCitas(): List<Cita>
}

// 2. Agregar al Repository
class CitaRepository {
    fun getAllCitas(): Flow<List<Cita>> {
        return citaDao.getAllCitas()
    }
}

// 3. Escribir Test
@Test
fun testGetAllCitas() = runTest {
    every { mockDao.getAllCitas() } returns flowOf(listOf(...))
    val result = repository.todasCitas
    assertNotNull(result)
}

// 4. Ejecutar test
./gradlew testDebugUnitTest --tests CitaRepositoryTest.testGetAllCitas
```

### 2. Validar Datos

```kotlin
// En UI o ViewModel
val validation = Validators.validateRut(rutIngresado)
if (!validation.isValid) {
    mostrarError(validation.errorMessage)
    return
}
// Continuar con inserción
```

---

## 🐛 Troubleshooting Común

### Error: "MockKException"
**Causa:** Falta `MockKAnnotations.init(this)`  
**Solución:** Agregar en método `@Before`

### Error: "Cannot find symbol 'Log'"
**Causa:** `Log` de Android en tests unitarios  
**Solución:** Usar `println()` o agregar dependencia

### Error: "Unresolved reference"
**Causa:** Gradle no sincronizado  
**Solución:** 
```bash
./gradlew clean
./gradlew build
```

### Tests ejecutándose lentamente
**Causa:** Primer build  
**Solución:** Paciencia 🎯 (toma ~30-40 segundos primera vez)

---

## 📊 Test Coverage

### Cobertura Actual
| Capa | Cobertura |
|------|-----------|
| Repository | 100% |
| Validators | 100% |
| DAO Interface | 100% |
| Entity Classes | 100% |

### Cómo Mejorar Cobertura
1. Agregar test para casos negativos
2. Testear excepciones
3. Validar límites de datos
4. Probar flujos complejos

---

## 🎯 Mejores Prácticas

### ✅ Hacer
```kotlin
// Usar try-catch en funciones que lanzan excepciones
@Test
fun testInsertInvalid() = runTest {
    assertThrows<Exception> {
        repository.insert(null)
    }
}

// Testear casos vacíos
@Test
fun testGetCitasEmpty() = runTest {
    every { mockDao.getAllCitas() } returns flowOf(emptyList())
    val result = repository.todasCitas
    assertNotNull(result)
}

// Nombrar tests descriptivamente
fun testGetCitasByPacienteWhenPacienteExists()
```

### ❌ No Hacer
```kotlin
// No usar datos sin validar
val rut = "invalido"  // ❌

// No hardcodear IDs
val id = 1L  // ❌ Usar factory

// No ignorar excepciones
try { ... } catch (e: Exception) { }  // ❌
```

---

## 🚦 Checklist Pre-Commit

- [ ] Todos los tests pasan (`./gradlew testDebugUnitTest`)
- [ ] Código compilado sin warnings
- [ ] Documentación actualizada
- [ ] No hay logs de debug
- [ ] Variables bien nombradas
- [ ] Métodos respetan pattern AAA

---

## 📚 Recursos Útiles

- [Room Database](https://developer.android.com/training/data-storage/room)
- [MockK Documentation](https://mockk.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Jetpack Compose](https://developer.android.com/compose)

---

## 🤝 Contribuir

### Pasos para Contribuir
1. Fork el repositorio
2. Crear rama: `git checkout -b feature/tu-feature`
3. Commit cambios: `git commit -am 'Add feature'`
4. Push a rama: `git push origin feature/tu-feature`
5. Pull Request

### Estándares de Código
- Kotlin style guide
- Nombres descriptivos
- Métodos pequeños y enfocados
- Documentación en clases públicas

---

## 🆘 Soporte

### Para Errores de Tests
```bash
# Ver detalles completos
./gradlew testDebugUnitTest --info

# Stack trace
./gradlew testDebugUnitTest --stacktrace
```

### Contacto
- Documentación: Ver TESTS_DOCUMENTATION.md
- Arquitectura: Ver README_ANDROID.md
- Resumen: Ver PROJECT_SUMMARY.md

---

## ✅ Checklist Inicial

- [ ] Android Studio abierto
- [ ] Gradle sincronizado
- [ ] Tests ejecutados: `./gradlew testDebugUnitTest`
- [ ] Reporte visualizado
- [ ] 159/159 tests pasando

**¡Listo para desarrollar! 🚀**

---

Última actualización: Diciembre 2024

