package com.prenotazioni.Po;


import com.prenotazioni.Enums.AppRole;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "utenti")
@Data
public class UtentePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtente;

    @Column(nullable = false, length = 60)
    private String nomeUtente;

    @Column(nullable = false, length = 60)
    private String cognomeUtente;

    @Column(nullable = false, length = 120, unique = true)
    private String emailUtente;

    @Column(length = 20)
    private String telefonoUtente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppRole ruoloUtente;

    @Column(nullable = false)
    private Boolean attivoUtente = true;

    public UtentePo() {
    }
}
