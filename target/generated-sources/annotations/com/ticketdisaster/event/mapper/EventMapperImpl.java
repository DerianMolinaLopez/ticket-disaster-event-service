package com.ticketdisaster.event.mapper;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.response.EventResponse;
import com.ticketdisaster.event.dto.response.SeatResponse;
import com.ticketdisaster.event.enums.StatusEvent;
import com.ticketdisaster.event.models.EventEntity;
import com.ticketdisaster.event.models.SeatEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-16T10:40:52-0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2.1 (Eclipse Adoptium)"
)
@Component
public class EventMapperImpl implements EventMapper {

    private final SeatMapper seatMapper;

    @Autowired
    public EventMapperImpl(SeatMapper seatMapper) {

        this.seatMapper = seatMapper;
    }

    @Override
    public EventEntity toEntity(CreateEventAndSeatsRequest request) {
        if ( request == null ) {
            return null;
        }

        EventEntity eventEntity = new EventEntity();

        eventEntity.setName( request.getName() );
        eventEntity.setVenue( request.getVenue() );
        eventEntity.setStartsAt( request.getStartsAt() );

        return eventEntity;
    }

    @Override
    public EventResponse toResponse(EventEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String venue = null;
        Instant startsAt = null;
        StatusEvent status = null;
        List<SeatResponse> seats = null;

        id = entity.getId();
        name = entity.getName();
        venue = entity.getVenue();
        startsAt = entity.getStartsAt();
        status = entity.getStatus();
        seats = seatEntityListToSeatResponseList( entity.getSeats() );

        EventResponse eventResponse = new EventResponse( id, name, venue, startsAt, status, seats );

        return eventResponse;
    }

    protected List<SeatResponse> seatEntityListToSeatResponseList(List<SeatEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<SeatResponse> list1 = new ArrayList<SeatResponse>( list.size() );
        for ( SeatEntity seatEntity : list ) {
            list1.add( seatMapper.toResponse( seatEntity ) );
        }

        return list1;
    }
}
