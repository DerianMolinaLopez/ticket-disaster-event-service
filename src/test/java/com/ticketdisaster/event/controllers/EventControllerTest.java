package com.ticketdisaster.event.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.ticketdisaster.event.dto.request.CreateEventAndSeatsRequest;
import com.ticketdisaster.event.dto.request.HoldSeatsRequest;
import com.ticketdisaster.event.dto.request.ReleaseSeatsHeldRequest;
import com.ticketdisaster.event.dto.response.EventResponse;
import com.ticketdisaster.event.dto.response.Response;
import com.ticketdisaster.event.dto.response.SeatsHeldListRequest;
import com.ticketdisaster.event.enums.StatusEvent;
import com.ticketdisaster.event.exception.SeatHeldException;
import com.ticketdisaster.event.interfaces.EventService;
import com.ticketdisaster.event.mapper.EventMapper;
import com.ticketdisaster.event.models.EventEntity;

/**
 * Tests de slice (@WebMvcTest) para EventController: el service y el mapper
 * se mockean, así que no se necesita levantar JPA/H2; solo valida mapeo de
 * rutas, serialización JSON, validación de DTOs y el ErrorHandler.
 */
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
    }

    // -------- POST /api/v1/event --------

    @Test
    void createSeats_devuelveElEventResponseMapeadoPorElMapper() throws Exception {
        CreateEventAndSeatsRequest request = new CreateEventAndSeatsRequest();
        request.setName("Concierto");
        request.setVenue("Estadio Nacional");
        request.setStartsAt(Instant.parse("2027-01-01T20:00:00Z"));

        EventEntity entity = new EventEntity();
        entity.setId(eventId);
        entity.setName("Concierto");

        EventResponse response = new EventResponse(
                eventId, "Concierto", "Estadio Nacional",
                Instant.parse("2027-01-01T20:00:00Z"), StatusEvent.ON_SALE, List.of());

        given(eventService.createSeatsForEvent(any(CreateEventAndSeatsRequest.class))).willReturn(entity);
        given(eventMapper.toResponse(entity)).willReturn(response);

        mockMvc.perform(post("/api/v1/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.name").value("Concierto"))
                .andExpect(jsonPath("$.status").value("ON_SALE"));

        verify(eventService).createSeatsForEvent(any(CreateEventAndSeatsRequest.class));
        verify(eventMapper).toResponse(entity);
    }

    // -------- GET /api/v1/event/events --------

    @Test
    void getEventsPagination_usaOffsetYLimitPorDefecto_cuandoNoSeEnvianParametros() throws Exception {
        Response<List<EventEntity>> response = Response.<List<EventEntity>>builder()
                .status(HttpStatus.OK)
                .data(List.of())
                .timestamp(LocalDate.now())
                .build();

        given(eventService.getEventsPagination(0, 10)).willReturn(response);

        mockMvc.perform(get("/api/v1/event/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(eventService).getEventsPagination(0, 10);
    }

    @Test
    void getEventsPagination_usaLosParametrosDeOffsetYLimitRecibidos() throws Exception {
        EventEntity entity = new EventEntity();
        entity.setId(eventId);

        Response<List<EventEntity>> response = Response.<List<EventEntity>>builder()
                .status(HttpStatus.OK)
                .data(List.of(entity))
                .timestamp(LocalDate.now())
                .build();

        given(eventService.getEventsPagination(2, 5)).willReturn(response);

        mockMvc.perform(get("/api/v1/event/events").param("offset", "2").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(eventId.toString()));

        verify(eventService).getEventsPagination(2, 5);
    }

    // -------- GET /api/v1/event/event/{id} --------

    @Test
    void getEventDetail_devuelveElEventoCuandoElIdExiste() throws Exception {
        EventEntity entity = new EventEntity();
        entity.setId(eventId);
        entity.setName("Concierto");

        Response<EventEntity> response = Response.<EventEntity>builder()
                .status(HttpStatus.OK)
                .data(entity)
                .timestamp(LocalDate.now())
                .build();

        given(eventService.getEventDetail(eventId)).willReturn(response);

        mockMvc.perform(get("/api/v1/event/event/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(eventId.toString()))
                .andExpect(jsonPath("$.data.name").value("Concierto"));
    }

    @Test
    void getEventDetail_devuelve400_cuandoElIdNoEsUnUuidValido() throws Exception {
        mockMvc.perform(get("/api/v1/event/event/{id}", "no-es-un-uuid"))
                .andExpect(status().isBadRequest());
    }

    // -------- POST /api/v1/event/internal/seats/hold --------

    @Test
    void retentionSeatsList_devuelveLosAsientosRetenidos_cuandoElRequestEsValido() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setBookingId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setSeatIds(Set.of(UUID.randomUUID()));
        request.setHoldMinutes(10);

        SeatsHeldListRequest held = new SeatsHeldListRequest();
        held.setSeatsHeldList(List.copyOf(request.getSeatIds()));

        Response<SeatsHeldListRequest> response = Response.<SeatsHeldListRequest>builder()
                .status(HttpStatus.OK)
                .data(held)
                .timestamp(LocalDate.now())
                .build();

        given(eventService.retentionSeatsList(any(HoldSeatsRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.seatsHeldList", hasSize(1)));
    }

    @Test
    void retentionSeatsList_devuelve400_cuandoFaltaElBookingId() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setEventId(eventId);
        request.setSeatIds(Set.of(UUID.randomUUID()));
        request.setHoldMinutes(10);

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retentionSeatsList_devuelve400_cuandoLaListaDeAsientosEstaVacia() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setBookingId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setSeatIds(Set.of());
        request.setHoldMinutes(10);

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retentionSeatsList_devuelve400_cuandoSeSuperanLos6AsientosPermitidos() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setBookingId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setSeatIds(Set.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        request.setHoldMinutes(10);

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retentionSeatsList_devuelve400_cuandoHoldMinutesSuperaElMaximoDe15() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setBookingId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setSeatIds(Set.of(UUID.randomUUID()));
        request.setHoldMinutes(16);

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retentionSeatsList_devuelve409_cuandoElServiceLanzaSeatHeldException() throws Exception {
        HoldSeatsRequest request = new HoldSeatsRequest();
        request.setBookingId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setSeatIds(Set.of(UUID.randomUUID()));
        request.setHoldMinutes(10);

        given(eventService.retentionSeatsList(any(HoldSeatsRequest.class)))
                .willThrow(new SeatHeldException("Uno o más asientos ya están retenidos"));

        mockMvc.perform(post("/api/v1/event/internal/seats/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Uno o más asientos ya están retenidos"));
    }

    // -------- POST /api/v1/event/internal/seats/release --------

    @Test
    void releaseSeats_devuelveOk_cuandoElRequestEsValido() throws Exception {
        ReleaseSeatsHeldRequest request = new ReleaseSeatsHeldRequest();
        request.setEventId(eventId);
        request.setListOfSeats(List.of(UUID.randomUUID()));

        Response<String> response = Response.<String>builder()
                .status(HttpStatus.OK)
                .data("Asientos liberados")
                .timestamp(LocalDate.now())
                .build();

        given(eventService.releaseSeats(any(ReleaseSeatsHeldRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/event/internal/seats/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Asientos liberados"));
    }

    @Test
    void releaseSeats_devuelve400_cuandoFaltaElEventId() throws Exception {
        ReleaseSeatsHeldRequest request = new ReleaseSeatsHeldRequest();
        request.setListOfSeats(List.of(UUID.randomUUID()));

        mockMvc.perform(post("/api/v1/event/internal/seats/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void releaseSeats_devuelve400_cuandoLaListaDeAsientosEstaVacia() throws Exception {
        ReleaseSeatsHeldRequest request = new ReleaseSeatsHeldRequest();
        request.setEventId(eventId);
        request.setListOfSeats(List.of());

        mockMvc.perform(post("/api/v1/event/internal/seats/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
