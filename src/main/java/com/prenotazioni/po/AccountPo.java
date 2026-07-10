package com.prenotazioni.po;

import com.prenotazioni.enums.AppRole;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Data
public class AccountPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_account")
    private Integer idAccount;

    @Column(name = "email_account", nullable = false, length = 120, unique = true)
    private String emailAccount;

    @Column(name = "password_account", nullable = false, length = 255)
    private String passwordAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo_account", nullable = false, length = 20)
    private AppRole ruoloAccount;

    @Column(name = "attivo_account", nullable = false)
    private Boolean attivoAccount = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private UtentePo utentePo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_collaboratore")
    private CollaboratorePo collaboratorePo;

    @Column(name = "data_creazione_account", nullable = false)
    private LocalDateTime dataCreazioneAccount;

    @Column(name = "data_modifica_account")
    private LocalDateTime dataModificaAccount;

    public AccountPo() {  // costruttore vuoto per jpa
    }
}