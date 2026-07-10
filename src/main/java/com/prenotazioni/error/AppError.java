package com.prenotazioni.error;

import org.springframework.http.HttpStatus;

public enum AppError {

    // =========================
    // ERRORI GENERICI
    // =========================

    ID_NON_VALIDO(HttpStatus.BAD_REQUEST, "ID non valido"),
    EMAIL_NON_VALIDA(HttpStatus.BAD_REQUEST, "Email non valida"),
    EMAIL_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Email già presente"),
    RUOLO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Ruolo non valido"),
    CREDENZIALI_NON_VALIDE(HttpStatus.UNAUTHORIZED, "Credenziali non valide"),
    TOKEN_NON_VALIDO(HttpStatus.UNAUTHORIZED, "Token non valido"),
    TOKEN_SCADUTO(HttpStatus.UNAUTHORIZED, "Token scaduto"),
    ACCESSO_NEGATO(HttpStatus.FORBIDDEN, "Accesso negato"),
    ACCOUNT_NON_TROVATO(HttpStatus.NOT_FOUND, "Account non trovato"),
    ACCOUNT_NON_ATTIVO(HttpStatus.UNAUTHORIZED, "Account non attivo"),
    ACCOUNT_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Account già presente"),
    ACCOUNT_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione account fallita"),

    // =========================
    // UTENTE
    // =========================

    UTENTE_NON_TROVATO(HttpStatus.NOT_FOUND, "Utente non trovato"),
    UTENTE_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione utente fallita"),
    UTENTE_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica utente fallita"),
    UTENTE_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione utente fallita"),
    UTENTE_NON_ELIMINABILE(HttpStatus.BAD_REQUEST, "Utente non eliminabile perché esistono prenotazioni associate"),
    UTENTE_CON_PREVENTIVI_ASSOCIATI(HttpStatus.BAD_REQUEST, "Impossibile eliminare utente con preventivi associati"),
    UTENTE_NON_ATTIVO(HttpStatus.BAD_REQUEST, "Utente non attivo"),


    // =========================
    // SERVIZIO
    // =========================

    SERVIZIO_NON_TROVATO(HttpStatus.NOT_FOUND, "Servizio non trovato"),
    SERVIZIO_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Servizio già presente"),
    SERVIZIO_NON_ATTIVO(HttpStatus.BAD_REQUEST, "Il servizio selezionato non è attivo"),
    TIPO_GESTIONE_SERVIZIO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Tipo gestione servizio non valido"),
    SERVIZIO_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione servizio fallita"),
    SERVIZIO_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica servizio fallita"),
    SERVIZIO_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione servizio fallita"),
    SERVIZIO_NON_ELIMINABILE(HttpStatus.BAD_REQUEST, "Servizio non eliminabile perché esistono prenotazioni o associazioni collaboratore-servizio collegate"),
    SERVIZIO_CON_PREVENTIVI_ASSOCIATI(HttpStatus.BAD_REQUEST, "Impossibile eliminare servizio con preventivi associati"),


    // =========================
    // COLLABORATORE
    // =========================

    COLLABORATORE_NON_TROVATO(HttpStatus.NOT_FOUND, "Collaboratore non trovato"),
    COLLABORATORE_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Collaboratore già presente"),
    COLLABORATORE_NON_ATTIVO(HttpStatus.BAD_REQUEST, "Il collaboratore selezionato non è attivo"),
    COLLABORATORE_NON_DISPONIBILE(HttpStatus.BAD_REQUEST, "Il collaboratore non è disponibile nella fascia oraria selezionata"),
    COLLABORATORE_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione collaboratore fallita"),
    COLLABORATORE_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica collaboratore fallita"),
    COLLABORATORE_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione collaboratore fallita"),
    COLLABORATORE_NON_DISPONIBILE_DA_CALENDARIO(HttpStatus.BAD_REQUEST, "Il collaboratore non lavora nella giornata o fascia oraria selezionata"),
    COLLABORATORE_ASSENTE(HttpStatus.BAD_REQUEST, "Collaboratore assente nella data e fascia oraria richiesta"),
    COLLABORATORE_NON_ELIMINABILE(HttpStatus.BAD_REQUEST, "Collaboratore non eliminabile perché esistono prenotazioni, servizi, calendari o assenze associati"),
    SERVIZIO_A_POSTI_NON_CONFIGURATO(HttpStatus.BAD_REQUEST, "Servizio a posti non configurato correttamente"),
    POSTI_SERVIZIO_ESAURITI(HttpStatus.BAD_REQUEST, "Posti esauriti per il servizio nella fascia oraria richiesta"),
    COLLABORATORE_NON_PREVISTO_PER_SERVIZIO_A_POSTI(HttpStatus.BAD_REQUEST, "Collaboratore non previsto per un servizio a posti"),




    // =========================
    // COLLABORATORE SERVIZIO
    // =========================

