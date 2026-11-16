package com.ticketdisaster.event.interfaces.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketdisaster.event.models.SeatEntity;

public interface SeatRepository extends JpaRepository<SeatEntity, UUID> {
    List<SeatEntity> findAllByIdInAndEventId(List<UUID> ids, UUID eventId);
}
