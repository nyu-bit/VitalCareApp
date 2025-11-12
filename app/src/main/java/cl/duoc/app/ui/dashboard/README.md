# Pantalla de Dashboard - VitalCare

## Descripción
Implementación de un Dashboard principal interactivo para visualizar signos vitales del paciente en VitalCare – App Tutor usando Jetpack Compose y Material3.

**Historia de Usuario**: HU-11 (Visualización de signos vitales del paciente)

## Estructura de Archivos

### 1. DashboardScreen.kt
Composable principal que implementa la UI del Dashboard con tarjetas de signos vitales.

### 2. DashboardViewModel.kt
ViewModel que maneja el estado y la lógica de obtención de datos de signos vitales.

## Características Implementadas

### ✅ Tarjetas de Signos Vitales

El Dashboard muestra 4 tarjetas con los signos vitales principales:

#### 1. Frecuencia Cardíaca
- **Ícono**: ❤️ (Favorite)
- **Valor**: XX bpm (latidos por minuto)
- **Rangos**:
  - 🟢 Normal: 60-100 bpm
  - 🟡 Observación: 45-60 o 100-120 bpm
  - 🔴 Alerta: <45 o >120 bpm

#### 2. Presión Arterial
- **Ícono**: 📊 (MonitorHeart)
- **Valor**: XXX/XX mmHg (sistólica/diastólica)
- **Rangos**:
  - 🟢 Normal: 90-120/60-80 mmHg
  - 🟡 Observación: 80-90 o 120-140 sistólica
  - 🔴 Alerta: <80 o >140 sistólica

#### 3. Temperatura
- **Ícono**: 🌡️ (Thermostat)
- **Valor**: XX.X °C
- **Rangos**:
  - 🟢 Normal: 36.1-37.2 °C
  - 🟡 Observación: 35.5-36.0 o 37.3-38.0 °C
  - 🔴 Alerta: <35.5 o >38.0 °C

#### 4. Nivel de Oxígeno
- **Ícono**: 💨 (Air)
- **Valor**: XX %
- **Rangos**:
  - 🟢 Normal: 95-100%
  - 🟡 Observación: 90-94%
  - 🔴 Alerta: <90%

### ✅ Niveles de Alerta

Cada tarjeta incluye un indicador visual de estado:

