package com.ticketdisaster.event.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.request.LayoutRequest;
import com.ticketdisaster.event.enums.StatusEvent;
import com.ticketdisaster.event.enums.StatusSeat;
import com.ticketdisaster.event.exception.TicketDisasterException;
import com.ticketdisaster.event.interfaces.EventService;
import com.ticketdisaster.event.interfaces.jpa.EventRepository;
import com.ticketdisaster.event.interfaces.jpa.SeatRepository;
import com.ticketdisaster.event.mapper.EventMapper;
import com.ticketdisaster.event.models.EventEntity;
import com.ticketdisaster.event.models.SeatEntity;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventServiceimpl implements EventService {

    private final EventRepository eventRepository;

    private final SeatRepository seatRepository;

    private final EventMapper eventMapper;

    public EventServiceimpl(EventRepository eventRepository, SeatRepository seatRepository,EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventMapper=eventMapper;
    }

    @Transactional
    @Override
    public EventEntity createSeatsForEvent(CreateEventAndSeatsRequest entity) {
        
        EventEntity eventWritten = createEvent(entity);
        List<SeatEntity> seats = createSeatEvent(entity, eventWritten);

        eventWritten.setSeats(seats);

        return eventWritten;
    }

    private EventEntity createEvent(CreateEventAndSeatsRequest entity) {
        log.info("Intentando crear un nuevo evento: {}", entity);
        EventEntity eventEntity = eventMapper.toEntity(entity);
        eventEntity.setStatus(StatusEvent.COMING_SOON);
        return this.eventRepository.save(eventEntity);
    }

    @Transactional
    public List<SeatEntity> createSeatEvent(CreateEventAndSeatsRequest request, EventEntity eventWritten) {

        List<SeatEntity> seats = new ArrayList<>();

        for (LayoutRequest layout : request.getLayout()) {
            for (int row = 0; row < layout.getRows(); row++) {
                for (int number = 0; number < layout.getSeatsPerRow(); number++) {
                    SeatEntity seat = SeatEntity.builder()
                            .event(eventWritten)
                            .section(layout.getSection())
                            .row(row + 1)
                            .number(number + 1)
                            .price(layout.getPrice())
                            .status(StatusSeat.AVAILABLE)
                            .build();
                    seats.add(seat);
                }
            }
        }
        seatRepository.saveAll(seats);
        return seats;
    }

    /*
     * A esta altura del flujo ya eberia e estar validado
     * si esta vacio o no
     */
    @Transactional
    @Override
    public void retentionSeatsList(HoldSeatsRequest holdSeatsRequest) {
        List<SeatEntity> seatsRtention = this.seatRepository.findAllById(holdSeatsRequest.getSeatIds());
        for(int i =0; i< seatsRtention.size(); i++){
            if(isHoldedorSale(seatsRtention.get(i))){
               throw new TicketDisasterException("El lugar "+seatsRtention.get(i).getSection());
            }
        }
        seatsRtention.forEach(seat -> seat.setStatus(StatusSeat.HELD));
    }

    private boolean isHoldedorSale(SeatEntity seatEntity) {
        return seatEntity.getStatus() == StatusSeat.HELD || seatEntity.getStatus() == StatusSeat.SOLD;
    }

}
