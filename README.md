# 🏢 Proyecto N-Capas - Backend

Backend de la plataforma web de gestión y renta de propiedades, reservas, contratos de arrendamiento, pagos integrados con Stripe, tickets de mantenimiento y analíticas. Desarrollado bajo una arquitectura robusta y escalable en capas.

---

## 🚀 Tecnologías y Arquitectura

* **Framework Principal:** Spring Boot 4.0 (Java 21)
* **Base de Datos:** PostgreSQL con Spring Data JPA (Hibernate)
* **Seguridad:** Spring Security con Autenticación Stateless basada en tokens JWT
* **Pagos Integrados:** Stripe Java SDK
* **Almacenamiento de Archivos:** Compatible con AWS S3 / Oracle Object Storage (para carga de fotos y documentos KYC)
* **Documentación:** Springdoc OpenAPI (Swagger UI)
* **Gestor de Dependencias:** Gradle

---

## 📖 Documentación de la API (Swagger / OpenAPI)

El proyecto cuenta con una documentación viva e interactiva que expone todas las rutas y especificaciones de la API.

### Acceso a la Documentación
* **Swagger UI (Interactiva):** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI Spec (JSON crudo):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Autenticación en Swagger
Muchos de los endpoints (ej. gestión de contratos, reservas, reportes de mantenimiento) requieren un token de autenticación. Para probarlos desde la web:
1. Inicia sesión en el endpoint de autenticación `/api/auth/login` con tus credenciales.
2. Copia el valor del token JWT retornado en la respuesta.
3. En la parte superior derecha de Swagger UI, haz clic en el botón **Authorize** (con el candado).
4. Pega el token en el campo de texto y haz clic en **Authorize**.

---



## 🛠️ Cómo Ejecutar el Proyecto Localmente

### Requisitos Previos
- **Java 21** o superior instalado.
- Base de datos **PostgreSQL** corriendo y accesible.

### Pasos
1. Clona el repositorio y ubícate en la carpeta raíz del backend.
2. Configura las variables de entorno requeridas en tu sistema o mediante un archivo `.env` / configuración del IDE:
   * `DATABASE_URL`: URL de conexión a tu BD PostgreSQL.
   * `DB_USERNAME` y `DB_PASSWORD`: Credenciales de la BD.
   * `STRIPE_SECRET_KEY`: Tu llave secreta de pruebas de Stripe.
   * `JWT_SECRET`: Firma para la creación y validación de tokens JWT.
3. Ejecuta el servidor de desarrollo:
   ```bash
   ./gradlew bootRun
   ```
