package com.prenotazioni.po;

import com.prenotazioni.enums.StatoPreventivo;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "preventivi")
@Data
public class PreventivoPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPreventivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false)
    private UtentePo utentePo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servizio")
    private ServizioPo servizioPo;

    @Column(nullable = false, length = 150)
    private String titoloPreventivo;

    @Column(nullable = false, length = 2000)
    private String descrizioneRichiestaPreventivo;

    @Column(precision = 10, scale = 2)
    private BigDecimal importoPreventivo;

    @Column(length = 2000)
    private String rispostaPreventivo;

    @Column
    private LocalDate dataScadenzaPreventivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatoPreventivo statoPreventivo;

    @Column(nullable = false)
    private LocalDateTime dataCreazionePreventivo;

    @Column
    private LocalDateTime dataModificaPreventivo;

    public PreventivoPo() {   // Costruttore vuoto richiesto da JPA
    }
}