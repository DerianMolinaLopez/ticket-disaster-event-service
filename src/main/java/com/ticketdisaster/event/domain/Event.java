package com.ticketdisaster.event.domain;

import java.time.Instant;
import java.util.UUID;

import com.ticketdisaster.event.enums.StatusEvent;

public class Event {

    private UUID id;
    private String name;
    private String venue;
    private Instant startsAt;
    private StatusEvent status;

    public Event(){
     this.id = null;
        this.name="";
        this.venue="";
        this.startsAt=null;
        this.status=StatusEvent.COMING_SOON;
    }
    public Event(UUID id,String name,String venue,Instant startsAt){
        this.id = null;
        this.name=name;
        this.venue=venue;
        this.startsAt=startsAt;
        this.status=StatusEvent.COMING_SOON;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public StatusEvent getStatus() {
        return status;
    }

    public void setStatus(StatusEvent status) {
        this.status = status;
    }


    

}
/*
 *
 * event-service

 Fase 1 — Cimientos. Responsable de los eventos, los recintos y el estado de los asientos. Este documento describe solo el contrato de este servicio: qué entra, qué sale y qué reglas aplica. Quién lo consume (el api-gateway, booking-service u otro) no es asunto de event-service: el servicio no sabe ni le importa quién hay al otro lado de la petición.

 1. Responsabilidad

 Mantener el catálogo de eventos y sus asientos.
 Garantizar que un asiento nunca se vende ni se retiene dos veces, incluso con peticiones concurrentes.
 Persistencia propia en PostgreSQL. Nadie fuera de este servicio toca sus tablas.
 2. Modelo de dominio

 Event

 Campo	Tipo	Reglas
 id	UUID	Generado por el servidor
 name	String	Obligatorio, 3–150 caracteres
 venue	String	Obligatorio
 startsAt	Instant	Obligatorio y en el futuro
 status	enum	DRAFT, ON_SALE, CANCELLED

 */
