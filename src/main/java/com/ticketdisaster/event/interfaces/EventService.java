package com.ticketdisaster.event.interfaces;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.models.EventEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EventService {

    EventEntity createSeatsForEvent(CreateEventAndSeatsRequest entity);

    Response<SeatsHeldListRequest> retentionSeatsList(HoldSeatsRequest listOfRetentionSeats);

    Response<List<EventEntity>>  getEventsPagination(Integer offset, Integer limit);

}
