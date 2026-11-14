# event-service

> Fase 1 — Cimientos. Responsable de los eventos, los recintos y el estado de los asientos.
> Este documento describe **solo el contrato de este servicio**: qué entra, qué sale y qué
> reglas aplica. Quién lo consume (el `api-gateway`, `booking-service` u otro) no es asunto
> de `event-service`: el servicio no sabe ni le importa quién hay al otro lado de la petición.

## 1. Responsabilidad

- Mantener el catálogo de eventos y sus asientos.
- Garantizar que un asiento nunca se vende ni se retiene dos veces, incluso con peticiones
  concurrentes.
- Persistencia propia en PostgreSQL. Nadie fuera de este servicio toca sus tablas.

## 2. Modelo de dominio

### Event

| Campo | Tipo | Reglas |
|---|---|---|
| `id` | UUID | Generado por el servidor |
| `name` | String | Obligatorio, 3–150 caracteres |
| `venue` | String | Obligatorio |
| `startsAt` | `Instant` | Obligatorio y en el futuro |
| `status` | enum | `DRAFT`, `ON_SALE`, `CANCELLED` |

### Seat

| Campo | Tipo | Reglas |
|---|---|---|
| `id` | UUID | Generado por el servidor |
| `eventId` | UUID | FK a `Event` |
| `section` | String | Por ejemplo `"A"`, `"VIP"` |
| `row` | int | ≥ 1 |
| `number` | int | ≥ 1 |
| `price` | `BigDecimal` | > 0, dos decimales. Nunca `double` para dinero |
| `status` | enum | `AVAILABLE`, `HELD`, `SOLD` |
| `heldBy` | UUID (nullable) | Id de la reserva que retiene el asiento |
| `heldUntil` | `Instant` (nullable) | Cuándo caduca la retención |

- La combinación `(eventId, section, row, number)` es **única** a nivel de BD.
- El esquema se crea y evoluciona con migraciones Flyway (`V1__init.sql`, …). Nunca `ddl-auto=update`.

## 3. Entradas y salidas

Todas las rutas públicas viven bajo `/api/v1/event`. Las internas, bajo `/internal/seats`
(quien las exponga o las oculte hacia fuera no es responsabilidad de este servicio).

### 3.1 Crear un evento con sus asientos

`POST /api/v1/event`

**Entrada**

```json
{
    "name": "Concierto de prueba",
    "venue": "Sala Desastre",
    "startsAt": "2027-03-01T21:00:00Z",
    "layout": [
        { "section": "VIP", "rows": 2, "seatsPerRow": 10, "price": 120.0 },
        { "section": "PISTA", "rows": 10, "seatsPerRow": 30, "price": 45.5 }
    ]
}
```

**Salida:** `200 OK` con el evento creado (incluye `id` y `status` inicial) y todos sus
asientos generados a partir del `layout`.

**Reglas**
- Crear el evento y generar todos sus asientos ocurre en **una única transacción**: o se
  crea todo, o no se crea nada.
- `name`, `venue` y `startsAt` obligatorios; `startsAt` debe ser una fecha futura.
- Cada elemento de `layout` genera `rows * seatsPerRow` asientos correlativos (fila 1..rows,
  número 1..seatsPerRow) con el `price` indicado.
- El evento nace en `DRAFT`.

### 3.2 Listar eventos

`GET /api/v1/event` (paginado)

**Salida:** lista de eventos en estado `ON_SALE` únicamente.

### 3.3 Detalle de un evento

`GET /api/v1/event/{id}`

**Salida:** `200` con el evento, o `404` si no existe.

### 3.4 Cambiar el estado de un evento

`PATCH /api/v1/event/{id}/status`

**Entrada:** el nuevo estado (`ON_SALE` o `CANCELLED`).

**Reglas**
- Transiciones permitidas: `DRAFT → ON_SALE`, cualquier estado `→ CANCELLED`.
- Cualquier otra transición (por ejemplo `CANCELLED → ON_SALE`) devuelve `409 Conflict`.

### 3.5 Listar asientos de un evento

`GET /api/v1/event/{id}/seats?status=AVAILABLE`

