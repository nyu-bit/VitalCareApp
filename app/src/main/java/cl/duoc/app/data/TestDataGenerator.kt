package cl.duoc.app.data

import cl.duoc.app.model.HealthCenter

/**
 * Generador de datos de prueba para la aplicación
 * Proporciona datos simulados para testing y demostración
 */
object TestDataGenerator {

    /**
     * Genera centros de salud de prueba
     * Incluye centros reales en Santiago, Chile
     */
    fun generateHealthCenters(): List<HealthCenter> {
        return listOf(
            HealthCenter(
                id = "center_1",
                name = "Centro de Salud Mental Santiago Centro",
                address = "Avenida Libertador Bernardo O'Higgins 1449, Santiago",
                latitude = -33.4548,
                longitude = -70.6654,
                phone = "+56 2 2688 9000",
                email = "contacto@csmsantiago.cl",
                schedule = "Lunes a Viernes: 8:00 - 18:00\nSábados: 9:00 - 13:00"
            ),
            HealthCenter(
                id = "center_2",
                name = "Clínica Psiquiátrica Universitaria",
                address = "Avenida Independencia 1027, Santiago",
                latitude = -33.4365,
                longitude = -70.6742,
                phone = "+56 2 2978 6000",
                email = "psiquiatria@clinicauni.cl",
                schedule = "Lunes a Viernes: 8:00 - 19:00"
            ),
            HealthCenter(
                id = "center_3",
                name = "Instituto Psicopedagógico de Santiago",
                address = "Avenida Salvador 2851, Ñuñoa",
                latitude = -33.4257,
                longitude = -70.5890,
                phone = "+56 2 2225 8900",
                email = "info@ips.cl",
                schedule = "Lunes a Viernes: 8:00 - 17:00\nSábados: 9:00 - 12:00"
            ),
            HealthCenter(
                id = "center_4",
                name = "Centro de Salud Mental Providencia",
                address = "Avenida 11 de Septiembre 1981, Providencia",
                latitude = -33.4149,
                longitude = -70.6050,
                phone = "+56 2 2366 7700",
                email = "csm.providencia@gmail.com",
                schedule = "Lunes a Viernes: 8:00 - 18:00"
            ),
            HealthCenter(
                id = "center_5",
                name = "Fundación Espíritu de Salud Mental",
                address = "Calle Lastarria 45, Santiago",
                latitude = -33.4383,
                longitude = -70.6589,
                phone = "+56 2 2633 2400",
                email = "contacto@espiritusalud.org",
                schedule = "Lunes a Viernes: 9:00 - 17:00"
            )
        )
    }

    /**
     * Obtiene el centro de salud principal (por defecto)
     */
    fun getMainHealthCenter(): HealthCenter {
        return generateHealthCenters().first()
    }

    /**
     * Genera una lista de usuarios de prueba
     */
    fun generateTestUsers() = listOf(
        cl.duoc.app.model.User(
            id = "user_1",
            name = "Juan García",
            email = "juan.garcia@example.com",
            phone = "+56912345678",
            rut = "12.345.678-9",
            birthDate = "1990-05-15",
            address = "Calle Principal 123, Santiago"
        ),
        cl.duoc.app.model.User(
            id = "user_2",
            name = "María López",
            email = "maria.lopez@example.com",
            phone = "+56987654321",
            rut = "87.654.321-0",
            birthDate = "1985-03-22",
            address = "Avenida Central 456, Providencia"
        )
    )
}

