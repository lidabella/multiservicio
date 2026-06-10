# Multiservicio — Frontend React

Interfaz web que consume la API REST del backend Spring Boot.

## Requisitos

- Node.js 18 o superior
- Backend corriendo en `http://localhost:8080`

## Instalación

```bash
cd frontend
npm install
```

## Ejecutar en desarrollo

```bash
npm run dev
```

Abrir **http://localhost:3000**

El proxy de Vite redirige `/api/*` al backend en el puerto 8080.

## Usuarios de prueba

| Usuario | Contraseña | Acceso |
|---------|------------|--------|
| admin | 1234 | Dashboard, Administración, todo |
| recepcion | 1234 | Crear turnos, sala de espera |
| operador | 1234 | Llamar, iniciar, finalizar, cancelar |

## Pantallas

| Ruta | Pantalla | Rol |
|------|----------|-----|
| `/login` | Inicio de sesión con JWT | Todos |
| `/dashboard` | Indicadores (datos reales de BD) | ADMIN |
| `/admin` | CRUD servicios, ventanillas, prioridades | ADMIN |
| `/recepcion` | Crear turno + pendientes | RECEPCION |
| `/operador` | Llamar, iniciar, finalizar, cancelar, no presentado | OPERADOR |
| `/sala` | Sala de espera (pendientes + llamados) | Todos |
| `/historial` | Auditoría de un turno por ID | Todos |

## Build para producción

```bash
npm run build
```

Los archivos quedan en `frontend/dist/`.

## Estructura

```
frontend/src/
├── api/api.js          # Llamadas a la API REST
├── context/            # AuthContext (JWT en localStorage)
├── pages/              # Pantallas por rol
├── components/         # Layout con menú lateral
└── App.jsx             # Rutas protegidas por rol
```