**Salida:** asientos del evento, filtrables por `status` (`AVAILABLE`, `HELD`, `SOLD`).

### 3.6 Retener asientos

`POST /internal/seats/hold`

**Entrada**

```json
{ "bookingId": "…uuid…", "eventId": "…uuid…", "seatIds": ["…", "…"], "holdMinutes": 10 }
```

**Salida:** `200` con los asientos retenidos (ahora en `HELD`, con `heldBy` = `bookingId` y
`heldUntil` calculado), o `409` indicando qué asientos fallaron.

**Reglas**
- `seatIds`: entre 1 y 6 asientos. Más de 6 → `400`.
- `holdMinutes`: entre 1 y 15.
- Todo o nada: si **uno** de los asientos no está `AVAILABLE`, no se retiene ninguno.
- Solo se pueden retener asientos de un evento en estado `ON_SALE`.
- Si N peticiones concurrentes intentan retener el mismo asiento, **exactamente una** tiene
  éxito; el resto recibe `409`. Esta garantía se verifica con un test de integración que
  lanza al menos 50 peticiones simultáneas sobre el mismo asiento (Testcontainers + Postgres
  real; virtual threads + `CountDownLatch` para que todas salgan a la vez).

### 3.7 Liberar asientos

`POST /internal/seats/release`

**Entrada:** `bookingId` (y, según se decida, la lista de `seatIds` o "todos los de esa reserva").

**Salida:** `200` con los asientos liberados (vuelven a `AVAILABLE`, se limpian `heldBy` y
`heldUntil`).

**Reglas**
- Solo actúa sobre asientos cuyo `heldBy` coincida con el `bookingId` recibido.
- Debe ser **idempotente**: llamarlo dos veces sobre la misma reserva no es un error.

### 3.8 Confirmar asientos

`POST /internal/seats/confirm`

**Entrada:** `bookingId` (y la lista de `seatIds` de esa reserva).

**Salida:** `200` con los asientos confirmados (pasan de `HELD` a `SOLD`).

**Reglas**
- Solo actúa sobre asientos cuyo `heldBy` coincida con el `bookingId` recibido.
- Debe ser **idempotente**: llamarlo dos veces no es un error.

### 3.9 Liberación automática por caducidad

Un proceso programado (`@Scheduled`) libera los asientos cuyo `heldUntil` ya pasó, sin
necesidad de que nadie lo pida: pasan de `HELD` a `AVAILABLE` y se limpian `heldBy`/`heldUntil`.

## 4. Errores y validación

- Toda respuesta de error es un `ProblemDetail` (RFC 9457): `type`, `title`, `status`,
  `detail` y, si aplica, la lista de campos inválidos.
- Validación de entrada con Bean Validation (`@Valid`, `@NotBlank`, `@Future`, etc.) sobre
  los DTOs de entrada. Las entidades JPA nunca se exponen por la API: los DTOs son `records`.
- Un único `@RestControllerAdvice` para todo el servicio. Sin `try/catch` en los controladores.

## 5. Configuración

- Puerto: `8081`.
- Base de datos: PostgreSQL propia (`events`), ver
  [`application.yml`](src/main/resources/application.yml).
- Migraciones: `classpath:db/migration` (esquema) y `classpath:db/seed` (datos de desarrollo;
  no debe ir a producción).
- `spring.jpa.hibernate.ddl-auto=validate`: Flyway crea el esquema, Hibernate solo comprueba
  que las entidades coincidan.
- Virtual threads activados (`spring.threads.virtual.enabled=true`).

## 6. Fuera del alcance de este servicio

Lo que hace cualquier otro componente con esta información no es responsabilidad de
`event-service`:

- Si lo llama el `api-gateway`, `booking-service` o cualquier otro cliente, por qué ruta y
  con qué autenticación.
- Enrutado, prefijos de URL, JWT o `X-Request-Id`: eso vive en el gateway.
- Cómo `booking-service` usa el resultado de `hold`/`release`/`confirm` para su propio
  modelo de reserva.

`event-service` solo garantiza su contrato de entrada/salida y sus invariantes de datos.
