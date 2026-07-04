package com.prenotazioni.Error;



import com.prenotazioni.Response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {

        AppError appError = ex.getError();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                appError.getStatus().value(),
                appError.name(),
                appError.getMessage()
        );

        log.warn("Errore applicativo: {} - {}", appError.name(), appError.getMessage());

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String message = "Errore di validazione";

        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getDefaultMessage();
        }

        AppError appError = AppError.ERRORE_VALIDAZIONE;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                appError.getStatus().value(),
                appError.name(),
                message
        );

        log.warn("Errore validazione DTO: {}", message);

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        AppError appError = AppError.ERRORE_GENERICO;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                appError.getStatus().value(),
                appError.name(),
                "Si è verificato un errore imprevisto"
        );

        log.error("Errore imprevisto", ex);

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException ex) {

        AppError appError = AppError.METODO_NON_SUPPORTATO;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                appError.getStatus().value(),
                appError.name(),
                appError.getMessage()
        );

        log.warn("Metodo HTTP non supportato per questa operazione : {}", ex.getMethod());

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }
}