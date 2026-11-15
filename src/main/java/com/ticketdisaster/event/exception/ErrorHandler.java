package com.ticketdisaster.event.exception;

import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.ticketdisaster.event.constants.ApiConstants;
import com.ticketdisaster.event.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SeatHeldException.class)
    public ResponseEntity<Response<ArrayList>> catchSeatHeldException(SeatHeldException ex,
            HttpServletRequest request,
            WebRequest webRequest) {


        log.info("Ocurrio un error durante la ejecucion");
        log.error(ex.getMessage());
        Response<ArrayList> body = Response.<ArrayList>builder()
            .data(new ArrayList<>())
            .message(ex.getMessage())
            .status(ex.getErrorCode())
            .path(ApiConstants.SEATS_HOLD)
            .timestamp(LocalDate.now())
            .build();
        return ResponseEntity.status(ex.getErrorCode()).body(body);
    }

}
