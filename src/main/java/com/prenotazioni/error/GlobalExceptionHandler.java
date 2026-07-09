package com.prenotazioni.error;

import com.prenotazioni.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String RICHIESTA_NON_VALIDA = "Richiesta non valida";
    private static final String RICHIESTA_MALFORMATA = "Richiesta malformata o non leggibile";
    private static final String PARAMETRO_NON_VALIDO = "Parametro non valido";

    private static final String CAMPO_PREFIX = "Il campo '";
    private static final String CAMPO_SEPARATOR = "' ";

    private static final String SUFFIX_NON_VALIDO = "non è valido";
    private static final String SUFFIX_OBBLIGATORIO = "è obbligatorio";
    private static final String SUFFIX_NON_DEVE_ESSERE_VALORIZZATO = "non deve essere valorizzato";
    private static final String SUFFIX_MAGGIORE_ZERO = "deve essere maggiore di zero";
    private static final String SUFFIX_EMAIL_NON_VALIDA = "deve contenere un indirizzo email valido";
    private static final String SUFFIX_LUNGHEZZA_MASSIMA = "supera la lunghezza massima consentita";
    private static final String SUFFIX_FORMATO_NON_VALIDO = "ha un formato non valido";
    private static final String SUFFIX_DATA_PASSATO = "non può essere nel passato";
    private static final String SUFFIX_POSITIVO = "deve essere positivo";
    private static final String SUFFIX_POSITIVO_O_ZERO = "deve essere positivo o uguale a zero";
    private static final String SUFFIX_DECIMALE_MINIMO = "deve essere maggiore o uguale al valore minimo consentito";
    private static final String SUFFIX_MASSIMO = "supera il valore massimo consentito";
    private static final String SUFFIX_NUMERO_NON_VALIDO = "deve rispettare il formato numerico consentito";

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(
            ServiceException ex,
            HttpServletRequest request) {

        AppError appError = ex.getError();

        log.warn("Errore applicativo gestito | metodo={} | uri={} | errore={} | status={} | messaggio={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.name(),
                appError.getStatus().value(),
                appError.getMessage());

        return creaResponseEntity(appError, appError.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_VALIDAZIONE;
        String message = estraiMessaggioValidazioneDto(ex, appError);

        log.warn("Errore validazione DTO | metodo={} | uri={} | status={} | messaggio={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                message);

        return creaResponseEntity(appError, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_VALIDAZIONE;
        String message = estraiMessaggioValidazioneParametro(ex, appError);

        log.warn("Errore validazione parametro | metodo={} | uri={} | status={} | messaggio={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                message);

        return creaResponseEntity(appError, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_VALIDAZIONE;

        log.warn("Errore lettura body richiesta | metodo={} | uri={} | status={} | exception={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                ex.getClass().getSimpleName());

        return creaResponseEntity(appError, RICHIESTA_MALFORMATA);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_VALIDAZIONE;
        String message = creaMessaggioParametroNonValido(ex);

        log.warn("Errore conversione parametro | metodo={} | uri={} | status={} | parametro={} | valore={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                ex.getName(),
                ex.getValue());

        return creaResponseEntity(appError, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        AppError appError = AppError.METODO_NON_SUPPORTATO;

        log.warn("Metodo HTTP non supportato | metodo={} | uri={} | status={} | metodiSupportati={}",
                ex.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                ex.getSupportedHttpMethods());

        return creaResponseEntity(appError, appError.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        AppError appError = AppError.ERRORE_GENERICO;

        log.error("Errore imprevisto non gestito | metodo={} | uri={} | status={} | exception={}",
                request.getMethod(),
                request.getRequestURI(),
                appError.getStatus().value(),
                ex.getClass().getSimpleName(),
                ex);

        return creaResponseEntity(appError, appError.getMessage());
    }

    private ResponseEntity<ErrorResponse> creaResponseEntity(AppError appError, String message) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                appError.getStatus().value(),
                appError.name(),
                message
        );

        return ResponseEntity
                .status(appError.getStatus())
                .body(response);
    }

    private String estraiMessaggioValidazioneDto(
            MethodArgumentNotValidException ex,
            AppError appError) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        if (fieldError == null) {
            return appError.getMessage();
        }

        String constraintName = fieldError.getCode();

        if (constraintName == null || constraintName.trim().isEmpty()) {
            return creaMessaggioCampo(fieldError.getField(), SUFFIX_NON_VALIDO);
        }

        return creaMessaggioValidazione(
                fieldError.getField(),
                constraintName
        );
    }

    private String estraiMessaggioValidazioneParametro(
            ConstraintViolationException ex,
            AppError appError) {

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        if (violations.isEmpty()) {
            return appError.getMessage();
        }

        Iterator<ConstraintViolation<?>> iterator = violations.iterator();
        ConstraintViolation<?> violation = iterator.next();

        String fieldName = estraiNomeParametro(violation);

        String constraintName = violation
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .getSimpleName();

        return creaMessaggioValidazione(fieldName, constraintName);
    }

    private String creaMessaggioParametroNonValido(MethodArgumentTypeMismatchException ex) {

        String nomeParametro = ex.getName().trim();

        if (nomeParametro.isEmpty()) {
            return PARAMETRO_NON_VALIDO;
        }

        return creaMessaggioCampo(nomeParametro, SUFFIX_FORMATO_NON_VALIDO);
    }

    private String creaMessaggioValidazione(String fieldName, String constraintName) {

        String nomeCampo = fieldName.trim();

        if (nomeCampo.isEmpty()) {
            return RICHIESTA_NON_VALIDA;
        }

        String nomeVincolo = constraintName.trim();

        if (nomeVincolo.isEmpty()) {
            return creaMessaggioCampo(nomeCampo, SUFFIX_NON_VALIDO);
        }

        switch (nomeVincolo) {
            case "NotBlank":
            case "NotNull":
                return creaMessaggioCampo(nomeCampo, SUFFIX_OBBLIGATORIO);

            case "Null":
                return creaMessaggioCampo(nomeCampo, SUFFIX_NON_DEVE_ESSERE_VALORIZZATO);

            case "Min":
                return creaMessaggioCampo(nomeCampo, SUFFIX_MAGGIORE_ZERO);

            case "Max":
                return creaMessaggioCampo(nomeCampo, SUFFIX_MASSIMO);

            case "Email":
                return creaMessaggioCampo(nomeCampo, SUFFIX_EMAIL_NON_VALIDA);

            case "Size":
                return creaMessaggioCampo(nomeCampo, SUFFIX_LUNGHEZZA_MASSIMA);

            case "Pattern":
                return creaMessaggioCampo(nomeCampo, SUFFIX_FORMATO_NON_VALIDO);

            case "FutureOrPresent":
                return creaMessaggioCampo(nomeCampo, SUFFIX_DATA_PASSATO);

            case "Positive":
                return creaMessaggioCampo(nomeCampo, SUFFIX_POSITIVO);

            case "PositiveOrZero":
                return creaMessaggioCampo(nomeCampo, SUFFIX_POSITIVO_O_ZERO);

            case "DecimalMin":
                return creaMessaggioCampo(nomeCampo, SUFFIX_DECIMALE_MINIMO);

            case "Digits":
                return creaMessaggioCampo(nomeCampo, SUFFIX_NUMERO_NON_VALIDO);

            default:
                return creaMessaggioCampo(nomeCampo, SUFFIX_NON_VALIDO);
        }
    }

    private String creaMessaggioCampo(String fieldName, String suffix) {
        return CAMPO_PREFIX + fieldName + CAMPO_SEPARATOR + suffix;
    }

    private String estraiNomeParametro(ConstraintViolation<?> violation) {

        String propertyPath = violation.getPropertyPath().toString();

        if (propertyPath.contains(".")) {
            return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        }

        return propertyPath;
    }
}