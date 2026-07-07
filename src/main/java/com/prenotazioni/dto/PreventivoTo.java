package com.prenotazioni.dto;

import com.prenotazioni.enums.StatoPreventivo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PreventivoTo {

    private Integer idPreventivo;

    private Integer idUtente;
    private String nomeUtente;
    private String cognomeUtente;

    private Integer idServizio;
    private String nomeServizio;

    private String titoloPreventivo;

    private String descrizioneRichiestaPreventivo;

    private BigDecimal importoPreventivo;

    private String rispostaPreventivo;

    private LocalDate dataScadenzaPreventivo;

    private StatoPreventivo statoPreventivo;

    private LocalDateTime dataCreazionePreventivo;

    private LocalDateTime dataModificaPreventivo;
}