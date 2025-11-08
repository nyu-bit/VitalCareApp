package cl.duoc.app.data.local.room

import cl.duoc.app.model.*

/**
 * Mappers para convertir entre entidades de Room y modelos de dominio
 * Estos mappers mantienen separadas las capas de datos y dominio
 */

/**
 * Convierte UserEntity (Room) a User (Dominio)
 */
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        createdAt = this.createdAt
    )
}

/**
 * Convierte User (Dominio) a UserEntity (Room)
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        createdAt = this.createdAt
    )
}

/**
 * Convierte lista de UserEntity a lista de User
 */
fun List<UserEntity>.toDomainList(): List<User> {
    return this.map { it.toDomain() }
}

/**
 * Convierte ReservationEntity (Room) a Reservation (Dominio)
 */
fun ReservationEntity.toDomain(): Reservation {
    return Reservation(
        id = this.id,
        userId = this.userId,
        specialty = this.specialty,
        doctorName = this.doctorName,
        date = this.date,
        status = ReservationStatus.valueOf(this.status)
    )
}

/**
 * Convierte Reservation (Dominio) a ReservationEntity (Room)
 */
fun Reservation.toEntity(): ReservationEntity {
    return ReservationEntity(
        id = this.id,
        userId = this.userId,
        specialty = this.specialty,
        doctorName = this.doctorName,
        date = this.date,
        status = this.status.name
    )
}

/**
 * Convierte lista de ReservationEntity a lista de Reservation
 */
fun List<ReservationEntity>.toReservationDomainList(): List<Reservation> {
    return this.map { it.toDomain() }
}

/**
 * Convierte VitalSignsEntity (Room) a VitalSigns (Dominio)
 */
fun VitalSignsEntity.toDomain(): VitalSigns {
    return VitalSigns(
        id = this.id,
        userId = this.userId,
        heartRate = this.heartRate,
        bloodPressureSystolic = this.bloodPressureSystolic,
        bloodPressureDiastolic = this.bloodPressureDiastolic,
        oxygenSaturation = this.oxygenSaturation,
        timestamp = this.timestamp
    )
}

/**
 * Convierte VitalSigns (Dominio) a VitalSignsEntity (Room)
 */
fun VitalSigns.toEntity(): VitalSignsEntity {
    return VitalSignsEntity(
        id = this.id,
        userId = this.userId,
        heartRate = this.heartRate,
        bloodPressureSystolic = this.bloodPressureSystolic,
        bloodPressureDiastolic = this.bloodPressureDiastolic,
        oxygenSaturation = this.oxygenSaturation,
        timestamp = this.timestamp
    )
}

/**
 * Convierte lista de VitalSignsEntity a lista de VitalSigns
 */
fun List<VitalSignsEntity>.toVitalSignsDomainList(): List<VitalSigns> {
    return this.map { it.toDomain() }
}
