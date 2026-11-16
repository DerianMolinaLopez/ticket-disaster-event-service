package com.ticketdisaster.event.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReleaseSeatsHeldRequest {
     @NotNull(message = "El campo eventId es necesario para esta operacion")
     private UUID eventId;
     @NotNull(message = "La lista de acientos debe existir para seguir con la operacion")
     @NotEmpty(message = "La lista debe de venir con datos")
     private List<UUID> listOfSeats;
}
