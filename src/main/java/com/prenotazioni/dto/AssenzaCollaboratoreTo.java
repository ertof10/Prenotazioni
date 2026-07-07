package com.prenotazioni.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssenzaCollaboratoreTo {

    private Integer idAssenzaCollaboratore;

    private Integer idCollaboratore;
    private String nomeCollaboratore;
    private String cognomeCollaboratore;

    private LocalDate dataInizioAssenza;
    private LocalDate dataFineAssenza;

    private LocalTime oraInizioAssenza;
    private LocalTime oraFineAssenza;

    private String motivoAssenzaCollaboratore;

    private Boolean attivoAssenzaCollaboratore;

    private LocalDateTime dataCreazioneAssenzaCollaboratore;
    private LocalDateTime dataModificaAssenzaCollaboratore;
}