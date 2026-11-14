package com.ticketdisaster.event.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ticketdisaster.event.enums.StatusSeat;

public record SeatResponse(
        UUID id,
        String section,
        Integer row,
        Integer number,
        BigDecimal price,
        StatusSeat status,
        UUID heldBy,
        Instant heldUntil) {
}
