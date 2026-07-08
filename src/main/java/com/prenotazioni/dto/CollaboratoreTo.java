package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CollaboratoreTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idCollaboratore;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 60, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeCollaboratore;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 60, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeCollaboratore;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 120, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String emailCollaboratore;

    @Size(max = 20, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(
            regexp = "^$|^[+]?[0-9\\s().-]{6,20}$",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String telefonoCollaboratore;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String specializzazioneCollaboratore;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String descrizioneCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Boolean attivoCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazioneCollaboratore;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaCollaboratore;
}