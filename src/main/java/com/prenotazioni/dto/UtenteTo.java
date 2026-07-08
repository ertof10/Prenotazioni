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

@Data
public class UtenteTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idUtente;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 60, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeUtente;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 60, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeUtente;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 120, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String emailUtente;

    @Size(max = 20, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(
            regexp = "^$|^[+]?[0-9\\s().-]{6,20}$",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String telefonoUtente;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Boolean attivoUtente;
}