package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssenzaCollaboratoreTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idAssenzaCollaboratore;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeCollaboratore;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @FutureOrPresent(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate dataInizioAssenza;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @FutureOrPresent(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDate dataFineAssenza;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraInizioAssenza;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalTime oraFineAssenza;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String motivoAssenzaCollaboratore;

    @Null(groups = ValidationGroups.Create.class)
    private Boolean attivoAssenzaCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazioneAssenzaCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaAssenzaCollaboratore;
}