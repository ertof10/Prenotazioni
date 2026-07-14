UPDATE account
SET password_account = '$2a$10$vntaxRElHCmo9l./MAZdd.NcF.yYwVUI9n2r8cUoBxSY2tjjwLVH2',
    attivo_account = 1,
    ruolo_account = 'ADMIN',
    data_modifica_account = NOW()
WHERE email_account = 'admin@prenotazioni.local';