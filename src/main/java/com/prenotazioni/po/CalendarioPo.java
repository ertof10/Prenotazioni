package com.prenotazioni.po;

import lombok.Data;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "calendari")
@Data
public class CalendarioPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCalendario;

    @ManyToOne
    @JoinColumn(name = "id_collaboratore", nullable = false)
    private CollaboratorePo collaboratorePo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DayOfWeek giornoSettimanaCalendario;

    @Column(nullable = false)
    private LocalTime oraInizioCalendario;

    @Column(nullable = false)
    private LocalTime oraFineCalendario;

    @Column(nullable = false)
    private Boolean attivoCalendario;

    @Column(nullable = false)
    private LocalDateTime dataCreazioneCalendario;

    private LocalDateTime dataModificaCalendario;

    public CalendarioPo() {   // Costruttore vuoto richiesto da JPA
    }
}