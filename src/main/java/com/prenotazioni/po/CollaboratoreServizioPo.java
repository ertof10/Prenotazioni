package com.prenotazioni.po;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "collaboratori_servizi")
@Data
public class CollaboratoreServizioPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCollaboratoreServizio;

    @ManyToOne
    @JoinColumn(name = "id_collaboratore", nullable = false)
    private CollaboratorePo collaboratorePo;

    @ManyToOne
    @JoinColumn(name = "id_servizio", nullable = false)
    private ServizioPo servizioPo;

    @Column(nullable = false)
    private Boolean attivoCollaboratoreServizio;

    @Column
    private Integer durataPersonalizzataMinuti;

    @Column(precision = 10, scale = 2)
    private BigDecimal prezzoPersonalizzato;

    @Column(length = 500)
    private String noteCollaboratoreServizio;

    @Column(nullable = false)
    private LocalDateTime dataCreazioneCollaboratoreServizio;

    private LocalDateTime dataModificaCollaboratoreServizio;

    public CollaboratoreServizioPo() {   // Costruttore vuoto richiesto da JPA
    }
}