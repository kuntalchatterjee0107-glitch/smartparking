package org.airtribe.smartparking.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

public class CustomExceptions {
    public static class NoSpotAvailableException extends RuntimeException {
        public NoSpotAvailableException(String msg) { super(msg); }
    }
    public static class TicketNotFoundException extends RuntimeException {
        public TicketNotFoundException(String msg) { super(msg); }
    }
    public static class VehicleAlreadyParkedException extends RuntimeException {
        public VehicleAlreadyParkedException(String msg) { super(msg); }
    }
}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomExceptions.NoSpotAvailableException.class)
    public ResponseEntity<?> handleNoSpot(CustomExceptions.NoSpotAvailableException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CustomExceptions.VehicleAlreadyParkedException.class)
    public ResponseEntity<?> handleDuplicateVehicle(CustomExceptions.VehicleAlreadyParkedException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CustomExceptions.TicketNotFoundException.class)
    public ResponseEntity<?> handleTicketNotFound(CustomExceptions.TicketNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<?> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", message
        ));
    }
}