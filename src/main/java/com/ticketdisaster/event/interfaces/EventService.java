package com.ticketdisaster.event.interfaces;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.models.EventEntity;

public interface EventService {

    EventEntity createSeatsForEvent(CreateEventAndSeatsRequest entity);

    Response <SeatsHeldListRequest> retentionSeatsList(HoldSeatsRequest listOfRetentionSeats);

}
