package com.ticketdisaster.event.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data

    @NoArgsConstructor 
public class SeatsHeldListRequest {

    List<UUID> seatsHeldList;

}
