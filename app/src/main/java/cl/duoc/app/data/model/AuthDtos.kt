package cl.duoc.app.data.model

/**
 * DTOs según Integracion_Backend.md, sección 2.1 (POST /api/auth/login)
 */
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val tutorId: String,
    val name: String
)
