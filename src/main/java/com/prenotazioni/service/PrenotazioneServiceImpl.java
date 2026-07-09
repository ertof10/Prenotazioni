package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.enums.TipoGestioneServizio;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.PrenotazioneMapper;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.CollaboratoreServizioPo;
import com.prenotazioni.po.PrenotazionePo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private static final Logger log = LoggerFactory.getLogger(PrenotazioneServiceImpl.class);

    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ServizioRepository servizioRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final PrenotazioneMapper prenotazioneMapper;
    private final CalendarioRepository calendarioRepository;
    private final AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;
    private final Clock clock;

    public PrenotazioneServiceImpl(PrenotazioneRepository prenotazioneRepository,
                                   UtenteRepository utenteRepository,
                                   ServizioRepository servizioRepository,
                                   CollaboratoreRepository collaboratoreRepository,
                                   CollaboratoreServizioRepository collaboratoreServizioRepository,
                                   PrenotazioneMapper prenotazioneMapper,
                                   CalendarioRepository calendarioRepository,
                                   AssenzaCollaboratoreRepository assenzaCollaboratoreRepository,
                                   Clock clock) {

        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.servizioRepository = servizioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.prenotazioneMapper = prenotazioneMapper;
        this.calendarioRepository = calendarioRepository;
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PrenotazioneTo saveOrUpdatePrenotazione(PrenotazioneTo prenotazioneTo) {

        log.info("Avvio salvataggio prenotazione | idPrenotazione={} | idUtente={} | idServizio={} | idCollaboratore={} | data={}",
                prenotazioneTo != null ? prenotazioneTo.getIdPrenotazione() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdUtente() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdServizio() : null,
                prenotazioneTo != null ? prenotazioneTo.getIdCollaboratore() : null,
                prenotazioneTo != null ? prenotazioneTo.getDataPrenotazione() : null);

        validaDatiPrenotazione(prenotazioneTo);

        UtentePo utentePo = recuperaUtenteAttivo(prenotazioneTo.getIdUtente());

        ServizioPo servizioPo = recuperaServizioAttivo(prenotazioneTo.getIdServizio());

        PrenotazionePo prenotazioneEsistentePo =
                recuperaPrenotazioneEsistenteSeModifica(prenotazioneTo.getIdPrenotazione());

        verificaSovrapposizioneUtente(prenotazioneTo);

        if (servizioPo.getTipoGestioneServizio() == TipoGestioneServizio.A_POSTI) {
            log.info("Gestione prenotazione servizio A_POSTI | idServizio={} | postiMassimi={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getPostiMassimiServizio());

            return gestisciPrenotazioneAPosti(
                    prenotazioneTo,
                    utentePo,
                    servizioPo,
                    prenotazioneEsistentePo
            );
        }

        log.info("Gestione prenotazione servizio CON_COLLABORATORE | idServizio={} | idCollaboratoreRichiesto={}",
                servizioPo.getIdServizio(),
                prenotazioneTo.getIdCollaboratore());

        return gestisciPrenotazioneConCollaboratore(
                prenotazioneTo,
                utentePo,
                servizioPo,
                prenotazioneEsistentePo
        );
    }

    private void validaDatiPrenotazione(PrenotazioneTo prenotazioneTo) {

        if (prenotazioneTo == null) {
            log.warn("Prenotazione bloccata: request nulla");
            throw new ServiceException(AppError.PRENOTAZIONE_NON_VALIDA);
        }

        if (prenotazioneTo.getIdUtente() == null
                || prenotazioneTo.getIdUtente() <= 0
                || prenotazioneTo.getIdServizio() == null
                || prenotazioneTo.getIdServizio() <= 0) {

            log.warn("Prenotazione bloccata: id utente o servizio non valido | idUtente={} | idServizio={}",
                    prenotazioneTo.getIdUtente(),
                    prenotazioneTo.getIdServizio());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (prenotazioneTo.getDataPrenotazione() == null
                || prenotazioneTo.getOraInizioPrenotazione() == null
                || prenotazioneTo.getOraFinePrenotazione() == null) {

            log.warn("Prenotazione bloccata: data o orari mancanti | idUtente={} | idServizio={}",
                    prenotazioneTo.getIdUtente(),
                    prenotazioneTo.getIdServizio());

            throw new ServiceException(AppError.PRENOTAZIONE_NON_VALIDA);
        }

        if (prenotazioneTo.getOraFinePrenotazione().isBefore(prenotazioneTo.getOraInizioPrenotazione())
                || prenotazioneTo.getOraFinePrenotazione().equals(prenotazioneTo.getOraInizioPrenotazione())) {

            log.warn("Prenotazione bloccata: orario non valido | oraInizio={} | oraFine={}",
                    prenotazioneTo.getOraInizioPrenotazione(),
                    prenotazioneTo.getOraFinePrenotazione());

            throw new ServiceException(AppError.ORARIO_PRENOTAZIONE_NON_VALIDO);
        }

        if (prenotazioneTo.getDataPrenotazione().isBefore(LocalDate.now(clock))) {
            log.warn("Prenotazione bloccata: data nel passato | dataPrenotazione={}",
                    prenotazioneTo.getDataPrenotazione());

            throw new ServiceException(AppError.DATA_PRENOTAZIONE_NON_VALIDA);
        }
    }

    private UtentePo recuperaUtenteAttivo(Integer idUtente) {

        UtentePo utentePo = recuperaUtenteConLock(idUtente);

        if (!Boolean.TRUE.equals(utentePo.getAttivoUtente())) {
            log.warn("Prenotazione bloccata: utente non attivo | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_ATTIVO);
        }

        return utentePo;
    }

    private UtentePo recuperaUtenteConLock(Integer idUtente) {

        return utenteRepository.findByIdForUpdate(idUtente)
                .orElseThrow(() -> {
                    log.warn("Prenotazione bloccata: utente non trovato | idUtente={}", idUtente);
                    return new ServiceException(AppError.UTENTE_NON_TROVATO);
                });
    }

    private ServizioPo recuperaServizioAttivo(Integer idServizio) {

        ServizioPo servizioPo = recuperaServizioConLock(idServizio);

        if (!Boolean.TRUE.equals(servizioPo.getAttivoServizio())) {
            log.warn("Prenotazione bloccata: servizio non attivo | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_ATTIVO);
        }

        if (servizioPo.getTipoGestioneServizio() == null) {
            log.warn("Prenotazione bloccata: tipo gestione servizio nullo | idServizio={}", idServizio);
            throw new ServiceException(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO);
        }

        return servizioPo;
    }

    private ServizioPo recuperaServizioConLock(Integer idServizio) {

        return servizioRepository.findByIdForUpdate(idServizio)
                .orElseThrow(() -> {
                    log.warn("Prenotazione bloccata: servizio non trovato | idServizio={}", idServizio);
                    return new ServiceException(AppError.SERVIZIO_NON_TROVATO);
                });
    }

    private PrenotazionePo recuperaPrenotazioneConLock(Integer idPrenotazione) {

        return prenotazioneRepository.findByIdForUpdate(idPrenotazione)
                .orElseThrow(() -> {
                    log.warn("Prenotazione non trovata | idPrenotazione={}", idPrenotazione);
                    return new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
                });
    }

    private CollaboratorePo recuperaCollaboratoreConLock(Integer idCollaboratore) {

        return collaboratoreRepository.findByIdForUpdate(idCollaboratore)
                .orElseThrow(() -> {
                    log.warn("Collaboratore non trovato | idCollaboratore={}", idCollaboratore);
                    return new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
                });
    }

    private PrenotazionePo recuperaPrenotazioneEsistenteSeModifica(Integer idPrenotazione) {

        if (idPrenotazione == null) {
            return null;
        }

        if (idPrenotazione <= 0) {
            log.warn("Modifica prenotazione bloccata: id non valido | idPrenotazione={}", idPrenotazione);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazioneEsistentePo = recuperaPrenotazioneConLock(idPrenotazione);

        if (prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {

            log.warn("Modifica prenotazione bloccata: stato non modificabile | idPrenotazione={} | stato={}",
                    idPrenotazione,
                    prenotazioneEsistentePo.getStatoPrenotazione());

            throw new ServiceException(AppError.PRENOTAZIONE_NON_MODIFICABILE);
        }

        return prenotazioneEsistentePo;
    }

    private void verificaSovrapposizioneUtente(PrenotazioneTo prenotazioneTo) {

        boolean prenotazioneSovrappostaUtente =
                prenotazioneRepository.existsPrenotazioneSovrappostaUtente(
                        prenotazioneTo.getIdPrenotazione(),
                        prenotazioneTo.getIdUtente(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (prenotazioneSovrappostaUtente) {
            log.warn("Prenotazione bloccata: utente già occupato nella fascia richiesta | idUtente={} | data={} | oraInizio={} | oraFine={}",
                    prenotazioneTo.getIdUtente(),
                    prenotazioneTo.getDataPrenotazione(),
                    prenotazioneTo.getOraInizioPrenotazione(),
                    prenotazioneTo.getOraFinePrenotazione());

            throw new ServiceException(AppError.PRENOTAZIONE_GIA_PRESENTE);
        }
    }

    private PrenotazioneTo gestisciPrenotazioneAPosti(PrenotazioneTo prenotazioneTo,
                                                      UtentePo utentePo,
                                                      ServizioPo servizioPo,
                                                      PrenotazionePo prenotazioneEsistentePo) {

        validaServizioAPosti(prenotazioneTo, servizioPo);

        verificaPostiDisponibiliServizioAPosti(prenotazioneTo, servizioPo);

        PrenotazionePo prenotazionePo = preparaPrenotazione(
                prenotazioneTo,
                utentePo,
                servizioPo,
                null,
                prenotazioneEsistentePo
        );

        return salvaPrenotazione(prenotazionePo, prenotazioneTo.getIdPrenotazione() == null);
    }

    private void validaServizioAPosti(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo) {

        if (prenotazioneTo.getIdCollaboratore() != null) {
            log.warn("Prenotazione A_POSTI bloccata: collaboratore non previsto | idServizio={} | idCollaboratore={}",
                    servizioPo.getIdServizio(),
                    prenotazioneTo.getIdCollaboratore());

            throw new ServiceException(AppError.COLLABORATORE_NON_PREVISTO_PER_SERVIZIO_A_POSTI);
        }

        if (servizioPo.getPostiMassimiServizio() == null || servizioPo.getPostiMassimiServizio() <= 0) {
            log.warn("Prenotazione A_POSTI bloccata: servizio non configurato | idServizio={} | postiMassimi={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getPostiMassimiServizio());

            throw new ServiceException(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO);
        }
    }

    private void verificaPostiDisponibiliServizioAPosti(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo) {

        Long postiOccupati =
                prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(
                        prenotazioneTo.getIdPrenotazione(),
                        servizioPo.getIdServizio(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (postiOccupati >= servizioPo.getPostiMassimiServizio()) {
            log.warn("Prenotazione A_POSTI bloccata: posti esauriti | idServizio={} | postiOccupati={} | postiMassimi={} | data={}",
                    servizioPo.getIdServizio(),
                    postiOccupati,
                    servizioPo.getPostiMassimiServizio(),
                    prenotazioneTo.getDataPrenotazione());

            throw new ServiceException(AppError.POSTI_SERVIZIO_ESAURITI);
        }

        log.info("Posti disponibili verificati | idServizio={} | postiOccupati={} | postiMassimi={}",
                servizioPo.getIdServizio(),
                postiOccupati,
                servizioPo.getPostiMassimiServizio());
    }

    private PrenotazioneTo gestisciPrenotazioneConCollaboratore(PrenotazioneTo prenotazioneTo,
                                                                UtentePo utentePo,
                                                                ServizioPo servizioPo,
                                                                PrenotazionePo prenotazioneEsistentePo) {

        DayOfWeek giornoSettimanaPrenotazione = prenotazioneTo.getDataPrenotazione().getDayOfWeek();

        CollaboratorePo collaboratorePo =
                selezionaCollaboratore(prenotazioneTo, servizioPo, giornoSettimanaPrenotazione);

        verificaCollaboratoreAssente(collaboratorePo, prenotazioneTo);

        PrenotazionePo prenotazionePo = preparaPrenotazione(
                prenotazioneTo,
                utentePo,
                servizioPo,
                collaboratorePo,
                prenotazioneEsistentePo
        );

        return salvaPrenotazione(prenotazionePo, prenotazioneTo.getIdPrenotazione() == null);
    }

    private CollaboratorePo selezionaCollaboratore(PrenotazioneTo prenotazioneTo,
                                                   ServizioPo servizioPo,
                                                   DayOfWeek giornoSettimanaPrenotazione) {

        if (prenotazioneTo.getIdCollaboratore() != null) {
            log.info("Selezione collaboratore manuale | idCollaboratore={} | idServizio={}",
                    prenotazioneTo.getIdCollaboratore(),
                    servizioPo.getIdServizio());

            return recuperaEValidaCollaboratoreManuale(
                    prenotazioneTo,
                    servizioPo,
                    giornoSettimanaPrenotazione
            );
        }

        log.info("Selezione collaboratore automatica | idServizio={}", servizioPo.getIdServizio());

        return selezionaCollaboratoreAutomatico(
                prenotazioneTo,
                servizioPo,
                giornoSettimanaPrenotazione
        );
    }

    private CollaboratorePo recuperaEValidaCollaboratoreManuale(PrenotazioneTo prenotazioneTo,
                                                                ServizioPo servizioPo,
                                                                DayOfWeek giornoSettimanaPrenotazione) {

        CollaboratorePo collaboratorePo =
                recuperaCollaboratoreConLock(prenotazioneTo.getIdCollaboratore());

        if (!Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore())) {
            log.warn("Collaboratore manuale non attivo | idCollaboratore={}",
                    collaboratorePo.getIdCollaboratore());

            throw new ServiceException(AppError.COLLABORATORE_NON_ATTIVO);
        }

        verificaCollaboratoreAbilitatoAlServizio(collaboratorePo, servizioPo);

        verificaCollaboratoreDisponibileDaCalendario(
                collaboratorePo,
                giornoSettimanaPrenotazione,
                prenotazioneTo
        );

        verificaSovrapposizioneCollaboratore(collaboratorePo, prenotazioneTo);

        return collaboratorePo;
    }

    private void verificaCollaboratoreAbilitatoAlServizio(CollaboratorePo collaboratorePo,
                                                          ServizioPo servizioPo) {

        boolean collaboratoreAbilitato =
                collaboratoreServizioRepository.existsAssociazioneAttiva(
                        collaboratorePo.getIdCollaboratore(),
                        servizioPo.getIdServizio()
                );

        if (!collaboratoreAbilitato) {
            log.warn("Collaboratore non abilitato al servizio | idCollaboratore={} | idServizio={}",
                    collaboratorePo.getIdCollaboratore(),
                    servizioPo.getIdServizio());

            throw new ServiceException(AppError.COLLABORATORE_NON_ABILITATO_AL_SERVIZIO);
        }
    }

    private void verificaCollaboratoreDisponibileDaCalendario(CollaboratorePo collaboratorePo,
                                                              DayOfWeek giornoSettimanaPrenotazione,
                                                              PrenotazioneTo prenotazioneTo) {

        boolean collaboratoreDisponibileDaCalendario =
                calendarioRepository.existsDisponibilitaCalendario(
                        collaboratorePo.getIdCollaboratore(),
                        giornoSettimanaPrenotazione,
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (!collaboratoreDisponibileDaCalendario) {
            log.warn("Collaboratore non disponibile da calendario | idCollaboratore={} | giorno={} | oraInizio={} | oraFine={}",
                    collaboratorePo.getIdCollaboratore(),
                    giornoSettimanaPrenotazione,
                    prenotazioneTo.getOraInizioPrenotazione(),
                    prenotazioneTo.getOraFinePrenotazione());

            throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE_DA_CALENDARIO);
        }
    }

    private void verificaSovrapposizioneCollaboratore(CollaboratorePo collaboratorePo,
                                                      PrenotazioneTo prenotazioneTo) {

        boolean collaboratoreSovrapposto =
                prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(
                        prenotazioneTo.getIdPrenotazione(),
                        collaboratorePo.getIdCollaboratore(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (collaboratoreSovrapposto) {
            log.warn("Collaboratore già occupato nella fascia richiesta | idCollaboratore={} | data={} | oraInizio={} | oraFine={}",
                    collaboratorePo.getIdCollaboratore(),
                    prenotazioneTo.getDataPrenotazione(),
                    prenotazioneTo.getOraInizioPrenotazione(),
                    prenotazioneTo.getOraFinePrenotazione());

            throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE);
        }
    }

    private CollaboratorePo selezionaCollaboratoreAutomatico(PrenotazioneTo prenotazioneTo,
                                                             ServizioPo servizioPo,
                                                             DayOfWeek giornoSettimanaPrenotazione) {

        List<CollaboratoreServizioPo> collaboratoriServizi =
                collaboratoreServizioRepository
                        .findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(
                                servizioPo.getIdServizio()
                        );

        List<CollaboratoreServizioPo> collaboratoriServiziOrdinati =
                collaboratoriServizi.stream()
                        .sorted(Comparator.comparing(collaboratoreServizioPo ->
                                collaboratoreServizioPo.getCollaboratorePo().getIdCollaboratore()))
                        .collect(Collectors.toList());

        log.info("Collaboratori candidati per assegnazione automatica | idServizio={} | candidati={}",
                servizioPo.getIdServizio(),
                collaboratoriServiziOrdinati.size());

        CollaboratorePo collaboratoreSelezionatoPo = null;
        Long caricoMinore = null;

        for (CollaboratoreServizioPo collaboratoreServizioPo : collaboratoriServiziOrdinati) {
            CollaboratorePo collaboratoreCandidatoPo =
                    recuperaCollaboratoreConLock(
                            collaboratoreServizioPo.getCollaboratorePo().getIdCollaboratore()
                    );

            if (!collaboratoreCandidatoValido(
                    collaboratoreCandidatoPo,
                    giornoSettimanaPrenotazione,
                    prenotazioneTo)) {
                continue;
            }

            Long caricoCollaboratore =
                    prenotazioneRepository.countPrenotazioniCollaboratorePerData(
                            prenotazioneTo.getIdPrenotazione(),
                            collaboratoreCandidatoPo.getIdCollaboratore(),
                            prenotazioneTo.getDataPrenotazione()
                    );

            if (collaboratoreSelezionatoPo == null || caricoCollaboratore < caricoMinore) {
                collaboratoreSelezionatoPo = collaboratoreCandidatoPo;
                caricoMinore = caricoCollaboratore;
            }
        }

        if (collaboratoreSelezionatoPo == null) {
            log.warn("Assegnazione automatica fallita: nessun collaboratore disponibile | idServizio={} | data={} | oraInizio={} | oraFine={}",
                    servizioPo.getIdServizio(),
                    prenotazioneTo.getDataPrenotazione(),
                    prenotazioneTo.getOraInizioPrenotazione(),
                    prenotazioneTo.getOraFinePrenotazione());

            throw new ServiceException(AppError.NESSUN_COLLABORATORE_DISPONIBILE);
        }

        log.info("Collaboratore assegnato automaticamente | idCollaboratore={} | idServizio={} | data={} | caricoGiornaliero={}",
                collaboratoreSelezionatoPo.getIdCollaboratore(),
                servizioPo.getIdServizio(),
                prenotazioneTo.getDataPrenotazione(),
                caricoMinore);

        return collaboratoreSelezionatoPo;
    }

    private boolean collaboratoreCandidatoValido(CollaboratorePo collaboratoreCandidatoPo,
                                                 DayOfWeek giornoSettimanaPrenotazione,
                                                 PrenotazioneTo prenotazioneTo) {

        if (!Boolean.TRUE.equals(collaboratoreCandidatoPo.getAttivoCollaboratore())) {
            log.info("Collaboratore candidato scartato: non attivo | idCollaboratore={}",
                    collaboratoreCandidatoPo.getIdCollaboratore());
            return false;
        }

        boolean collaboratoreAssente =
                assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(
                        collaboratoreCandidatoPo.getIdCollaboratore(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (collaboratoreAssente) {
            log.info("Collaboratore candidato scartato: assente | idCollaboratore={} | data={}",
                    collaboratoreCandidatoPo.getIdCollaboratore(),
                    prenotazioneTo.getDataPrenotazione());
            return false;
        }

        boolean collaboratoreDisponibileDaCalendario =
                calendarioRepository.existsDisponibilitaCalendario(
                        collaboratoreCandidatoPo.getIdCollaboratore(),
                        giornoSettimanaPrenotazione,
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (!collaboratoreDisponibileDaCalendario) {
            log.info("Collaboratore candidato scartato: fuori calendario | idCollaboratore={} | giorno={}",
                    collaboratoreCandidatoPo.getIdCollaboratore(),
                    giornoSettimanaPrenotazione);
            return false;
        }

        boolean collaboratoreSovrapposto =
                prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(
                        prenotazioneTo.getIdPrenotazione(),
                        collaboratoreCandidatoPo.getIdCollaboratore(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (collaboratoreSovrapposto) {
            log.info("Collaboratore candidato scartato: già occupato | idCollaboratore={} | data={}",
                    collaboratoreCandidatoPo.getIdCollaboratore(),
                    prenotazioneTo.getDataPrenotazione());
        }

        return !collaboratoreSovrapposto;
    }

    private void verificaCollaboratoreAssente(CollaboratorePo collaboratorePo,
                                              PrenotazioneTo prenotazioneTo) {

        boolean collaboratoreAssente =
                assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(
                        collaboratorePo.getIdCollaboratore(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (collaboratoreAssente) {
            log.warn("Prenotazione bloccata: collaboratore assente | idCollaboratore={} | data={}",
                    collaboratorePo.getIdCollaboratore(),
                    prenotazioneTo.getDataPrenotazione());

            throw new ServiceException(AppError.COLLABORATORE_ASSENTE);
        }
    }

    private PrenotazionePo preparaPrenotazione(PrenotazioneTo prenotazioneTo,
                                               UtentePo utentePo,
                                               ServizioPo servizioPo,
                                               CollaboratorePo collaboratorePo,
                                               PrenotazionePo prenotazioneEsistentePo) {

        boolean nuovaPrenotazione = prenotazioneTo.getIdPrenotazione() == null;

        PrenotazionePo prenotazionePo =
                nuovaPrenotazione ? new PrenotazionePo() : prenotazioneEsistentePo;

        prenotazionePo.setUtentePo(utentePo);
        prenotazionePo.setServizioPo(servizioPo);
        prenotazionePo.setCollaboratorePo(collaboratorePo);
        prenotazionePo.setDataPrenotazione(prenotazioneTo.getDataPrenotazione());
        prenotazionePo.setOraInizioPrenotazione(prenotazioneTo.getOraInizioPrenotazione());
        prenotazionePo.setOraFinePrenotazione(prenotazioneTo.getOraFinePrenotazione());
        prenotazionePo.setNotePrenotazione(prenotazioneTo.getNotePrenotazione());
        prenotazionePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);

        if (nuovaPrenotazione) {
            prenotazionePo.setDataCreazionePrenotazione(LocalDateTime.now(clock));
        } else {
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));
        }

        return prenotazionePo;
    }

    private PrenotazioneTo salvaPrenotazione(PrenotazionePo prenotazionePo,
                                             boolean nuovaPrenotazione) {

        try {
            prenotazionePo = prenotazioneRepository.save(prenotazionePo);

            log.info("{} prenotazione completata | idPrenotazione={} | idUtente={} | idServizio={} | idCollaboratore={} | stato={}",
                    nuovaPrenotazione ? "Creazione" : "Modifica",
                    prenotazionePo.getIdPrenotazione(),
                    prenotazionePo.getUtentePo().getIdUtente(),
                    prenotazionePo.getServizioPo().getIdServizio(),
                    prenotazionePo.getCollaboratorePo() != null
                            ? prenotazionePo.getCollaboratorePo().getIdCollaboratore()
                            : null,
                    prenotazionePo.getStatoPrenotazione());

            return prenotazioneMapper.toDto(prenotazionePo);

        } catch (Exception e) {
            log.error("{} prenotazione fallita per errore tecnico | idPrenotazione={} | idUtente={} | idServizio={}",
                    nuovaPrenotazione ? "Creazione" : "Modifica",
                    prenotazionePo.getIdPrenotazione(),
                    prenotazionePo.getUtentePo() != null ? prenotazionePo.getUtentePo().getIdUtente() : null,
                    prenotazionePo.getServizioPo() != null ? prenotazionePo.getServizioPo().getIdServizio() : null,
                    e);

            if (nuovaPrenotazione) {
                throw new ServiceException(AppError.PRENOTAZIONE_CREAZIONE_FALLITA, e);
            }

            throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public PrenotazioneTo getPrenotazioneById(Integer idPrenotazione) {

        log.info("Ricerca prenotazione per id | idPrenotazione={}", idPrenotazione);

        if (idPrenotazione == null || idPrenotazione <= 0) {
            log.warn("Ricerca prenotazione bloccata: id non valido | idPrenotazione={}", idPrenotazione);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazionePo = prenotazioneRepository.findById(idPrenotazione).orElse(null);

        if (prenotazionePo == null) {
            log.warn("Ricerca prenotazione fallita: prenotazione non trovata | idPrenotazione={}", idPrenotazione);
            throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
        }

        log.info("Ricerca prenotazione completata | idPrenotazione={} | stato={}",
                prenotazionePo.getIdPrenotazione(),
                prenotazionePo.getStatoPrenotazione());

        return prenotazioneMapper.toDto(prenotazionePo);
    }

    @Override
    public Page<PrenotazioneTo> getAllPrenotazioni(Pageable pageable) {

        log.info("Ricerca prenotazioni paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<PrenotazioneTo> risultato = prenotazioneRepository.findAll(pageable)
                .map(prenotazioneMapper::toDto);

        log.info("Ricerca prenotazioni completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public List<PrenotazioneTo> getPrenotazioniByUtente(Integer idUtente) {

        log.info("Ricerca prenotazioni per utente | idUtente={}", idUtente);

        List<PrenotazioneTo> risultato =
                prenotazioneRepository.findByUtentePo_IdUtente(idUtente)
                        .stream()
                        .map(prenotazioneMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca prenotazioni per utente completata | idUtente={} | elementi={}",
                idUtente,
                risultato.size());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deletePrenotazione(Integer idPrenotazione) {

        log.info("Avvio eliminazione prenotazione | idPrenotazione={}", idPrenotazione);

        if (idPrenotazione == null || idPrenotazione <= 0) {
            log.warn("Eliminazione prenotazione bloccata: id non valido | idPrenotazione={}", idPrenotazione);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        recuperaPrenotazioneConLock(idPrenotazione);

        try {
            prenotazioneRepository.deleteById(idPrenotazione);

            log.info("Eliminazione prenotazione completata | idPrenotazione={}", idPrenotazione);

            return new EsitoResponse("Prenotazione eliminata correttamente");

        } catch (Exception e) {
            log.error("Eliminazione prenotazione fallita per errore tecnico | idPrenotazione={}",
                    idPrenotazione,
                    e);

            throw new ServiceException(AppError.PRENOTAZIONE_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PrenotazioneTo utenteAnnullaPrenotazione(Integer idPrenotazione) {

        log.info("Avvio annullamento prenotazione da utente | idPrenotazione={}", idPrenotazione);

        return annullaPrenotazione(
                idPrenotazione,
                StatoPrenotazione.ANNULLATA_DA_UTENTE,
                "utente"
        );
    }

    @Override
    @Transactional
    public PrenotazioneTo strutturaAnnullaPrenotazione(Integer idPrenotazione) {

        log.info("Avvio annullamento prenotazione da struttura | idPrenotazione={}", idPrenotazione);

        return annullaPrenotazione(
                idPrenotazione,
                StatoPrenotazione.ANNULLATA_DA_STRUTTURA,
                "struttura"
        );
    }

    private PrenotazioneTo annullaPrenotazione(Integer idPrenotazione,
                                               StatoPrenotazione nuovoStato,
                                               String origineAnnullamento) {

        if (idPrenotazione == null || idPrenotazione <= 0) {
            log.warn("Annullamento prenotazione bloccato: id non valido | origine={} | idPrenotazione={}",
                    origineAnnullamento,
                    idPrenotazione);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazionePo = recuperaPrenotazioneConLock(idPrenotazione);

        if (prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {

            log.warn("Annullamento prenotazione bloccato: stato non annullabile | origine={} | idPrenotazione={} | stato={}",
                    origineAnnullamento,
                    idPrenotazione,
                    prenotazionePo.getStatoPrenotazione());

            throw new ServiceException(AppError.PRENOTAZIONE_NON_ANNULLABILE);
        }

        try {
            prenotazionePo.setStatoPrenotazione(nuovoStato);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));

            prenotazionePo = prenotazioneRepository.save(prenotazionePo);

            log.info("Annullamento prenotazione completato | origine={} | idPrenotazione={} | stato={}",
                    origineAnnullamento,
                    prenotazionePo.getIdPrenotazione(),
                    prenotazionePo.getStatoPrenotazione());

            return prenotazioneMapper.toDto(prenotazionePo);

        } catch (Exception e) {
            log.error("Annullamento prenotazione fallito per errore tecnico | origine={} | idPrenotazione={}",
                    origineAnnullamento,
                    idPrenotazione,
                    e);

            throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public List<PrenotazioneTo> getPrenotazioniDaRiprogrammare() {

        log.info("Ricerca prenotazioni da riprogrammare");

        List<PrenotazioneTo> risultato =
                prenotazioneRepository.findByStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE)
                        .stream()
                        .map(prenotazioneMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca prenotazioni da riprogrammare completata | elementi={}",
                risultato.size());

        return risultato;
    }

    @Override
    public List<PrenotazioneTo> getPrenotazioniDaRiprogrammareByUtente(Integer idUtente) {

        log.info("Ricerca prenotazioni da riprogrammare per utente | idUtente={}", idUtente);

        if (idUtente == null || idUtente <= 0) {
            log.warn("Ricerca prenotazioni da riprogrammare bloccata: id utente non valido | idUtente={}",
                    idUtente);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!utenteRepository.existsById(idUtente)) {
            log.warn("Ricerca prenotazioni da riprogrammare bloccata: utente non trovato | idUtente={}",
                    idUtente);

            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        List<PrenotazioneTo> risultato =
                prenotazioneRepository
                        .findByUtentePo_IdUtenteAndStatoPrenotazione(
                                idUtente,
                                StatoPrenotazione.DA_RIPROGRAMMARE
                        )
                        .stream()
                        .map(prenotazioneMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca prenotazioni da riprogrammare per utente completata | idUtente={} | elementi={}",
                idUtente,
                risultato.size());

        return risultato;
    }
}