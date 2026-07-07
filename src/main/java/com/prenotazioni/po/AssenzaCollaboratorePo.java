package com.prenotazioni.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "assenze_collaboratori")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssenzaCollaboratorePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_assenza_collaboratore")
    private Integer idAssenzaCollaboratore;

    @ManyToOne
    @JoinColumn(name = "id_collaboratore", nullable = false)
    private CollaboratorePo collaboratorePo;

    @Column(name = "data_inizio_assenza", nullable = false)
    private LocalDate dataInizioAssenza;

    @Column(name = "data_fine_assenza", nullable = false)
    private LocalDate dataFineAssenza;

    @Column(name = "ora_inizio_assenza", nullable = false)
    private LocalTime oraInizioAssenza;

    @Column(name = "ora_fine_assenza", nullable = false)
    private LocalTime oraFineAssenza;

    @Column(name = "motivo_assenza_collaboratore")
    private String motivoAssenzaCollaboratore;

    @Column(name = "attivo_assenza_collaboratore", nullable = false)
    private Boolean attivoAssenzaCollaboratore;

    @Column(name = "data_creazione_assenza_collaboratore")
    private LocalDateTime dataCreazioneAssenzaCollaboratore;

    @Column(name = "data_modifica_assenza_collaboratore")
    private LocalDateTime dataModificaAssenzaCollaboratore;
}