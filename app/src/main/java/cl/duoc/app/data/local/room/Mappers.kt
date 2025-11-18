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
        rut = this.rut,
        birthDate = this.birthDate,
        address = this.address,
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
        rut = this.rut,
        birthDate = this.birthDate,
        address = this.address,
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
        heartRate = this.heartRate ?: 0,
        bloodPressureSystolic = this.bloodPressureSystolic ?: 0,
        bloodPressureDiastolic = this.bloodPressureDiastolic ?: 0,
        oxygenSaturation = this.oxygenSaturation ?: 0,
        timestamp = this.timestamp
    )
}

/**
 * Convierte lista de VitalSignsEntity a lista de VitalSigns
 */
fun List<VitalSignsEntity>.toVitalSignsDomainList(): List<VitalSigns> {
    return this.map { it.toDomain() }
}

/**
 * Convierte SOSEventEntity (Room) a SOSEvent (Dominio)
 */
fun SOSEventEntity.toDomain(): SOSEvent {
    return SOSEvent(
        id = this.id,
        userId = this.userId,
        location = LocationData(
            latitude = this.latitude,
            longitude = this.longitude,
            accuracy = this.accuracy,
            timestamp = this.timestamp
        ),
        timestamp = this.timestamp,
        status = this.status,
        tutorNotified = this.tutorNotified
    )
}

/**
 * Convierte SOSEvent (Dominio) a SOSEventEntity (Room)
 */
fun SOSEvent.toEntity(): SOSEventEntity {
    return SOSEventEntity(
        id = this.id,
        userId = this.userId,
        latitude = this.location.latitude,
        longitude = this.location.longitude,
        accuracy = this.location.accuracy,
        timestamp = this.timestamp,
        status = this.status,
        tutorNotified = this.tutorNotified
    )
}

/**
 * Convierte lista de SOSEventEntity a lista de SOSEvent
 */
fun List<SOSEventEntity>.toSOSEventDomainList(): List<SOSEvent> {
    return this.map { it.toDomain() }
}

/**
 * Convierte HealthCenterEntity (Room) a HealthCenter (Dominio)
 */
fun HealthCenterEntity.toDomain(): HealthCenter {
    return HealthCenter(
        id = this.id,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        phone = this.phone,
        email = this.email,
        schedule = this.schedule
    )
}

/**
 * Convierte HealthCenter (Dominio) a HealthCenterEntity (Room)
 */
fun HealthCenter.toEntity(): HealthCenterEntity {
    return HealthCenterEntity(
        id = this.id,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        phone = this.phone,
        email = this.email,
        schedule = this.schedule
    )
}

/**
 * Convierte lista de HealthCenterEntity a lista de HealthCenter
 */
fun List<HealthCenterEntity>.toHealthCenterDomainList(): List<HealthCenter> {
    return this.map { it.toDomain() }
}
