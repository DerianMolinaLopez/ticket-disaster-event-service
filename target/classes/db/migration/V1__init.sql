-- =====================================================================
--  V1: esquema inicial de event-service
--  Una vez aplicada, NUNCA se edita: los cambios van en V2__..., V3__...
-- =====================================================================

CREATE TABLE events (
    id         UUID         PRIMARY KEY,
    name       VARCHAR(150) NOT NULL CHECK (char_length(name) >= 3),
    venue      VARCHAR(255) NOT NULL,
    starts_at  TIMESTAMPTZ  NOT NULL,
    status     VARCHAR(20)  NOT NULL
);

CREATE TABLE seats (
    id          UUID          PRIMARY KEY,
    event_id    UUID          NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    section     VARCHAR(50)   NOT NULL,
    row         INTEGER       NOT NULL CHECK (row >= 1),
    number      INTEGER       NOT NULL CHECK (number >= 1),
    price       NUMERIC(10,2) NOT NULL CHECK (price > 0),
    status      VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE'
                              CHECK (status IN ('AVAILABLE', 'HELD', 'SOLD')),
    held_by     UUID,
    held_until  TIMESTAMPTZ,

    -- R-2.1: un asiento físico existe una sola vez por evento.
    CONSTRAINT uq_seats_event_section_row_number UNIQUE (event_id, section, row, number),

    -- Un asiento HELD siempre sabe quién lo retiene y hasta cuándo.
    CONSTRAINT ck_seats_held_has_owner
        CHECK (status <> 'HELD' OR (held_by IS NOT NULL AND held_until IS NOT NULL))
);

-- GET /events/{id}/seats?status=AVAILABLE
CREATE INDEX ix_seats_event_status ON seats (event_id, status);

-- R-2.10: el job que libera retenciones caducadas solo mira los HELD.
CREATE INDEX ix_seats_held_until ON seats (held_until) WHERE status = 'HELD';

-- release / confirm buscan por bookingId (R-2.9).
CREATE INDEX ix_seats_held_by ON seats (held_by) WHERE held_by IS NOT NULL;
