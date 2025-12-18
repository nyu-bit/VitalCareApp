package cl.duoc.app.data.repository

import cl.duoc.app.data.model.LoginRequest
import cl.duoc.app.data.model.LoginResponse
import cl.duoc.app.data.remote.PatientService
import cl.duoc.app.data.session.SessionManager
import cl.duoc.app.network.safeApiCall

/**
 * Auth repository según Integracion_Backend.md sección 2.1 (POST /api/auth/login)
 */
class AuthRepository(
    private val api: PatientService,
    private val sessionManager: SessionManager
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return safeApiCall {
            api.login(LoginRequest(email = email, password = password))
        }.onSuccess { response ->
            sessionManager.updateSession(
                token = response.token,
                tutorId = response.tutorId,
                tutorName = response.name
            )
        }
    }

    fun logout() {
        sessionManager.clear()
    }
}
