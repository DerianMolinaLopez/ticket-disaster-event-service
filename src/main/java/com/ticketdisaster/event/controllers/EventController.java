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
import com.ticketdisaster.event.interfaces.EventService;
import com.ticketdisaster.event.models.EventEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConstants.API_EVENT)
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    public ResponseEntity<EventEntity> createSeats(@RequestBody CreateEventAndSeatsRequest entity) {

        return ResponseEntity.ok(this.eventService.createSeatsForEvent(entity));
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
/*
 * 
 * | Método | Ruta | Descripción |
 * | ------ | ------------------------- |
 * --------------------------------------------------- |
 * | `POST` | `/internal/seats/hold` | Retiene una lista de asientos para una
 * reserva |
 * | `POST` | `/internal/seats/release` | Libera los asientos de una reserva |
 * | `POST` | `/internal/seats/confirm` | Pasa los asientos de una reserva de
 * `HELD` a `SOLD` |
 */