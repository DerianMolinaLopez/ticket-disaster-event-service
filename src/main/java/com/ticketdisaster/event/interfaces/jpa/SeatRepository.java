package com.ticketdisaster.event.interfaces.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketdisaster.event.models.SeatEntity;

public interface SeatRepository extends JpaRepository<SeatEntity, UUID> {

}
