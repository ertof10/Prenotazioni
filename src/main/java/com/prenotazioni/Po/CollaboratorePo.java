package com.prenotazioni.Po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "collaboratori")
@Data
public class CollaboratorePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCollaboratore;

    @Column(nullable = false, length = 60)
    private String nomeCollaboratore;

    @Column(nullable = false, length = 60)
    private String cognomeCollaboratore;

    @Column(nullable = false, length = 120, unique = true)
    private String emailCollaboratore;

    @Column(length = 20)
    private String telefonoCollaboratore;

    @Column(nullable = false, length = 100)
    private String specializzazioneCollaboratore;

    @Column(length = 500)
    private String descrizioneCollaboratore;

    @Column(nullable = false)
    private LocalDateTime dataCreazioneCollaboratore;

    private LocalDateTime dataModificaCollaboratore;

    @Column(nullable = false)
    private Boolean attivoCollaboratore = true;

    public CollaboratorePo() {
    }
}