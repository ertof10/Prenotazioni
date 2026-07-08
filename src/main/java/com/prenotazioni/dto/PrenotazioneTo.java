package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class PrenotazioneTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idPrenotazione;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idUtente;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idServizio;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @FutureOrPresent(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate dataPrenotazione;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraInizioPrenotazione;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraFinePrenotazione;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String statoPrenotazione;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String notePrenotazione;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazionePrenotazione;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaPrenotazione;

    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeCollaboratore;
}