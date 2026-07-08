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