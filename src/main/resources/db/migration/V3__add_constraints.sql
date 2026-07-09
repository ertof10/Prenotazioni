ALTER TABLE utenti
    ADD CONSTRAINT uk_utenti_email
        UNIQUE (email_utente);

ALTER TABLE collaboratori
    ADD CONSTRAINT uk_collaboratori_email
        UNIQUE (email_collaboratore);

ALTER TABLE collaboratori_servizi
    ADD CONSTRAINT uk_collaboratori_servizi_collab_serv
        UNIQUE (id_collaboratore, id_servizio);

ALTER TABLE calendari
    ADD CONSTRAINT uk_calendari_collab_giorno_orario
        UNIQUE (id_collaboratore, giorno_settimana_calendario, ora_inizio_calendario, ora_fine_calendario);