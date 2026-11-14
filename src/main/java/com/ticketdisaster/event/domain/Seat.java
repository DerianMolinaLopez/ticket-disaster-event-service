package com.ticketdisaster.event.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Seat {

    private UUID id;
    private UUID eventId;
    private String section;
    private Integer row;
    private Integer number;
    private BigDecimal price;
    private UUID heldBy;
    private Instant heldUntill;

    public Seat() {
    }


    public Seat(UUID id, UUID eventId, String section, Integer row, Integer number,
                BigDecimal price, UUID heldBy, Instant heldUntill) {
        this.id = id;
        this.eventId = eventId;
        this.section = section;
        this.row = row;
        this.number = number;
        this.price = price;
        this.heldBy = heldBy;
        this.heldUntill = heldUntill;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getHeldBy() {
        return heldBy;
    }

    public void setHeldBy(UUID heldBy) {
        this.heldBy = heldBy;
    }

    public Instant getHeldUntill() {
        return heldUntill;
    }

    public void setHeldUntill(Instant heldUntill) {
        this.heldUntill = heldUntill;
    }

    

}

/*Campo	Tipo	Reglas
id	UUID	Generado por el servidor
eventId	UUID	FK a Event
section	String	Por ejemplo "A", "VIP"
row	int	≥ 1
number	int	≥ 1
price	BigDecimal	> 0, dos decimales. Nunca double para dinero
status	enum	AVAILABLE, HELD, SOLD
heldBy	UUID (nullable)	Id de la reserva que retiene el asiento
heldUntil	Instant (nullable)	Cuándo caduca la retención*/
