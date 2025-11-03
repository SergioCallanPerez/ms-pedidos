# Microservicios- Productos y servicios

Proyecto que implementa dos microservicios para la gestión de pedidos utilizando **Springboot Webflux** y **R2dbc** para una conexión reactiva con
bases de datos y utilizando config-server para los perfiles de los microservicios.

---

## 1. Instalación

### Requisitos

- Java 21
- Gradle
- PostgreSQL

### Compilación del proyecto

Iniciar primero compilando ms-config-server.
- Pedidos: Ejecutar main.java

---

## 2. Base de datos

### Bases de datos

- CREATE DATABASE db_pedidos_dev
- CREATE DATABASE db_pedidos_prd
- CREATE DATABASE db_pedidos_qa

### Tablas
En cada base de datos, crear las tabla pedidos y detalle_pedidos:

- CREATE TABLE pedidos (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  cliente VARCHAR(255),
  fecha TIMESTAMP,
  total NUMERIC(10, 2),
  estado VARCHAR(50)
  );
- CREATE TABLE detalle_pedidos (
  id BIGSERIAL PRIMARY KEY,
  pedido_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INTEGER NOT NULL,
  precio_unitario NUMERIC(10, 2) NOT NULL,
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
  );

---


## 3. Cambios en el código
Crear y/o cambiar el .env de cada microservicio para la nueva url de las bases de datos; ejemplo:
- DB_URL_PEDIDOS_DEV=r2dbc:postgresql://localhost:5432/db_pedidos_dev
- DB_URL_PEDIDOS_PRD=r2dbc:postgresql://localhost:5432/db_pedidos_prd
- DB_URL_PEDIDOS_QA=r2dbc:postgresql://localhost:5432/db_pedidos_qa
- DB_USERNAME=postgres
- DB_PASSWORD=admin123

---

## 4. Endpoints y sus usos

URL base: /api/pedidos
### Crear pedido: POST /
Request:

{
"cliente": "Juan Pérez",
"fecha": "2025-11-02T15:30:00",
"estado": "PENDIENTE",
"detalles": [
{
"productoId": 6,
"cantidad": 2
}
]
}

Response (201):

{
"id": 7,
"cliente": "Juan Pérez",
"fecha": [
2025,11,2,22,41,13,940137500
],
"total": 2500.0,
"estado": "PENDIENTE",
"detalles": [
{
"productoId": 7,
"cantidad": 2
}
]
}

### Ver pedidos: GET /

Response:

[
{
"id": 1,
"cliente": "Juan Pérez",
"fecha": [
2025,11,2,16,1,27,203965000
],
"total": 2425.0,
"estado": "PROCESADO"
},
{
"id": 2,
"cliente": "Juan Pérez",
"fecha": [
2025,11,2,16,46,2,919554000
],
"total": 3750.0,
"estado": "PROCESADO"
}
]

### Ver 1 pedido en específico: GET /{id}

Response:

[
{
"id": 1,
"cliente": "Juan Pérez",
"fecha": [
2025,11,2,16,1,27,203965000
],
"total": 2425.0,
"estado": "PROCESADO"
}
]

### Cambiar estado: /{id}/estado

Request:

{
"estado": "PROCESADO"
}

Response:

{
"id": 6,
"cliente": "Juan Pérez",
"fecha": [
2025,11,2,20,36,35,618002000
],
"total": 2500.0,
"estado": "PROCESADO"
}

### Borrar un pedido y los detalles atados: Delete /{id}

