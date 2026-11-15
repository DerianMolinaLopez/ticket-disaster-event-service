package com.ticketdisaster.event.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
public class SeatHeldException extends TicketDisasterException{

    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    @Builder
    public SeatHeldException(String message, @Singular("context") Map<String,Object> context){
        super(STATUS,message,context);
    }
    public SeatHeldException(String message){
        super(STATUS,message,Map.of());
    }

}
