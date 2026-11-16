package com.ticketdisaster.event.constants;

public class ApiConstants {

    private ApiConstants() {

    }

    public static final String API_EVENT = "/api/v1/event";

    // Base
    public static final String INTERNAL_SEATS = "/internal/seats";

    // Eventos
    public static final String EVENTS = "/events";
    public static final String EVENT = "/event/{id}";
    public static final String EVENT_SEATS = EVENTS + "/{id}/seats";

    // Seats internos (derivados de la base)
    public static final String SEATS_HOLD = INTERNAL_SEATS + "/hold";
    public static final String SEATS_RELEASE = INTERNAL_SEATS + "/release";
    public static final String SEATS_CONFIRM = INTERNAL_SEATS + "/confirm";

}
