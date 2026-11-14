package com.ticketdisaster.event.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
    @Setter
public class TicketDisasterException extends RuntimeException {

    private HttpStatus erroCode;
    private String message;
    private Map<String,Object> context;

    public TicketDisasterException(String message){
        super(message);
    }
    public TicketDisasterException(HttpStatus errorCode,String message,Map<String,Object> context){
        this.context=context;
        this.message=message;
        this.erroCode=errorCode;
    }
}