| Estado | Color | Significado |
|--------|-------|-------------|
| 🟢 Estable | Verde (#4CAF50) | Valores normales |
| 🟡 Observación | Amarillo (#FFC107) | Valores ligeramente fuera de rango |
| 🔴 Alerta | Rojo (#F44336) | Valores críticos que requieren atención |

### ✅ Interactividad

- **Click en tarjeta**: Muestra Toast con "Detalle de [signo vital]"
- **Preparado** para navegación a pantalla de detalle

### ✅ Generación de Datos

**Función `generateSimulatedVitalSigns()`**:
- Genera valores aleatorios realistas
- Calcula automáticamente el nivel de alerta según rangos médicos
- Se ejecuta al iniciar la pantalla
- Simula datos recientes del paciente

### ✅ Diseño Material3

#### Componentes UI:
- **Scaffold** - Estructura principal
- **TopAppBar** - Barra superior con título
- **ElevatedCard** - Tarjetas con elevación
- **Surface** - Contenedores de íconos
- **Icon** - Iconografía Material
- **Column/Row** - Layouts flexibles

#### Estética:
- Jerarquía visual clara
- Espaciado uniforme (16dp entre tarjetas)
- Tipografía legible
- Alto contraste
- Fondo claro
- Esquinas redondeadas
- Sombras sutiles

## Componentes Utilizados

### Material Icons
- `Icons.Default.Favorite` - Frecuencia cardíaca
- `Icons.Default.MonitorHeart` - Presión arterial
- `Icons.Default.Thermostat` - Temperatura
- `Icons.Default.Air` - Nivel de oxígeno
- `Icons.Default.Info` - Información adicional

### Material3 Components
- `Scaffold` - Estructura de pantalla
- `TopAppBar` - Barra de aplicación
- `ElevatedCard` - Tarjetas elevadas
- `Surface` - Superficies con color
- `Icon` - Íconos vectoriales
- `Text` - Textos estilizados

## Data Classes

### VitalSignData
```kotlin
data class VitalSignData(
    val title: String,        // "Frecuencia Cardíaca"
    val value: String,        // "78"
    val unit: String,         // "bpm"
    val icon: ImageVector,    // Icons.Default.Favorite
    val alertLevel: AlertLevel // NORMAL, WARNING, DANGER
)
```

### AlertLevel
```kotlin
enum class AlertLevel {
    NORMAL,      // Verde - Estable
    WARNING,     // Amarillo - Observación
    DANGER       // Rojo - Alerta
}
```

## Arquitectura

```
┌─────────────────────────────────────┐
│       DashboardScreen.kt            │
│  - UI con tarjetas                  │
│  - Generación de datos simulados    │
│  - Interactividad (onClick)         │
└────────────┬────────────────────────┘
             │
             │ (opcional)
             │
┌────────────▼────────────────────────┐
│     DashboardViewModel.kt           │
│  - StateFlow para estado            │
│  - loadVitalSigns()                 │
│  - convertToVitalSignData()         │
│  - Integración con repositorio      │
└─────────────────────────────────────┘
```

## Flujo de Datos

### Carga Inicial
```
DashboardScreen() 
  → remember { generateSimulatedVitalSigns() }
    → Random.nextInt/nextDouble()
      → Cálculo de AlertLevel
        → Lista de VitalSignData
          → Renderizado de tarjetas
```

### Interacción
```
Usuario hace click en tarjeta
  → onClick() callback
    → Toast.makeText()
      → "Detalle de [signo vital]"
```

## Integración con el Modelo

El ViewModel incluye función `convertToVitalSignData()` que convierte:
```kotlin
cl.duoc.app.model.VitalSigns → List<VitalSignData>
```

Esto permite integrar fácilmente con repositorios y fuentes de datos reales.

## Cómo Usar

### Navegación
Después de login exitoso → Dashboard se muestra automáticamente

```kotlin
LoginScreen(
    onLoginSuccess = { currentScreen = "dashboard" }
)
```

### Vista Previa
```kotlin
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview()
```

## Rangos Médicos Implementados

### Frecuencia Cardíaca (bpm)
- ✅ Normal: 60-100
- ⚠️ Observación: 45-60 o 100-120
- ⛔ Peligro: <45 o >120

### Presión Arterial (mmHg)
- ✅ Normal: 90-120 / 60-80
- ⚠️ Observación: 80-90 o 120-140 sistólica
- ⛔ Peligro: <80 o >140 sistólica

### Temperatura (°C)
- ✅ Normal: 36.1-37.2
- ⚠️ Observación: 35.5-36.0 o 37.3-38.0
- ⛔ Peligro: <35.5 o >38.0

### Saturación de Oxígeno (%)
- ✅ Normal: 95-100
- ⚠️ Observación: 90-94
- ⛔ Peligro: <90

## Próximos Pasos (TODO)

- [ ] Integrar con repositorio real de signos vitales
- [ ] Implementar actualización automática cada 5 minutos
- [ ] Agregar gráficos de tendencias
- [ ] Pantalla de detalle para cada signo vital
- [ ] Historial de mediciones
- [ ] Notificaciones push para alertas críticas
- [ ] Filtros por fecha/rango
- [ ] Exportación de datos (PDF/Excel)
- [ ] Comparación con valores anteriores
- [ ] Botón de actualización manual (Pull to Refresh)

## Diseño Responsivo

- ✅ Scroll vertical para dispositivos pequeños
- ✅ Tarjetas de ancho completo
- ✅ Espaciado adaptable
- ✅ Tipografía escalable
- ✅ Íconos proporcionales

## Accesibilidad

- ✅ Descripciones de contenido en íconos
- ✅ Alto contraste de colores
- ✅ Texto legible (mínimo 12sp)
- ✅ Áreas táctiles amplias
- ✅ Indicadores visuales claros

## Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - Framework UI declarativo
- **Material3** - Sistema de diseño moderno
- **StateFlow** - Gestión de estado reactivo
- **ViewModel** - Arquitectura MVVM
- **Material Icons** - Iconografía consistente

## Pruebas

### Manual Testing
1. Abrir app y hacer login
2. Ver Dashboard con 4 tarjetas
3. Verificar que cada tarjeta muestra:
   - ✅ Ícono correcto
   - ✅ Título del signo vital
   - ✅ Valor con unidad
   - ✅ Indicador de estado coloreado
4. Click en cada tarjeta
5. Verificar Toast con mensaje correcto

### Casos de Prueba
- ✅ Generación aleatoria de valores
- ✅ Cálculo correcto de alertas
- ✅ Colores según nivel de alerta
- ✅ Toast al hacer click
- ✅ Scroll funcional
- ✅ Renderizado correcto en preview

## Capturas de Pantalla (Descripción)

### Estado Normal (Todo Verde)
- 4 tarjetas con indicadores verdes
- Todos los valores en rango normal
- Aspecto limpio y ordenado

### Estado Mixto
- Algunas tarjetas verdes
- Algunas tarjetas amarillas (observación)
- Posible tarjeta roja (alerta)

### Interacción
- Toast aparece al tocar tarjeta
- Mensaje claro y contextual

---

**Autor**: Implementado para VitalCare - MajoApp branch  
**Fecha**: Noviembre 2025  
**Historia de Usuario**: HU-11 (Visualización de signos vitales del paciente)
