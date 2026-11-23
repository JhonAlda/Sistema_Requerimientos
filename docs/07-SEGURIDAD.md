# Seguridad

## Autenticación

El sistema usa sesiones HTTP con Spring Security:

1. `POST /api/auth/login` recibe usuario y contraseña.
2. Spring autentica mediante `UsuarioDetailsService`.
3. La contraseña se compara contra BCrypt.
4. Se crea un contexto de seguridad en la sesión HTTP.
5. `POST /api/auth/logout` invalida la sesión.
6. `GET /api/auth/csrf` emite/expone el token CSRF de la sesión.

## Contraseñas

- Se almacena `passwordHash`, no texto plano.
- La creación y actualización usan `PasswordEncoder` BCrypt.

## Autorización

- `ADMINISTRADOR`: usuarios y administración.
- `ALMACENERO`: despachos e inventario.
- `SOLICITANTE`: creación de requerimientos.
- `SUPERVISOR`: operaciones autorizadas combinadas.
- `SOLICITANTE` solo puede consultar y crear requerimientos en sus áreas asignadas.
- Reportes: administrador y supervisor.
- Catálogos mutables: administrador y supervisor.

## Backend

La autorización no depende del frontend. Está definida en `SecurityConfig` y también en anotaciones `@PreAuthorize` de controladores sensibles.

## CORS

La configuración permite orígenes locales con credenciales habilitadas. La política debe restringirse al dominio real antes de desplegar.

## CSRF

CSRF está activo mediante `CookieCsrfTokenRepository`. El token se entrega en `GET /api/auth/csrf`; el frontend envía su valor en `X-XSRF-TOKEN` y conserva cookies con `credentials: include`. Las escrituras sin token responden 403.

## CORS y sesiones

CORS actualmente está limitado a orígenes localhost para desarrollo. En despliegue hay que definir el origen exacto de la aplicación y servirla bajo HTTPS.

## Límites

- La autorización del menú frontend es solo UX; el control efectivo está en Spring Security y servicios.
- La desactivación de usuarios es lógica. Está prohibido desactivar o degradar al último administrador activo.
- No se permite modificar directamente un despacho finalizado; el flujo de corrección/auditoría aún no está implementado.
