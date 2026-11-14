package com.ticketdisaster.event.dto.request;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HoldSeatsRequest {

    @NotNull
    private UUID bookingId;

    @NotNull
    private UUID eventId;

    @NotEmpty
    @Size(max = 6)
    private Set<@NotNull UUID> seatIds;

    @NotNull
    @Min(1)
    @Max(15)
    private Integer holdMinutes;
}