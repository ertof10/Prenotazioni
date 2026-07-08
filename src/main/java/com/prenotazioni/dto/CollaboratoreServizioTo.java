package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CollaboratoreServizioTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idCollaboratoreServizio;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idCollaboratore;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idServizio;

    @Null(groups = ValidationGroups.Create.class)
    private Boolean attivoCollaboratoreServizio;

    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Max(value = 1440, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer durataPersonalizzataMinuti;

    @DecimalMin(value = "0.00", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Digits(integer = 8, fraction = 2, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private BigDecimal prezzoPersonalizzato;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String noteCollaboratoreServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazioneCollaboratoreServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaCollaboratoreServizio;
}