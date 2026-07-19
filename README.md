# Restaurant Rate

Aplicación Android nativa para descubrir, registrar y reseñar restaurantes. Los usuarios se autentican, exploran un listado de restaurantes con su calificación promedio, ven el detalle con las reseñas de la comunidad y pueden crear, editar y eliminar tanto restaurantes como sus propias reseñas.

## Tabla de contenidos

- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [API backend](#api-backend)
- [Autenticación](#autenticación)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Navegación](#navegación)

## Stack tecnológico

| Área | Tecnología |
|------|------------|
| Lenguaje | Kotlin `2.0.21` |
| UI | Jetpack Compose (Material 3) |
| Navegación | Navigation Compose |
| Red | Retrofit `3.0.0` + Gson + OkHttp |
| Arquitectura | MVVM (ViewModel + StateFlow/Compose State) |
| Gestos | [saket/swipe](https://github.com/saket/swipe) para acciones deslizables |
| Build | Gradle (Kotlin DSL) + AGP `9.0.1` |
| SDK | `minSdk 24` · `targetSdk 36` · `compileSdk 36` |

## Arquitectura

El proyecto sigue el patrón **MVVM** con una capa de datos separada de la capa de UI:

```
UI (Compose)  ─►  ViewModel  ─►  Api (Retrofit)  ─►  Backend REST
   ▲                  │
   └──── State ◄──────┘
```

- **`data/api`** — Interfaces Retrofit que definen los endpoints (`AuthApi`, `RestaurantApi`, `ReviewApi`).
- **`data/model`** — Data classes (DTOs) para las peticiones y respuestas.
- **`data/network`** — Cliente HTTP (`ApiClient`), inyección del token (`AuthInterceptor`) y gestión de sesión (`TokenManager`).
- **`ui/<feature>`** — Cada feature agrupa su `Screen` de Compose, su `ViewModel` y componentes reutilizables.

## Estructura del proyecto

```
app/src/main/java/com/chiris/app/restaurant_rate/
├── MainActivity.kt
├── data/
│   ├── api/
│   │   ├── AuthApi.kt            # Login
│   │   ├── RestaurantApi.kt      # CRUD de restaurantes
│   │   └── ReviewApi.kt          # CRUD de reseñas
│   ├── model/
│   │   ├── Login.kt              # LoginRequest / LoginResponse
│   │   └── Restaurant.kt         # DTOs de restaurantes y reseñas
│   └── network/
│       ├── ApiClient.kt          # Configuración de Retrofit + OkHttp
│       ├── AuthInterceptor.kt    # Añade el header Authorization
│       └── TokenManager.kt       # Guarda el JWT y decodifica sus claims
├── ui/
│   ├── login/                    # Pantalla y ViewModel de login
│   ├── navigation/               # AppNavigation + rutas (Screen)
│   ├── restaurant/
│   │   ├── list/                 # Listado, búsqueda y filtros
│   │   ├── detail/               # Detalle + reseñas
│   │   ├── create/               # Crear restaurante
│   │   └── edit/                 # Editar restaurante
│   ├── usuario/                  # Gestión de usuarios (solo ADMIN)
│   │   ├── list/                 # Listado + eliminar
│   │   ├── create/               # Crear usuario
│   │   ├── edit/                 # Editar usuario
│   │   └── component/            # RolSelector (ADMIN/USER)
│   └── theme/                    # Colores, tipografía y tema Material 3
└── utils/
    └── Constants.kt              # BASE_URL y paths de la API
```

## Funcionalidades

- **Autenticación** con email y contraseña (JWT).
- **Listado de restaurantes** con nombre, tipo de comida y calificación promedio.
- **Búsqueda** server-side por nombre o tipo de comida.
- **Filtros** mediante bottom sheet.
- **Detalle del restaurante** con dirección, teléfono y listado de reseñas.
- **Restaurantes**: crear, editar (PATCH parcial) y eliminar.
- **Reseñas**: crear, editar y eliminar las propias (el backend valida la autoría; la app usa el `userId` del JWT para mostrar las acciones solo al autor).
- **Usuarios (solo ADMIN)**: listar, crear, editar (PATCH parcial) y eliminar usuarios, con asignación de rol `ADMIN`/`USER`. Accesible desde el icono 👥 de la barra superior. Si el usuario autenticado no es administrador, el backend responde `403` y la app muestra un mensaje de "sin permisos".

## API backend

Todos los endpoints se sirven bajo la `BASE_URL` configurada en `utils/Constants.kt`.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/login` | Autenticación, devuelve un token JWT |
| `GET` | `/api/v1/restaurantes?query=` | Lista restaurantes (filtro opcional por nombre/tipo) |
| `POST` | `/api/v1/restaurantes` | Crea un restaurante |
| `GET` | `/api/v1/restaurantes/{id}` | Detalle de un restaurante con sus reseñas |
| `PATCH` | `/api/v1/restaurantes/{id}` | Actualiza parcialmente un restaurante |
| `DELETE` | `/api/v1/restaurantes/{id}` | Elimina un restaurante |
| `POST` | `/api/v1/resenas` | Crea una reseña |
| `PATCH` | `/api/v1/resenas/{id}` | Edita una reseña propia |
| `DELETE` | `/api/v1/resenas/{id}` | Elimina una reseña propia |
| `GET` | `/api/v1/usuarios` | Lista usuarios · **solo ADMIN** |
| `GET` | `/api/v1/usuarios/{id}` | Detalle de un usuario · **solo ADMIN** |
| `POST` | `/api/v1/usuarios` | Crea un usuario · **solo ADMIN** |
| `PATCH` | `/api/v1/usuarios/{id}` | Actualiza parcialmente un usuario · **solo ADMIN** |
| `DELETE` | `/api/v1/usuarios/{id}` | Elimina un usuario · **solo ADMIN** |

> El campo `password` es de solo escritura: se envía al crear/editar pero nunca se devuelve en las respuestas. En el PATCH parcial, la contraseña solo se actualiza si se envía un valor no vacío.

## Roles y permisos

La API define dos roles (`ADMIN` y `USER`):

- Cualquier usuario autenticado puede consultar restaurantes y gestionar sus propias reseñas.
- La **gestión de usuarios** (`/api/v1/usuarios`) está restringida a **ADMIN** en el backend (`SecurityConfig`).

> **Importante:** el JWT emitido por el backend contiene únicamente los claims `userId` y `sub` (email), **no incluye el rol**. Por eso la app no puede saber de antemano si el usuario es administrador: la pantalla de usuarios intenta cargar la lista y, si recibe un `403 Forbidden`, muestra el mensaje de "sin permisos" y oculta las acciones de gestión.

## Autenticación

- Tras el login, el JWT se guarda en memoria en `TokenManager`.
- `AuthInterceptor` añade automáticamente el header `Authorization` a cada petición saliente.
- `TokenManager` decodifica el payload del JWT para obtener el `userId` (claim `userId`) y el email (claim `sub`), que se usan para identificar las reseñas del usuario actual.

> Nota: el token se mantiene solo en memoria, por lo que la sesión se pierde al cerrar la app.

## Configuración y ejecución

### Requisitos

- Android Studio (versión reciente, con soporte para AGP 9.x).
- JDK 11.
- Un backend REST compatible con los endpoints descritos arriba.

### Configurar la URL del backend

La URL base se define en `app/src/main/java/com/chiris/app/restaurant_rate/utils/Constants.kt`:

```kotlin
object Constants {
    const val BASE_URL = "http://10.0.2.2:8080"   // 10.0.2.2 = localhost del host desde el emulador
    const val OBJECTS_PATH = "/api/v1/restaurantes"
}
```

- `10.0.2.2` apunta al `localhost` de la máquina anfitriona desde el **emulador** de Android.
- Para un dispositivo físico, reemplaza por la IP de tu máquina en la red local.
- El manifest habilita `usesCleartextTraffic="true"` para permitir HTTP en desarrollo.

### Compilar y ejecutar

```bash
# Compilar
./gradlew assembleDebug

# Instalar en un emulador/dispositivo conectado
./gradlew installDebug

# Ejecutar los tests
./gradlew test
```

También puedes abrir el proyecto en Android Studio y ejecutarlo con el botón **Run**.

## Navegación

El grafo de navegación (`ui/navigation/AppNavigation.kt`) define las siguientes rutas:

| Ruta | Pantalla |
|------|----------|
| `login` | Inicio de sesión |
| `main` | Listado de restaurantes |
| `create_restaurant` | Crear restaurante |
| `detail/{id}` | Detalle de restaurante y reseñas |
| `edit_restaurant/{id}` | Editar restaurante |
| `usuarios` | Listado de usuarios (solo ADMIN) |
| `create_usuario` | Crear usuario |
| `edit_usuario/{id}` | Editar usuario |
