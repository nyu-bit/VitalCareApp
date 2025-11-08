# Domain Layer

Esta capa contiene la lógica de negocio de la aplicación.

## Estructura:
- `usecase/`: Casos de uso (interactores) que encapsulan lógica de negocio
- `repository/`: Interfaces de repositorios (contratos)

## Responsabilidades:
- Definir la lógica de negocio pura
- Ser independiente de frameworks y UI
- Definir contratos que la capa de datos debe implementar
