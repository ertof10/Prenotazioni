package com.prenotazioni.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CollaboratoreServizioTo {

    private Integer idCollaboratoreServizio;

    @NotNull(message = "L'id collaboratore è obbligatorio")
    private Integer idCollaboratore;

    @NotNull(message = "L'id servizio è obbligatorio")
    private Integer idServizio;

    private Boolean attivoCollaboratoreServizio;

    @Min(value = 1, message = "La durata personalizzata deve essere almeno di 1 minuto")
    private Integer durataPersonalizzataMinuti;

    @DecimalMin(value = "0.00", message = "Il prezzo personalizzato non può essere negativo")
    private BigDecimal prezzoPersonalizzato;

    @Size(max = 500, message = "Note collaboratore servizio troppo lunghe")
    private String noteCollaboratoreServizio;

    private LocalDateTime dataCreazioneCollaboratoreServizio;

    private LocalDateTime dataModificaCollaboratoreServizio;


}