package com.ticketdisaster.event.mapper;

import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.SeatResponse;
import com.ticketdisaster.event.enums.StatusSeat;
import com.ticketdisaster.event.models.SeatEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-14T17:38:11-0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.5 (Red Hat, Inc.)"
)
@Component
public class SeatMapperImpl implements SeatMapper {

    @Override
    public void applyHold(HoldSeatsRequest request, SeatEntity seat) {
        if ( request == null ) {
            return;
        }

        seat.setHeldBy( request.getBookingId() );
    }

    @Override
    public SeatResponse toResponse(SeatEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String section = null;
        Integer row = null;
        Integer number = null;
        BigDecimal price = null;
        StatusSeat status = null;
        UUID heldBy = null;
        Instant heldUntil = null;

        id = entity.getId();
        section = entity.getSection();
        row = entity.getRow();
        number = entity.getNumber();
        price = entity.getPrice();
        status = entity.getStatus();
        heldBy = entity.getHeldBy();
        heldUntil = entity.getHeldUntil();

        SeatResponse seatResponse = new SeatResponse( id, section, row, number, price, status, heldBy, heldUntil );

        return seatResponse;
    }
}
