package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CalendarioTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idCalendario;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeCollaboratore;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(
            regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String giornoSettimanaCalendario;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraInizioCalendario;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraFineCalendario;

    @Null(groups = ValidationGroups.Create.class)
    private Boolean attivoCalendario;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazioneCalendario;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaCalendario;
}