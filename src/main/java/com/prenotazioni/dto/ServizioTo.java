package com.prenotazioni.dto;

import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServizioTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idServizio;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeServizio;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 100, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String categoriaServizio;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String descrizioneServizio;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @DecimalMin(value = "0.00", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Digits(integer = 8, fraction = 2, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private BigDecimal prezzoServizio;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Max(value = 1440, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer durataMinutiServizio;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(
            regexp = "^(CON_COLLABORATORE|A_POSTI)$",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String tipoGestioneServizio;

    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Max(value = 100000, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer postiMassimiServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Boolean attivoServizio;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(
            regexp = "^$|^https?://.+",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String urlImmagineServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazioneServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaServizio;
}