    COLLABORATORE_SERVIZIO_NON_TROVATO(HttpStatus.NOT_FOUND, "Associazione collaboratore servizio non trovata"),
    COLLABORATORE_SERVIZIO_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Servizio già assegnato al collaboratore"),
    COLLABORATORE_NON_ABILITATO_AL_SERVIZIO(HttpStatus.BAD_REQUEST, "Il collaboratore non è abilitato al servizio selezionato"),
    COLLABORATORE_SERVIZIO_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione associazione collaboratore servizio fallita"),
    COLLABORATORE_SERVIZIO_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica associazione collaboratore servizio fallita"),
    COLLABORATORE_SERVIZIO_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione associazione collaboratore servizio fallita"),


    // =========================
    // PRENOTAZIONE
    // =========================

    PRENOTAZIONE_NON_TROVATA(HttpStatus.NOT_FOUND, "Prenotazione non trovata"),
    PRENOTAZIONE_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Esiste già una prenotazione in questa fascia oraria"),
    ORARIO_PRENOTAZIONE_NON_VALIDO(HttpStatus.BAD_REQUEST, "Orario prenotazione non valido"),
    DATA_PRENOTAZIONE_NON_VALIDA(HttpStatus.BAD_REQUEST, "La data prenotazione non può essere nel passato"),
    NESSUN_COLLABORATORE_DISPONIBILE(HttpStatus.BAD_REQUEST, "Nessun collaboratore disponibile per la giornata e la fascia oraria selezionata"),
    PRENOTAZIONE_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione prenotazione fallita"),
    PRENOTAZIONE_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica prenotazione fallita"),
    PRENOTAZIONE_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione prenotazione fallita"),
    PRENOTAZIONE_NON_VALIDA(HttpStatus.BAD_REQUEST, "Prenotazione non valida"),
    PRENOTAZIONE_NON_ANNULLABILE(HttpStatus.BAD_REQUEST, "Prenotazione non annullabile perché risulta già annullata o completata"),
    PRENOTAZIONE_NON_MODIFICABILE(HttpStatus.BAD_REQUEST, "Prenotazione non modificabile nello stato attuale"),


    // =========================
    // CALENDARIO
    // =========================

    CALENDARIO_NON_TROVATO(HttpStatus.NOT_FOUND, "Calendario non trovato"),
    CALENDARIO_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Esiste già una fascia calendario sovrapposta per questo collaboratore"),
    GIORNO_CALENDARIO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Giorno calendario non valido"),
    ORARIO_CALENDARIO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Orario calendario non valido"),
    CALENDARIO_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione calendario fallita"),
    CALENDARIO_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica calendario fallita"),
    CALENDARIO_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione calendario fallita"),


    // =====================
    // ASSENZA COLLABORATORE
    // =====================

    ASSENZA_COLLABORATORE_NON_TROVATA(HttpStatus.NOT_FOUND, "Assenza collaboratore non trovata"),
    ASSENZA_COLLABORATORE_GIA_PRESENTE(HttpStatus.BAD_REQUEST, "Esiste già un'assenza sovrapposta per questo collaboratore"),
    ASSENZA_COLLABORATORE_NON_VALIDA(HttpStatus.BAD_REQUEST, "Assenza collaboratore non valida"),
    ASSENZA_COLLABORATORE_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante creazione assenza collaboratore"),
    ASSENZA_COLLABORATORE_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante modifica assenza collaboratore"),
    ASSENZA_COLLABORATORE_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante eliminazione assenza collaboratore"),


    // =========================
    // PREVENTIVO
    // =========================

    PREVENTIVO_NON_TROVATO(HttpStatus.NOT_FOUND, "Preventivo non trovato"),
    PREVENTIVO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Preventivo non valido"),
    STATO_PREVENTIVO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Stato preventivo non valido"),
    IMPORTO_PREVENTIVO_NON_VALIDO(HttpStatus.BAD_REQUEST, "Importo preventivo non valido"),
    PREVENTIVO_NON_MODIFICABILE(HttpStatus.BAD_REQUEST, "Preventivo non modificabile nello stato attuale"),
    PREVENTIVO_NON_INVIABILE(HttpStatus.BAD_REQUEST, "Preventivo non inviabile nello stato attuale o senza importo valido"),
    PREVENTIVO_NON_ACCETTABILE(HttpStatus.BAD_REQUEST, "Preventivo accettabile solo se inviato"),
    PREVENTIVO_NON_RIFIUTABILE(HttpStatus.BAD_REQUEST, "Preventivo rifiutabile solo se inviato"),
    PREVENTIVO_CREAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Creazione preventivo fallita"),
    PREVENTIVO_MODIFICA_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Modifica preventivo fallita"),
    PREVENTIVO_ELIMINAZIONE_FALLITA(HttpStatus.INTERNAL_SERVER_ERROR, "Eliminazione preventivo fallita"),



    // =========================
    // ERRORI TECNICI / VALIDAZIONE
    // =========================

    ERRORE_VALIDAZIONE(HttpStatus.BAD_REQUEST, "Errore di validazione"),
    METODO_NON_SUPPORTATO(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP non supportato per questa operazione"),
    ERRORE_GENERICO(HttpStatus.INTERNAL_SERVER_ERROR, "Errore generico");


    private final HttpStatus status;
    private final String message;

    AppError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}