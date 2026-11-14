package com.ticketdisaster.event.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ticketdisaster.event.enums.StatusEvent;

public record EventResponse(
        UUID id,
        String name,
        String venue,
        Instant startsAt,
        StatusEvent status,
        List<SeatResponse> seats) {
}
