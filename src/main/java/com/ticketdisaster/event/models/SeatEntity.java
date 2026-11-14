package com.ticketdisaster.event.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ticketdisaster.event.enums.StatusSeat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Exclude;

@Entity
@Table(name = "seats")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ToString.Exclude
    @Exclude 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;
    private String section;
    private Integer row;
    private Integer number;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSeat status;
    private UUID heldBy;
    private Instant heldUntil;
}
