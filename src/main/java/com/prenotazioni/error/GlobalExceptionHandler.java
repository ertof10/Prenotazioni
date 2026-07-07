package com.prenotazioni.error;

import com.prenotazioni.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(
            ServiceException ex,
            HttpServletRequest request) {

        AppError appError = ex.getError();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                appError.getStatus().value(),
                appError.name(),
                appError.getMessage()
        );

        log.warn(
                "Errore applicativo | metodo={} | uri={} | errore={} | messaggio={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.name(),
                appError.getMessage()
        );

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = "Errore di validazione";

        FieldError fieldError = ex.getBindingResult().getFieldError();

        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            message = fieldError.getDefaultMessage();
        }

        AppError appError = AppError.ERRORE_VALIDAZIONE;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                appError.getStatus().value(),
                appError.name(),
                message
        );

        log.warn(
                "Errore validazione DTO | metodo={} | uri={} | messaggio={}",
                request.getMethod(),
                request.getRequestURI(),
                message
        );

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        AppError appError = AppError.METODO_NON_SUPPORTATO;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                appError.getStatus().value(),
                appError.name(),
                appError.getMessage()
        );

        log.warn(
                "Metodo HTTP non supportato | metodo={} | uri={} | metodiSupportati={}",
                ex.getMethod(),
                request.getRequestURI(),
                ex.getSupportedHttpMethods()
        );

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_GENERICO;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                appError.getStatus().value(),
                appError.name(),
                "Si è verificato un errore imprevisto"
        );

        log.error(
                "Errore imprevisto | metodo={} | uri={} | exception={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex
        );

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }
}