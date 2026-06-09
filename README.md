# MiniMarket Plus - Backend Seguro

Backend desarrollado con Spring Boot para el caso MiniMarket Plus. El proyecto incorpora autenticacion, autorizacion y control de acceso sobre endpoints REST.

## Descripcion general

El sistema implementa una API backend para la gestion de usuarios, productos, categorias, inventario, carrito y ventas. La capa de seguridad utiliza JWT y roles para restringir el acceso a funcionalidades privadas.

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- H2 Database
- JWT
- BCrypt

## Seguridad incorporada

- Autenticacion mediante token JWT.
- Autorizacion por roles.
- Proteccion de endpoints privados.
- Cifrado de contrasenas con BCrypt.
- Respuestas controladas para accesos no autenticados y accesos denegados.
- Validacion basica de patrones asociados a XSS y SQL Injection.
- Registro basico de eventos de seguridad.

## Roles del sistema

- ROLE_CLIENTE
- ROLE_EMPLEADO
- ROLE_ADMIN

## Endpoints principales

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | /auth/login | Autenticacion de usuario |
| GET | /public/hola | Endpoint publico de verificacion |
| GET | /api/usuarios | Gestion y consulta de usuarios |
| GET | /api/productos | Consulta de productos |
| POST | /api/categorias | Gestion de categorias |

## Datos iniciales

La aplicacion carga usuarios, roles, categorias y productos en una base de datos H2 en memoria.

## Usuarios configurados

| Usuario | Rol |
|---|---|
| cliente | ROLE_CLIENTE |
| empleado | ROLE_EMPLEADO |
| admin | ROLE_ADMIN |

## Estructura general

```text
src/main/java/com/minimarket
├── config
├── controller
├── entity
├── repository
├── security
└── service
```

## Alcance

Proyecto backend con autenticacion JWT, autorizacion por roles, control de acceso y pruebas automatizadas de seguridad.
