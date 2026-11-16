package com.ticketdisaster.event.controllers;

import com.ticketdisaster.event.dto.request.ReleaseSeatsHeldRequest;
import com.ticketdisaster.event.models.EventEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ticketdisaster.event.constants.ApiConstants;
import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.EventResponse;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.interfaces.EventService;
import com.ticketdisaster.event.mapper.EventMapper;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_EVENT)
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping()
    public ResponseEntity<EventResponse> createSeats(@RequestBody CreateEventAndSeatsRequest entity) {

        return ResponseEntity.ok(eventMapper.toResponse(this.eventService.createSeatsForEvent(entity)));
    }

    @GetMapping(ApiConstants.EVENTS)
    public Response<List<EventEntity>>  getEventsPagination(@RequestParam(defaultValue = "0") String offset, @RequestParam(defaultValue = "10") String limit) {
        return this.eventService.getEventsPagination(Integer.valueOf(offset),Integer.valueOf(limit));

    }
    @GetMapping(ApiConstants.EVENT)
    public Response<EventEntity> getEventDetail(@PathVariable UUID id){

        return this.eventService.getEventDetail(id);
    }


    @GetMapping(ApiConstants.EVENT_SEATS)
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
                 
    @PostMapping(ApiConstants.SEATS_HOLD)
    public Response<SeatsHeldListRequest> retentionSeatsList(@RequestBody @Valid HoldSeatsRequest entity) {

        return this.eventService.retentionSeatsList(entity);
    }

    @PostMapping(ApiConstants.SEATS_RELEASE)
    public Response<String>  releaseSeats(@RequestBody @Valid ReleaseSeatsHeldRequest seatsHeld) {
        return this.eventService.releaseSeats(seatsHeld);
    }

    @PostMapping(ApiConstants.SEATS_CONFIRM)
    public Response<String> confirmReservation(@RequestBody @Valid ReleaseSeatsHeldRequest seatsHeld) {

        return this.eventService.confirmReservation(seatsHeld);
    }

}
