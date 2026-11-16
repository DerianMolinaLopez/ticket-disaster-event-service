package com.ticketdisaster.event.interfaces;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.request.ReleaseSeatsHeldRequest;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.models.EventEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventEntity createSeatsForEvent(CreateEventAndSeatsRequest entity);

    Response<SeatsHeldListRequest> retentionSeatsList(HoldSeatsRequest listOfRetentionSeats);

    Response<List<EventEntity>>  getEventsPagination(Integer offset, Integer limit);

    Response<EventEntity> getEventDetail(UUID id);

    Response<String> releaseSeats(ReleaseSeatsHeldRequest seats);

    Response<String> confirmReservation(ReleaseSeatsHeldRequest seatsHeld);

}
