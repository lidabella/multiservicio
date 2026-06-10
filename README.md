# Sistema Web de Gestión de Atención Multiservicio

Proyecto de habilitación — Backend Spring Boot + Frontend React + MySQL.

## Estructura del repositorio

```
multiservicio/
├── src/                    # Backend Spring Boot
├── frontend/               # Frontend React (Vite)
├── pom.xml
└── README.md
```

## Requisitos

- Java 21
- Maven 3.9+
- MySQL 8+
- Node.js 18+ (para el frontend)

## Base de datos

1. Crear la base de datos `multiservicio_db` en MySQL
2. Importar el script SQL con tablas y datos iniciales (phpMyAdmin o consola)
3. Configurar credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/multiservicio_db
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

## Ejecutar el backend

### NetBeans
1. Abrir el proyecto Maven
2. Run en `MultiservicioApplication.java`

### Terminal
```bash
mvn spring-boot:run
```

El backend arranca en **http://localhost:8080**

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | 1234 | ADMIN |
| operador | 1234 | OPERADOR |
| recepcion | 1234 | RECEPCION |

## Documentación API (Swagger)

**http://localhost:8080/swagger-ui.html**

Autenticación: botón **Authorize** → pegar token JWT → `Bearer <token>`

## Endpoints principales

| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| POST | `/api/auth/login` | Login (devuelve JWT) | Público |
| GET | `/api/servicios` | Listar servicios | Autenticado |
| POST | `/api/servicios` | Crear servicio | ADMIN |
| GET | `/api/ventanillas` | Listar ventanillas | Autenticado |
| POST | `/api/ventanillas` | Crear ventanilla | ADMIN |
| GET | `/api/prioridades` | Listar prioridades | Autenticado |
| POST | `/api/turnos` | Crear turno | RECEPCION |
| GET | `/api/turnos/pendientes` | Turnos pendientes | RECEPCION/OPERADOR |
| POST | `/api/turnos/llamar` | Llamar siguiente turno | OPERADOR |
| PATCH | `/api/turnos/{id}/iniciar` | Iniciar atención | OPERADOR |
| PATCH | `/api/turnos/{id}/finalizar` | Finalizar atención | OPERADOR |
| PATCH | `/api/turnos/{id}/cancelar` | Cancelar turno | RECEPCION/OPERADOR |
| PATCH | `/api/turnos/{id}/no-presentado` | No presentado | OPERADOR |
| GET | `/api/turnos/{id}/historial` | Auditoría del turno | Autenticado |
| GET | `/api/dashboard` | Indicadores reales | ADMIN |

## Reglas de negocio (llamado de turnos)

Implementadas en `TurnoService.java`:

1. **Correlativo por día y servicio:** `RAD-20260610-001`
2. **Prioridad:** mayor peso primero
3. **FIFO:** más antiguo primero (mismo peso)
4. **Aging:** turno regular que supera `tiempo_maximo_espera` escala posición
5. **Fairness:** cada 4 llamados, máximo 3 prioritarios; al menos 1 regular si hay pendientes

## Ejecutar el frontend

Ver [frontend/README.md](frontend/README.md)

```bash
cd frontend
npm install
npm run dev
```

Frontend en **http://localhost:3000**

## Tecnologías

- Spring Boot 3.5, Spring Security, JWT, Spring Data JPA
- MySQL, Lombok, Swagger/OpenAPI
- React 18, Vite, React Router

## Autor

Proyecto de habilitación — Gestión de Atención Multiservicio
