CREATE TABLE account (
                         id_account INT AUTO_INCREMENT PRIMARY KEY,

                         email_account VARCHAR(120) NOT NULL,
                         password_account VARCHAR(255) NOT NULL,
                         ruolo_account VARCHAR(20) NOT NULL,
                         attivo_account BIT NOT NULL DEFAULT 1,

                         id_utente INT NULL,
                         id_collaboratore INT NULL,

                         data_creazione_account DATETIME NOT NULL,
                         data_modifica_account DATETIME NULL,

                         CONSTRAINT uk_account_email
                             UNIQUE (email_account),

                         CONSTRAINT uk_account_utente
                             UNIQUE (id_utente),

                         CONSTRAINT uk_account_collaboratore
                             UNIQUE (id_collaboratore),

                         CONSTRAINT fk_account_utente
                             FOREIGN KEY (id_utente)
                                 REFERENCES utenti (id_utente),

                         CONSTRAINT fk_account_collaboratore
                             FOREIGN KEY (id_collaboratore)
                                 REFERENCES collaboratori (id_collaboratore)
);