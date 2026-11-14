package com.ticketdisaster.event.interfaces.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketdisaster.event.models.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {

}
