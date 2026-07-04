package com.prenotazioni.Dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class PrenotazioneTo {

    private Integer idPrenotazione;

    @NotNull(message = "L'id utente è obbligatorio")
    private Integer idUtente;

    @NotNull(message = "L'id servizio è obbligatorio")
    private Integer idServizio;

    @NotNull(message = "La data prenotazione è obbligatoria")
    private LocalDate dataPrenotazione;

    @NotNull(message = "L'ora inizio prenotazione è obbligatoria")
    private LocalTime oraInizioPrenotazione;

    @NotNull(message = "L'ora fine prenotazione è obbligatoria")
    private LocalTime oraFinePrenotazione;

    private String statoPrenotazione;

    @Size(max = 500, message = "Note prenotazione troppo lunghe")
    private String notePrenotazione;

    private LocalDateTime dataCreazionePrenotazione;

    private LocalDateTime dataModificaPrenotazione;

    private Integer idCollaboratore;

    private String nomeCollaboratore;

    private String cognomeCollaboratore;

    public PrenotazioneTo() {
    }
}