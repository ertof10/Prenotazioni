package com.prenotazioni.po;

import com.prenotazioni.enums.TipoGestioneServizio;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "servizi")
@Data
public class ServizioPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServizio;

    @Column(nullable = false, length = 100)
    private String nomeServizio;

    @Column(nullable = false, length = 100)
    private String categoriaServizio;

    @Column(length = 500)
    private String descrizioneServizio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoServizio;

    @Column(nullable = false)
    private Integer durataMinutiServizio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoGestioneServizio tipoGestioneServizio;

    @Column
    private Integer postiMassimiServizio;

    @Column(nullable = false)
    private Boolean attivoServizio;

    @Column(length = 500)
    private String urlImmagineServizio;

    @Column(nullable = false)
    private LocalDateTime dataCreazioneServizio;

    @Column
    private LocalDateTime dataModificaServizio;

    public ServizioPo() {  // Costruttore vuoto richiesto da JPA
    }
}