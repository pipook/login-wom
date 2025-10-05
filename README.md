# Proyecto de Login con Spring Boot y JWT

Este proyecto implementa un sistema de autenticación y registro de usuarios utilizando Spring Boot, JWT y MySQL.

## Configuración del Proyecto

### Prerrequisitos

- **Java 21** o superior
- **Maven** (incluido en el proyecto con `mvnw`)
- **MySQL** como base de datos
- **Postman** o cualquier cliente HTTP para probar los endpoints

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto basado en el archivo `.env.example` y configura las siguientes variables:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/nombre_de_tu_base_de_datos
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña

JWT_PRIVATE_KEY=clave_privada_en_base64
JWT_PUBLIC_KEY=clave_publica_en_base64
JWT_EXPIRATION=900000
```

### Base de Datos

1. Crea una base de datos en MySQL.
2. Configura las credenciales en el archivo `.env`.
3. Ejecuta las migraciones de Flyway automáticamente al iniciar el proyecto.

### Instalación

1. Clona el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd login
   ```

2. Compila el proyecto:
   ```bash
   ./mvnw clean install
   ```

3. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`.

---

## Endpoints de la API

Todos los endpoints están prefijados con `/api`.

### **Autenticación**

#### 1. **Registro de Usuario**
- **URL:** `POST /api/auth/register`
- **Descripción:** Registra un nuevo usuario.
- **Cuerpo de la solicitud:**
  ```json
  {
    "username": "usuario",
    "email": "correo@ejemplo.com",
    "password": "contraseña"
  }
  ```
- **Respuesta exitosa:**
  ```json
  {
    "success": true,
    "message": "Usuario creado exitosamente",
    "data": {
      "id": 1,
      "username": "usuario",
      "email": "correo@ejemplo.com",
      "accessToken": "token_jwt",
      "refreshToken": null
    }
  }
  ```

#### 2. **Inicio de Sesión**
- **URL:** `POST /api/auth/login`
- **Descripción:** Autentica a un usuario y genera un token JWT.
- **Cuerpo de la solicitud:**
  ```json
  {
    "username": "usuario",
    "password": "contraseña"
  }
  ```
- **Respuesta exitosa:**
  ```json
  {
    "success": true,
    "message": "Login exitoso",
    "data": {
      "id": 1,
      "username": "usuario",
      "email": "correo@ejemplo.com",
      "accessToken": "token_jwt",
      "refreshToken": "refresh_token"
    }
  }
  ```

#### 3. **Refrescar Token**
- **URL:** `POST /api/auth/refresh`
- **Descripción:** Genera un nuevo token de acceso utilizando un token de refresco.
- **Cuerpo de la solicitud:**
  ```json
  {
    "refreshToken": "refresh_token"
  }
  ```
- **Respuesta exitosa:**
  ```json
  {
    "id": 1,
    "username": "usuario",
    "email": "correo@ejemplo.com",
    "accessToken": "nuevo_token_jwt",
    "refreshToken": "nuevo_refresh_token"
  }
  ```

#### 4. **Cerrar Sesión**
- **URL:** `POST /api/auth/logout`
- **Descripción:** Invalida el token de refresco.
- **Cuerpo de la solicitud:**
  ```json
  {
    "refreshToken": "refresh_token"
  }
  ```
- **Respuesta exitosa:**
  ```json
  {
    "message": "logged out"
  }
  ```

#### 5. **Obtener Información del Usuario**
- **URL:** `GET /api/auth/me`
- **Descripción:** Devuelve los datos del usuario autenticado.
- **Encabezado:**
  ```
  Authorization: Bearer token_jwt
  ```
- **Respuesta exitosa:**
  ```json
  {
    "id": 1,
    "username": "usuario",
    "email": "correo@ejemplo.com",
    "enabled": true,
    "failed_attempts": 0,
    "locked_until": null,
    "two_factor_enabled": false,
    "created_at": "2023-10-01T12:00:00Z",
    "updated_at": "2023-10-01T12:00:00Z"
  }
  ```

---

## Notas Adicionales

- **Seguridad:** Los tokens JWT están firmados con claves RSA. Asegúrate de configurar correctamente las claves pública y privada en el archivo `.env`.
- **Manejo de Errores:** La API devuelve mensajes claros en caso de errores de validación o autenticación.
- **Migraciones:** Flyway se encarga de gestionar las migraciones de la base de datos. Los scripts están ubicados en `src/main/resources/db/migration`.

---

## Recursos

- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de JWT](https://jwt.io/introduction/)
- [Documentación de Flyway](https://flywaydb.org/documentation/)