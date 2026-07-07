package com.prenotazioni.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CalendarioTo {

    private Integer idCalendario;

    @NotNull(message = "L'id collaboratore è obbligatorio")
    private Integer idCollaboratore;

    private String nomeCollaboratore;

    private String cognomeCollaboratore;

    @NotBlank(message = "Il giorno settimana calendario è obbligatorio")
    private String giornoSettimanaCalendario;

    @NotNull(message = "L'ora inizio calendario è obbligatoria")
    private LocalTime oraInizioCalendario;

    @NotNull(message = "L'ora fine calendario è obbligatoria")
    private LocalTime oraFineCalendario;

    private Boolean attivoCalendario;

    private LocalDateTime dataCreazioneCalendario;

    private LocalDateTime dataModificaCalendario;


}