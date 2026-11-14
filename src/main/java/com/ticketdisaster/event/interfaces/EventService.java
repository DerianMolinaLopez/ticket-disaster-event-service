package com.ticketdisaster.event.interfaces;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.models.EventEntity;

public interface EventService {

    EventEntity createSeatsForEvent(CreateEventAndSeatsRequest entity);

    void retentionSeatsList(HoldSeatsRequest listOfRetentionSeats);

}
