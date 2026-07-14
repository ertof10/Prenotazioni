CREATE TABLE utenti (
                        id_utente INT NOT NULL AUTO_INCREMENT,
                        attivo_utente BIT NOT NULL,
                        cognome_utente VARCHAR(60) NOT NULL,
                        email_utente VARCHAR(120) NOT NULL,
                        nome_utente VARCHAR(60) NOT NULL,
                        ruolo_utente VARCHAR(20) NOT NULL,
                        telefono_utente VARCHAR(20) DEFAULT NULL,
                        PRIMARY KEY (id_utente),
                        CONSTRAINT uk_utenti_email UNIQUE (email_utente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE collaboratori (
                               id_collaboratore INT NOT NULL AUTO_INCREMENT,
                               attivo_collaboratore BIT NOT NULL,
                               cognome_collaboratore VARCHAR(60) NOT NULL,
                               data_creazione_collaboratore DATETIME(6) NOT NULL,
                               data_modifica_collaboratore DATETIME(6) DEFAULT NULL,
                               descrizione_collaboratore VARCHAR(500) DEFAULT NULL,
                               email_collaboratore VARCHAR(120) NOT NULL,
                               nome_collaboratore VARCHAR(60) NOT NULL,
                               specializzazione_collaboratore VARCHAR(100) NOT NULL,
                               telefono_collaboratore VARCHAR(20) DEFAULT NULL,
                               PRIMARY KEY (id_collaboratore),
                               CONSTRAINT uk_collaboratori_email UNIQUE (email_collaboratore)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE servizi (
                         id_servizio INT NOT NULL AUTO_INCREMENT,
                         attivo_servizio BIT NOT NULL,
                         categoria_servizio VARCHAR(100) NOT NULL,
                         data_creazione_servizio DATETIME(6) NOT NULL,
                         data_modifica_servizio DATETIME(6) DEFAULT NULL,
                         descrizione_servizio VARCHAR(500) DEFAULT NULL,
                         durata_minuti_servizio INT NOT NULL,
                         nome_servizio VARCHAR(100) NOT NULL,
                         posti_massimi_servizio INT DEFAULT NULL,
                         prezzo_servizio DECIMAL(10,2) NOT NULL,
                         tipo_gestione_servizio VARCHAR(30) NOT NULL,
                         url_immagine_servizio VARCHAR(500) DEFAULT NULL,
                         PRIMARY KEY (id_servizio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE account (
                         id_account INT NOT NULL AUTO_INCREMENT,
                         attivo_account BIT NOT NULL,
                         data_creazione_account DATETIME(6) NOT NULL,
                         data_modifica_account DATETIME(6) DEFAULT NULL,
                         email_account VARCHAR(120) NOT NULL,
                         password_account VARCHAR(255) NOT NULL,
                         ruolo_account VARCHAR(20) NOT NULL,
                         id_collaboratore INT DEFAULT NULL,
                         id_utente INT DEFAULT NULL,
                         PRIMARY KEY (id_account),
                         CONSTRAINT uk_account_email UNIQUE (email_account),
                         CONSTRAINT uk_account_collaboratore UNIQUE (id_collaboratore),
                         CONSTRAINT uk_account_utente UNIQUE (id_utente),
                         CONSTRAINT fk_account_collaboratore
                             FOREIGN KEY (id_collaboratore)
                                 REFERENCES collaboratori (id_collaboratore),
                         CONSTRAINT fk_account_utente
                             FOREIGN KEY (id_utente)
                                 REFERENCES utenti (id_utente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE calendari (
                           id_calendario INT NOT NULL AUTO_INCREMENT,
                           attivo_calendario BIT NOT NULL,
                           data_creazione_calendario DATETIME(6) NOT NULL,
                           data_modifica_calendario DATETIME(6) DEFAULT NULL,
                           giorno_settimana_calendario VARCHAR(20) NOT NULL,
                           ora_fine_calendario TIME NOT NULL,
                           ora_inizio_calendario TIME NOT NULL,
                           id_collaboratore INT NOT NULL,
                           PRIMARY KEY (id_calendario),
                           CONSTRAINT uk_calendari_collab_giorno_orario
                               UNIQUE (id_collaboratore, giorno_settimana_calendario, ora_inizio_calendario, ora_fine_calendario),
                           CONSTRAINT fk_calendari_collaboratore
                               FOREIGN KEY (id_collaboratore)
                                   REFERENCES collaboratori (id_collaboratore)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE collaboratori_servizi (
                                       id_collaboratore_servizio INT NOT NULL AUTO_INCREMENT,
                                       attivo_collaboratore_servizio BIT NOT NULL,
                                       data_creazione_collaboratore_servizio DATETIME(6) NOT NULL,
                                       data_modifica_collaboratore_servizio DATETIME(6) DEFAULT NULL,
                                       durata_personalizzata_minuti INT DEFAULT NULL,
                                       note_collaboratore_servizio VARCHAR(500) DEFAULT NULL,
                                       prezzo_personalizzato DECIMAL(10,2) DEFAULT NULL,
                                       id_collaboratore INT NOT NULL,
                                       id_servizio INT NOT NULL,
                                       PRIMARY KEY (id_collaboratore_servizio),
                                       CONSTRAINT uk_collaboratori_servizi_collab_serv
                                           UNIQUE (id_collaboratore, id_servizio),
                                       CONSTRAINT fk_collaboratori_servizi_collaboratore
                                           FOREIGN KEY (id_collaboratore)
                                               REFERENCES collaboratori (id_collaboratore),
                                       CONSTRAINT fk_collaboratori_servizi_servizio
                                           FOREIGN KEY (id_servizio)
                                               REFERENCES servizi (id_servizio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE assenze_collaboratori (
                                       id_assenza_collaboratore INT NOT NULL AUTO_INCREMENT,
                                       attivo_assenza_collaboratore BIT NOT NULL,
                                       data_creazione_assenza_collaboratore DATETIME(6) DEFAULT NULL,
                                       data_fine_assenza DATE NOT NULL,
                                       data_inizio_assenza DATE NOT NULL,
                                       data_modifica_assenza_collaboratore DATETIME(6) DEFAULT NULL,
                                       motivo_assenza_collaboratore VARCHAR(255) DEFAULT NULL,
                                       ora_fine_assenza TIME NOT NULL,
                                       ora_inizio_assenza TIME NOT NULL,
                                       id_collaboratore INT NOT NULL,
                                       PRIMARY KEY (id_assenza_collaboratore),
                                       CONSTRAINT fk_assenze_collaboratori_collaboratore
                                           FOREIGN KEY (id_collaboratore)
                                               REFERENCES collaboratori (id_collaboratore)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE prenotazioni (
                              id_prenotazione INT NOT NULL AUTO_INCREMENT,
                              data_creazione_prenotazione DATETIME(6) NOT NULL,
                              data_modifica_prenotazione DATETIME(6) DEFAULT NULL,
                              data_prenotazione DATE NOT NULL,
                              note_prenotazione VARCHAR(500) DEFAULT NULL,
                              ora_fine_prenotazione TIME NOT NULL,
                              ora_inizio_prenotazione TIME NOT NULL,
                              stato_prenotazione VARCHAR(30) NOT NULL,
                              id_collaboratore INT DEFAULT NULL,
                              id_servizio INT NOT NULL,
                              id_utente INT NOT NULL,
                              PRIMARY KEY (id_prenotazione),
                              CONSTRAINT fk_prenotazioni_collaboratore
                                  FOREIGN KEY (id_collaboratore)
                                      REFERENCES collaboratori (id_collaboratore),
                              CONSTRAINT fk_prenotazioni_servizio
                                  FOREIGN KEY (id_servizio)
                                      REFERENCES servizi (id_servizio),
                              CONSTRAINT fk_prenotazioni_utente
                                  FOREIGN KEY (id_utente)
                                      REFERENCES utenti (id_utente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE preventivi (
                            id_preventivo INT NOT NULL AUTO_INCREMENT,
                            data_creazione_preventivo DATETIME(6) NOT NULL,
                            data_modifica_preventivo DATETIME(6) DEFAULT NULL,
                            data_scadenza_preventivo DATE DEFAULT NULL,
                            descrizione_richiesta_preventivo VARCHAR(2000) NOT NULL,
                            importo_preventivo DECIMAL(10,2) DEFAULT NULL,
                            risposta_preventivo VARCHAR(2000) DEFAULT NULL,
                            stato_preventivo VARCHAR(30) NOT NULL,
                            titolo_preventivo VARCHAR(150) NOT NULL,
                            id_servizio INT DEFAULT NULL,
                            id_utente INT NOT NULL,
                            PRIMARY KEY (id_preventivo),
                            CONSTRAINT fk_preventivi_servizio
                                FOREIGN KEY (id_servizio)
                                    REFERENCES servizi (id_servizio),
                            CONSTRAINT fk_preventivi_utente
                                FOREIGN KEY (id_utente)
                                    REFERENCES utenti (id_utente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE INDEX idx_prenotazioni_utente_stato_data_orario
    ON prenotazioni (
                     id_utente,
                     stato_prenotazione,
                     data_prenotazione,
                     ora_inizio_prenotazione,
                     ora_fine_prenotazione
        );

CREATE INDEX idx_prenotazioni_collab_stato_data_orario
    ON prenotazioni (
                     id_collaboratore,
                     stato_prenotazione,
                     data_prenotazione,
                     ora_inizio_prenotazione,
                     ora_fine_prenotazione
        );

CREATE INDEX idx_prenotazioni_servizio_stato_data_orario
    ON prenotazioni (
                     id_servizio,
                     stato_prenotazione,
                     data_prenotazione,
                     ora_inizio_prenotazione,
                     ora_fine_prenotazione
        );

CREATE INDEX idx_prenotazioni_stato
    ON prenotazioni (
                     stato_prenotazione
        );

CREATE INDEX idx_calendari_collab_attivo_giorno_orario
    ON calendari (
                  id_collaboratore,
                  attivo_calendario,
                  giorno_settimana_calendario,
                  ora_inizio_calendario,
                  ora_fine_calendario
        );

CREATE INDEX idx_assenze_collab_attivo_date_orario
    ON assenze_collaboratori (
                              id_collaboratore,
                              attivo_assenza_collaboratore,
                              data_inizio_assenza,
                              data_fine_assenza,
                              ora_inizio_assenza,
                              ora_fine_assenza
        );

CREATE INDEX idx_collab_serv_collab_serv_attivo
    ON collaboratori_servizi (
                              id_collaboratore,
                              id_servizio,
                              attivo_collaboratore_servizio
        );

CREATE INDEX idx_collab_serv_serv_attivo_collab
    ON collaboratori_servizi (
                              id_servizio,
                              attivo_collaboratore_servizio,
                              id_collaboratore
        );