package com.ticketdisaster.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.SeatResponse;
import com.ticketdisaster.event.models.SeatEntity;

@Mapper(componentModel = "spring")
public interface SeatMapper {


    @Mapping(target = "heldBy", source = "bookingId")
    @Mapping(target = "heldUntil", ignore = true)
    @Mapping(target = "status", ignore = true)
    void applyHold(HoldSeatsRequest request, @MappingTarget SeatEntity seat);

    // id, section, row, number, price, status, heldBy y heldUntil convergen (mismo
    // nombre y tipo). No se mapea "event": SeatResponse no lo tiene a propósito,
    // para no reintroducir la referencia circular Event <-> Seat en el JSON.
    SeatResponse toResponse(SeatEntity entity);
}
