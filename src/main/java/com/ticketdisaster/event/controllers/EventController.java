package com.ticketdisaster.event.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticketdisaster.event.constants.ApiConstants;
import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.response.EventResponse;
import com.ticketdisaster.event.interfaces.EventService;
import com.ticketdisaster.event.mapper.EventMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConstants.API_EVENT)
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }
/*
 * TODO:  como este metodo hace la creacion de eventos,
 * deberia de colocarle un token que solamente
 * pueda crearse despues de crear un negocio o
 * firmar un contrato de vento
 */
    @PostMapping()
    public ResponseEntity<EventResponse> createSeats(@RequestBody CreateEventAndSeatsRequest entity) {

        return ResponseEntity.ok(eventMapper.toResponse(this.eventService.createSeatsForEvent(entity)));
    }

    @GetMapping(ApiConstants.EVENT_SEATS)
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PostMapping(ApiConstants.SEATS_HOLD)
    public String retentionSeatsList(@RequestBody @Valid HoldSeatsRequest entity) {

        return "";
    }

    @PostMapping(ApiConstants.SEATS_RELEASE)
    public String releaseSeats(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

    @PostMapping(ApiConstants.SEATS_CONFIRM)
    public String changeStatusReservation(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

}

