# Proyecto de Login con Spring Boot y JWT

Este proyecto implementa un sistema de autenticación y registro de usuarios utilizando Spring Boot, JWT y MySQL. Incluye endpoints para registrar usuarios, iniciar sesión, y obtener información del usuario autenticado.

---

## Configuración del Proyecto

### Prerrequisitos

- **Java 21** o superior
- **Maven** (incluido en el proyecto con `mvnw`)
- **MySQL** como base de datos
- **Postman** o cualquier cliente HTTP para probar los endpoints

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto y configura las siguientes variables:

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
   git clone https://github.com/pipook/login-wom.git
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

## Configuración de Seguridad

El proyecto utiliza Spring Security para proteger los endpoints. A continuación, se describen las rutas públicas y protegidas:

### **Rutas Públicas**

Las siguientes rutas son accesibles sin autenticación:

- **Swagger y documentación de la API:**
  - `/v3/api-docs/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
  - `/api/docs/**`
  - `/api/swagger-ui/**`
  - `/swagger-resources/**`
  - `/webjars/**`
- **Autenticación:**
  - `/api/auth/**` (endpoints para login, registro, etc.)

### **Rutas Protegidas**

Cualquier otra ruta requiere autenticación mediante un token JWT válido. El token debe incluirse en el encabezado `Authorization` con el formato:

```
Authorization: Bearer <token_jwt>
```

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
    "success": true,
    "message": "Token refrescado exitosamente",
    "data": {
      "id": 1,
      "username": "usuario",
      "email": "correo@ejemplo.com",
      "accessToken": "nuevo_token_jwt",
      "refreshToken": "nuevo_refresh_token"
    }
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
    "success": true,
    "message": "Sesión cerrada exitosamente",
    "data": null
  }
  ```

#### 5. **Obtener Información del Usuario**
- **URL:** `GET /api/auth/me`
- **Descripción:** Devuelve los datos del usuario autenticado.
- **Encabezado:**
  ```
  Authorization: Bearer <token_jwt>
  ```
- **Respuesta exitosa esperada:**
  ```json
  {
    "success": true,
    "message": "Información del usuario obtenida exitosamente",
    "data": {
      "id": 1,
      "enabled": true,
      "username": "usuario",
      "email": "correo@ejemplo.com",
      "created_at": "2023-10-01T12:00:00Z"
    }
  }
  ```

La estructura de salida está definida en la clase `ApiResponse` ubicada en el paquete `com.wom.login.dto`.

---

## Notas Adicionales

- **CORS:** El proyecto incluye una configuración global de CORS para permitir solicitudes desde el cliente. Asegúrate de configurar el dominio del cliente en `WebConfig`.
- **Seguridad:** Los tokens JWT están firmados con claves RSA. Asegúrate de configurar correctamente las claves pública y privada en el archivo `.env`.
- **Manejo de Errores:** La API devuelve mensajes claros en caso de errores de validación o autenticación.
- **Migraciones:** Flyway se encarga de gestionar las migraciones de la base de datos. Los scripts están ubicados en `src/main/resources/db/migration`.

---

## Recursos

- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de JWT](https://jwt.io/introduction/)
- [Documentación de Flyway](https://flywaydb.org/documentation/)