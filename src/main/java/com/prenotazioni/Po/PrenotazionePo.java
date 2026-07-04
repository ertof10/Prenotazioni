package com.prenotazioni.Po;

import com.prenotazioni.Enums.StatoPrenotazione;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "prenotazioni")
@Data
public class PrenotazionePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPrenotazione;

    @ManyToOne
    @JoinColumn(name= "id_utente" , nullable = false)
    private UtentePo utentePo;

    @ManyToOne
    @JoinColumn(name = "id_servizio", nullable = false)
    private ServizioPo servizioPo;

    @ManyToOne
    @JoinColumn(name = "id_collaboratore")
    private CollaboratorePo collaboratorePo;

    @Column(nullable = false)
    private LocalDate dataPrenotazione;

    @Column(nullable = false)
    private LocalTime oraInizioPrenotazione;

    @Column(nullable = false)
    private LocalTime oraFinePrenotazione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatoPrenotazione statoPrenotazione;

    @Column(length = 500)
    private String notePrenotazione;

    @Column(nullable = false)
    private LocalDateTime dataCreazionePrenotazione;

    private LocalDateTime dataModificaPrenotazione;

    public PrenotazionePo() {
    }
}

