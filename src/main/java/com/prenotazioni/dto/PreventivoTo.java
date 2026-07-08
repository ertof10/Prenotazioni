package com.prenotazioni.dto;

import com.prenotazioni.enums.StatoPreventivo;
import com.prenotazioni.validation.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PreventivoTo {

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    @Min(value = 1, groups = ValidationGroups.Update.class)
    private Integer idPreventivo;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idUtente;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeUtente;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String cognomeUtente;

    @Min(value = 1, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Integer idServizio;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nomeServizio;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 150, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String titoloPreventivo;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 1000, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String descrizioneRichiestaPreventivo;

    @Null(groups = ValidationGroups.Create.class)
    @DecimalMin(value = "0.01", groups = ValidationGroups.Update.class)
    @Digits(integer = 8, fraction = 2, groups = ValidationGroups.Update.class)
    private BigDecimal importoPreventivo;

    @Null(groups = ValidationGroups.Create.class)
    @Size(max = 1000, groups = ValidationGroups.Update.class)
    private String rispostaPreventivo;

    @Null(groups = ValidationGroups.Create.class)
    @FutureOrPresent(groups = ValidationGroups.Update.class)
    private LocalDate dataScadenzaPreventivo;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private StatoPreventivo statoPreventivo;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataCreazionePreventivo;

    @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private LocalDateTime dataModificaPreventivo;
}