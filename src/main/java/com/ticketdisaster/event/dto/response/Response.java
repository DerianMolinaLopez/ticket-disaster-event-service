package com.ticketdisaster.event.dto.response;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
    @Builder 

public class Response<T> {
    private HttpStatus status;
    private String message;
    private String path;
    private LocalDate timestamp;
    private T data;
}
