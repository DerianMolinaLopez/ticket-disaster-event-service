package com.ticketdisaster.event.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ticketdisaster.event.dto.request.ReleaseSeatsHeldRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ticketdisaster.event.constants.ApiConstants;
import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.request.LayoutRequest;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.enums.StatusEvent;
import com.ticketdisaster.event.enums.StatusSeat;
import com.ticketdisaster.event.exception.SeatHeldException;
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

    public EventServiceimpl(EventRepository eventRepository, SeatRepository seatRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventMapper = eventMapper;
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
    public Response<SeatsHeldListRequest> retentionSeatsList(HoldSeatsRequest holdSeatsRequest) {
        List<SeatEntity> seatsRtention = this.seatRepository.findAllById(holdSeatsRequest.getSeatIds());
        for (int i = 0; i < seatsRtention.size(); i++) {
            if (isHeldorSale(seatsRtention.get(i))) {
                throw SeatHeldException.builder()
                        .message("El lugar " + seatsRtention.get(i).getNumber() + " de la seccion "
                                + seatsRtention.get(i).getSection() + " ya esta siendo reservado o ya fue comprado")
                        .context("seatHeld", seatsRtention.get(i))
                        .build();
            }
        }

        Instant heldUntil = Instant.now().plus(Duration.ofMinutes(holdSeatsRequest.getHoldMinutes()));
        seatsRtention.forEach(seat -> {
            seat.setStatus(StatusSeat.HELD);
            seat.setHeldBy(holdSeatsRequest.getBookingId());
            seat.setHeldUntil(heldUntil);
        });
        SeatsHeldListRequest request = new SeatsHeldListRequest();
        request.setSeatsHeldList(seatsRtention.stream().map(SeatEntity::getId).toList());

        return Response.<SeatsHeldListRequest>builder()
                .data(request)
                .message("Se han apartado los lugares")
                .path(ApiConstants.SEATS_HOLD)
                .status(HttpStatus.ACCEPTED)
                .timestamp(LocalDate.now())
                .build();
    }

    private boolean isHeldorSale(SeatEntity seatEntity) {
        return seatEntity.getStatus() == StatusSeat.HELD || seatEntity.getStatus() == StatusSeat.SOLD;
    }

    @Override
    public Response<List<EventEntity>>  getEventsPagination(Integer offset, Integer limit) {
        int page = offset / limit;
        Pageable pagable = PageRequest.of(page, limit);
         Page<EventEntity> events = this.eventRepository.findAll(pagable);

         return Response.<List<EventEntity>>builder().data(events.getContent())
                 .status(HttpStatus.ACCEPTED)
                 .message("Lista de eventos")
                 .path(ApiConstants.EVENTS)
                 .timestamp(LocalDate.now())
                 .build();
    }

    @Override
    public Response<EventEntity> getEventDetail(UUID id) {
        Optional<EventEntity> eventFound = this.eventRepository.findById(id);
        return Response.<EventEntity>builder().data(eventFound.get())
                .message("Detalle del evento")
                .status(HttpStatus.ACCEPTED).path(ApiConstants.EVENT).timestamp(LocalDate.now()).build();
    }
    @Transactional
    @Override
    public Response<String> releaseSeats(ReleaseSeatsHeldRequest seats) {
        updateSeatsStatus(seats, StatusSeat.AVAILABLE);
        return buildResponse("Se han liberado los lugares", ApiConstants.SEATS_RELEASE);
    }

    @Transactional
    @Override
    public Response<String> confirmReservation(ReleaseSeatsHeldRequest seats) {
        updateSeatsStatus(seats, StatusSeat.SOLD); // o RESERVED, según tu lógica
        return buildResponse("Reserva confirmada", ApiConstants.SEATS_CONFIRM);
    }

    private void updateSeatsStatus(ReleaseSeatsHeldRequest request, StatusSeat status) {
        List<SeatEntity> seats = this.seatRepository
                .findAllByIdInAndEventId(request.getListOfSeats(), request.getEventId());
        seats.forEach(seat -> seat.setStatus(status));
    }

    private Response<String> buildResponse(String message, String path) {
        return Response.<String>builder()
                .data(message)
                .message("")
                .path(path)
                .timestamp(LocalDate.now())
                .build();
    }
}
