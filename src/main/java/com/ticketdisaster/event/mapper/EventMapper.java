package com.ticketdisaster.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.response.EventResponse;
import com.ticketdisaster.event.models.EventEntity;

@Mapper(componentModel = "spring", uses = SeatMapper.class)
public interface EventMapper {

    // name, venue y startsAt convergen (mismo nombre y tipo en el DTO y en la entidad):
    // MapStruct los copia solo, sin necesidad de @Mapping explícito.
    @Mapping(target = "id", ignore = true) // lo genera la base de datos
    @Mapping(target = "status", ignore = true) // estado inicial: lo decide el service, no el DTO
    @Mapping(target = "seats", ignore = true) // layout -> seats no converge: 1 LayoutRequest genera N SeatEntity, es una expansión, no un mapeo de propiedad
    EventEntity toEntity(CreateEventAndSeatsRequest request);

    // id, name, venue, startsAt y status convergen directo. "seats" sí converge aquí
    // (List<SeatEntity> -> List<SeatResponse>, mismo significado); MapStruct usa
    // SeatMapper.toResponse(...) para cada elemento, que corta la referencia circular.
    EventResponse toResponse(EventEntity entity);
}
