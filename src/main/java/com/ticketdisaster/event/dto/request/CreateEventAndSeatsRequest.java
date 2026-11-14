package com.ticketdisaster.event.dto.request;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEventAndSeatsRequest {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String venue;
    @NotNull
    @NotBlank
    private Instant startsAt;
    private List<LayoutRequest> layout;

}
