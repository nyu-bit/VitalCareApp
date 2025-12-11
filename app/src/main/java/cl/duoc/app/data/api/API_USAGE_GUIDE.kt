package cl.duoc.app.data.api

/**
 * DOCUMENTACIÓN DE USO - INTERFACES RETROFIT Y DTOs
 *
 * ============================================================
 * 1. VITALES API (http://10.0.2.2:8081/)
 * ============================================================
 *
 * Obtener todos los signos vitales:
 * ```
 * val vitalesApi = RetrofitInstance.getVitalesApi()
 * val signos = vitalesApi.getAllVitales()
 * ```
 *
 * Obtener signos vitales de un paciente:
 * ```
 * val vitalesApi = RetrofitInstance.getVitalesApi()
 * val signos = vitalesApi.getVitalesByPaciente("pacienteId123")
 * ```
 *
 * Crear nuevo registro de signos vitales:
 * ```
 * val vitalesApi = RetrofitInstance.getVitalesApi()
 * val nuevoSigno = SignosVitalesDto(
 *     pacienteId = "paciente123",
 *     frecuenciaCardiaca = 72,
 *     presionArterialSistolica = 120,
 *     presionArterialDiastolica = 80,
 *     saturacionOxigeno = 98,
 *     temperatura = 37.5
 * )
 * val resultado = vitalesApi.createVitales(nuevoSigno)
 * ```
 *
 * Eliminar un registro de signos vitales:
 * ```
 * val vitalesApi = RetrofitInstance.getVitalesApi()
 * vitalesApi.deleteVitales("vitalesId123")
 * ```
 *
 * ============================================================
 * 2. UBICACION API (http://10.0.2.2:8082/)
 * ============================================================
 *
 * Obtener todas las ubicaciones:
 * ```
 * val ubicacionApi = RetrofitInstance.getUbicacionApi()
 * val ubicaciones = ubicacionApi.getAllUbicaciones()
 * ```
 *
 * Obtener ubicaciones de un paciente:
 * ```
 * val ubicacionApi = RetrofitInstance.getUbicacionApi()
 * val ubicaciones = ubicacionApi.getUbicacionesByPaciente("pacienteId123")
 * ```
 *
 * Crear nueva ubicación:
 * ```
 * val ubicacionApi = RetrofitInstance.getUbicacionApi()
 * val nuevaUbicacion = UbicacionDto(
 *     pacienteId = "paciente123",
 *     latitud = -33.8688,
 *     longitud = -71.5203,
 *     direccion = "Calle Principal 123",
 *     ciudad = "Santiago",
 *     pais = "Chile"
 * )
 * val resultado = ubicacionApi.createUbicacion(nuevaUbicacion)
 * ```
 *
 * ============================================================
 * 3. ALERTAS API (http://10.0.2.2:8083/)
 * ============================================================
 *
 * Obtener todas las alertas:
 * ```
 * val alertasApi = RetrofitInstance.getAlertasApi()
 * val alertas = alertasApi.getAllAlertas()
 * ```
 *
 * Obtener alertas de un paciente:
 * ```
 * val alertasApi = RetrofitInstance.getAlertasApi()
 * val alertas = alertasApi.getAlertasByPaciente("pacienteId123")
 * ```
 *
 * Crear nueva alerta:
 * ```
 * val alertasApi = RetrofitInstance.getAlertasApi()
 * val nuevaAlerta = AlertaDto(
 *     pacienteId = "paciente123",
 *     titulo = "Presión Arterial Alta",
 *     mensaje = "La presión arterial está fuera de rango normal",
 *     severidad = "Alto",
 *     tipo = "Signos Vitales"
 * )
 * val resultado = alertasApi.createAlerta(nuevaAlerta)
 * ```
 *
 * Actualizar una alerta:
 * ```
 * val alertasApi = RetrofitInstance.getAlertasApi()
 * val alertaActualizada = AlertaDto(
 *     id = "alerta123",
 *     pacienteId = "paciente123",
 *     titulo = "Presión Arterial Normalizada",
 *     mensaje = "La presión arterial se ha normalizado",
 *     severidad = "Bajo",
 *     tipo = "Signos Vitales",
 *     leida = true
 * )
 * val resultado = alertasApi.updateAlerta("alerta123", alertaActualizada)
 * ```
 *
 * Eliminar una alerta:
 * ```
 * val alertasApi = RetrofitInstance.getAlertasApi()
 * alertasApi.deleteAlerta("alertaId123")
 * ```
 *
 * ============================================================
 * EJEMPLO EN CORRUTINA (Uso típico en ViewModel)
 * ============================================================
 *
 * ```
 * viewModelScope.launch {
 *     try {
 *         val vitalesApi = RetrofitInstance.getVitalesApi()
 *         val signos = vitalesApi.getAllVitales()
 *         // Procesar signos
 *         _vitalesState.value = signos
 *     } catch (e: Exception) {
 *         // Manejar error
 *         _error.value = e.message
 *     }
 * }
 * ```
 */

