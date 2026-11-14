-- =====================================================================
--  Datos de ambientación SOLO para desarrollo local.
--
--  Es una migración "repetible" (R__): Flyway la ejecuta después de las
--  versionadas y la vuelve a ejecutar si cambias este archivo.
--  Por eso todo es idempotente (ON CONFLICT DO NOTHING): ejecutarla
--  dos veces no duplica nada.
--
--  Los eventos tienen UUID fijos para poder usarlos en Postman/curl:
--
--  | UUID                                 | Evento                  | Estado      | Asientos                    |
--  |--------------------------------------|-------------------------|-------------|-----------------------------|
--  | 11111111-1111-1111-1111-111111111111 | Concierto de prueba     | ON_SALE     | 320 (VIP 20 + PISTA 300)    |
--  | 22222222-2222-2222-2222-222222222222 | Stand-up íntimo         | ON_SALE     | 8, con HELD, SOLD y caducado |
--  | 33333333-3333-3333-3333-333333333333 | Festival suspendido     | CANCELLED   | 40                          |
--  | 44444444-4444-4444-4444-444444444444 | Ópera de primavera      | COMING_SOON | 60                          |
--  | 55555555-5555-5555-5555-555555555555 | Final de liga           | ON_SALE     | 1 (para pruebas de concurrencia) |
-- =====================================================================

INSERT INTO events (id, name, venue, starts_at, status) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Concierto de prueba', 'Sala Desastre',      '2027-03-01T21:00:00Z', 'ON_SALE'),
    ('22222222-2222-2222-2222-222222222222', 'Stand-up íntimo',     'Bar El Chiste',      '2027-01-15T20:30:00Z', 'ON_SALE'),
    ('33333333-3333-3333-3333-333333333333', 'Festival suspendido', 'Parque Central',     '2027-06-10T16:00:00Z', 'CANCELLED'),
    ('44444444-4444-4444-4444-444444444444', 'Ópera de primavera',  'Teatro Municipal',   '2027-04-20T19:00:00Z', 'COMING_SOON'),
    ('55555555-5555-5555-5555-555555555555', 'Final de liga',       'Estadio Olímpico',   '2027-05-30T18:00:00Z', 'ON_SALE')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------
--  Asientos generados a partir de un "layout", igual que POST /events:
--  sección × filas × asientos por fila.
-- ---------------------------------------------------------------------
INSERT INTO seats (id, event_id, section, row, number, price, status)
SELECT gen_random_uuid(), l.event_id, l.section, r.row, n.number, l.price, 'AVAILABLE'
FROM (VALUES
        -- event_id                                section    filas  por fila  precio
        ('11111111-1111-1111-1111-111111111111'::uuid, 'VIP',      2,   10,   120.00),
        ('11111111-1111-1111-1111-111111111111'::uuid, 'PISTA',   10,   30,    45.50),
        ('22222222-2222-2222-2222-222222222222'::uuid, 'GENERAL',  2,    4,    25.00),
        ('33333333-3333-3333-3333-333333333333'::uuid, 'GENERAL',  4,   10,    60.00),
        ('44444444-4444-4444-4444-444444444444'::uuid, 'PLATEA',   3,   10,    80.00),
        ('44444444-4444-4444-4444-444444444444'::uuid, 'PALCO',    2,   15,   150.00),
        ('55555555-5555-5555-5555-555555555555'::uuid, 'TRIBUNA',  1,    1,    99.99)
     ) AS l(event_id, section, rows, seats_per_row, price)
CROSS JOIN LATERAL generate_series(1, l.rows)          AS r(row)
CROSS JOIN LATERAL generate_series(1, l.seats_per_row) AS n(number)
ON CONFLICT (event_id, section, row, number) DO NOTHING;

-- ---------------------------------------------------------------------
--  Stand-up íntimo: asientos en distintos estados para probar
--  hold / release / confirm y el job de caducidad (R-2.10).
-- ---------------------------------------------------------------------

-- Fila 1, asientos 1-2: retenidos por una reserva activa (caducan dentro de 10 min).
UPDATE seats
SET status     = 'HELD',
    held_by    = 'aaaaaaaa-0000-0000-0000-000000000001',
    held_until = now() + INTERVAL '10 minutes'
WHERE event_id = '22222222-2222-2222-2222-222222222222'
  AND section = 'GENERAL' AND row = 1 AND number IN (1, 2)
  AND status = 'AVAILABLE';

-- Fila 1, asiento 3: retención YA caducada; el @Scheduled debería liberarla.
UPDATE seats
SET status     = 'HELD',
    held_by    = 'aaaaaaaa-0000-0000-0000-000000000002',
    held_until = now() - INTERVAL '5 minutes'
WHERE event_id = '22222222-2222-2222-2222-222222222222'
  AND section = 'GENERAL' AND row = 1 AND number = 3
  AND status = 'AVAILABLE';

-- Fila 2, asientos 1-2: ya vendidos.
UPDATE seats
SET status  = 'SOLD',
    held_by = 'aaaaaaaa-0000-0000-0000-000000000003'
WHERE event_id = '22222222-2222-2222-2222-222222222222'
  AND section = 'GENERAL' AND row = 2 AND number IN (1, 2)
  AND status = 'AVAILABLE';
