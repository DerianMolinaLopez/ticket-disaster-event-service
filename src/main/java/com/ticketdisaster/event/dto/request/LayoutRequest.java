package com.ticketdisaster.event.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LayoutRequest {
    @jakarta.validation.constraints.NotNull
    @NotBlank
    private String section;
    @Min(0)
    private Integer rows;
    @Min(0)
    private Integer seatsPerRow;
    @Min(0)
    private BigDecimal price;

}
