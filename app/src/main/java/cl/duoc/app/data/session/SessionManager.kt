package cl.duoc.app.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mantiene sesión en memoria.
 *
 * Basado en Integracion_Backend.md (sección 2.1): login retorna token + tutorId + name.
 */
class SessionManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _tutorId = MutableStateFlow<String?>(null)
    val tutorId: StateFlow<String?> = _tutorId.asStateFlow()

    private val _tutorName = MutableStateFlow<String?>(null)
    val tutorName: StateFlow<String?> = _tutorName.asStateFlow()

    fun updateSession(token: String, tutorId: String, tutorName: String) {
        _token.value = token
        _tutorId.value = tutorId
        _tutorName.value = tutorName
    }

    fun clear() {
        _token.value = null
        _tutorId.value = null
        _tutorName.value = null
    }
}
