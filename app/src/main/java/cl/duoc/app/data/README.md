# Data Layer

Esta capa contiene las implementaciones concretas de los repositorios y las fuentes de datos.

## Estructura:
- `local/`: Fuentes de datos locales (Room, SharedPreferences)
- `repository/`: Implementaciones concretas de los repositorios definidos en domain

## Responsabilidades:
- Gestionar el acceso a datos (local y remoto)
- Implementar los contratos definidos en la capa de dominio
- Transformar datos entre modelos de datos y entidades de dominio
