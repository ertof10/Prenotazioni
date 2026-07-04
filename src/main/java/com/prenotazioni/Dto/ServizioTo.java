package com.prenotazioni.Dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServizioTo {

    private Integer idServizio;

    @NotBlank(message = "Il nome del servizio è obbligatorio")
    @Size(max = 100, message = "Nome servizio troppo lungo")
    private String nomeServizio;

    @NotBlank(message = "La categoria del servizio è obbligatoria")
    @Size(max = 100, message = "Categoria servizio troppo lunga")
    private String categoriaServizio;

    @Size(max = 500, message = "Descrizione servizio troppo lunga")
    private String descrizioneServizio;

    @NotNull(message = "Il prezzo del servizio è obbligatorio")
    @DecimalMin(value = "0.00", message = "Il prezzo non può essere negativo")
    private BigDecimal prezzoServizio;

    @NotNull(message = "La durata del servizio è obbligatoria")
    @Min(value = 1, message = "La durata deve essere almeno di 1 minuto")
    private Integer durataMinutiServizio;

    @NotBlank(message = "Il tipo gestione servizio è obbligatorio")
    private String tipoGestioneServizio;

    private Integer postiMassimiServizio;

    private Boolean attivoServizio;

    @Size(max = 500, message = "URL immagine troppo lungo")
    private String urlImmagineServizio;

    private LocalDateTime dataCreazioneServizio;

    private LocalDateTime dataModificaServizio;

    public ServizioTo() {
    }
}