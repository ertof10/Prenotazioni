INSERT INTO account (
    email_account,
    password_account,
    ruolo_account,
    attivo_account,
    id_utente,
    id_collaboratore,
    data_creazione_account,
    data_modifica_account
)
VALUES (
           'admin@prenotazioni.local',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'ADMIN',
           1,
           NULL,
           NULL,
           NOW(),
           NULL
       );