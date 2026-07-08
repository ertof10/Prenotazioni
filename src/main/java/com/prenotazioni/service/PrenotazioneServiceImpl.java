package com.prenotazioni.service;

import com.prenotazioni.dao.*;
import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.enums.TipoGestioneServizio;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.PrenotazioneMapper;
import com.prenotazioni.po.*;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

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
        validaDatiPrenotazione(prenotazioneTo);
        UtentePo utentePo = recuperaUtenteAttivo(prenotazioneTo.getIdUtente());
        ServizioPo servizioPo = recuperaServizioAttivo(prenotazioneTo.getIdServizio());
        PrenotazionePo prenotazioneEsistentePo = recuperaPrenotazioneEsistenteSeModifica(prenotazioneTo.getIdPrenotazione());
        verificaSovrapposizioneUtente(prenotazioneTo);
        if (servizioPo.getTipoGestioneServizio() == TipoGestioneServizio.A_POSTI) {
            return gestisciPrenotazioneAPosti(prenotazioneTo, utentePo, servizioPo, prenotazioneEsistentePo);
        }
        return gestisciPrenotazioneConCollaboratore(prenotazioneTo, utentePo, servizioPo, prenotazioneEsistentePo);
    }

    private void validaDatiPrenotazione(PrenotazioneTo prenotazioneTo) {
        if (prenotazioneTo == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_VALIDA);
        }
        if (prenotazioneTo.getIdUtente() == null || prenotazioneTo.getIdUtente() <= 0 || prenotazioneTo.getIdServizio() == null || prenotazioneTo.getIdServizio() <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
        if (prenotazioneTo.getDataPrenotazione() == null || prenotazioneTo.getOraInizioPrenotazione() == null || prenotazioneTo.getOraFinePrenotazione() == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_VALIDA);
        }
        if (prenotazioneTo.getOraFinePrenotazione().isBefore(prenotazioneTo.getOraInizioPrenotazione()) || prenotazioneTo.getOraFinePrenotazione().equals(prenotazioneTo.getOraInizioPrenotazione())) {
            throw new ServiceException(AppError.ORARIO_PRENOTAZIONE_NON_VALIDO);
        }
        if (prenotazioneTo.getDataPrenotazione().isBefore(LocalDate.now(clock))) {
            throw new ServiceException(AppError.DATA_PRENOTAZIONE_NON_VALIDA);
        }
    }

    private UtentePo recuperaUtenteAttivo(Integer idUtente) {

        UtentePo utentePo = recuperaUtenteConLock(idUtente);

        if (!Boolean.TRUE.equals(utentePo.getAttivoUtente())) {
            throw new ServiceException(AppError.UTENTE_NON_ATTIVO);
        }

        return utentePo;
    }

    private UtentePo recuperaUtenteConLock(Integer idUtente) {

        return utenteRepository.findByIdForUpdate(idUtente)
                .orElseThrow(() -> new ServiceException(AppError.UTENTE_NON_TROVATO));
    }

    private ServizioPo recuperaServizioAttivo(Integer idServizio) {

        ServizioPo servizioPo = recuperaServizioConLock(idServizio);

        if (!Boolean.TRUE.equals(servizioPo.getAttivoServizio())) {
            throw new ServiceException(AppError.SERVIZIO_NON_ATTIVO);
        }

        if (servizioPo.getTipoGestioneServizio() == null) {
            throw new ServiceException(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO);
        }

        return servizioPo;
    }

    private ServizioPo recuperaServizioConLock(Integer idServizio) {

        return servizioRepository.findByIdForUpdate(idServizio)
                .orElseThrow(() -> new ServiceException(AppError.SERVIZIO_NON_TROVATO));
    }

    private PrenotazionePo recuperaPrenotazioneConLock(Integer idPrenotazione) {

        return prenotazioneRepository.findByIdForUpdate(idPrenotazione)
                .orElseThrow(() -> new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA));
    }


    private CollaboratorePo recuperaCollaboratoreConLock(Integer idCollaboratore) {

        return collaboratoreRepository.findByIdForUpdate(idCollaboratore)
                .orElseThrow(() -> new ServiceException(AppError.COLLABORATORE_NON_TROVATO));
    }


    private PrenotazionePo recuperaPrenotazioneEsistenteSeModifica(Integer idPrenotazione) {

        if (idPrenotazione == null) {
            return null;
        }

        if (idPrenotazione <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazioneEsistentePo = recuperaPrenotazioneConLock(idPrenotazione);

        if (prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {

            throw new ServiceException(AppError.PRENOTAZIONE_NON_MODIFICABILE);
        }

        return prenotazioneEsistentePo;
    }

    private void verificaSovrapposizioneUtente(PrenotazioneTo prenotazioneTo) {
        boolean prenotazioneSovrappostaUtente = prenotazioneRepository.existsPrenotazioneSovrappostaUtente(prenotazioneTo.getIdPrenotazione(), prenotazioneTo.getIdUtente(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (prenotazioneSovrappostaUtente) {
            throw new ServiceException(AppError.PRENOTAZIONE_GIA_PRESENTE);
        }
    }

    private PrenotazioneTo gestisciPrenotazioneAPosti(PrenotazioneTo prenotazioneTo, UtentePo utentePo, ServizioPo servizioPo, PrenotazionePo prenotazioneEsistentePo) {
        validaServizioAPosti(prenotazioneTo, servizioPo);
        verificaPostiDisponibiliServizioAPosti(prenotazioneTo, servizioPo);
        PrenotazionePo prenotazionePo = preparaPrenotazione(prenotazioneTo, utentePo, servizioPo, null, prenotazioneEsistentePo);
        return salvaPrenotazione(prenotazionePo, prenotazioneTo.getIdPrenotazione() == null);
    }

    private void validaServizioAPosti(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo) {
        if (prenotazioneTo.getIdCollaboratore() != null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_PREVISTO_PER_SERVIZIO_A_POSTI);
        }
        if (servizioPo.getPostiMassimiServizio() == null || servizioPo.getPostiMassimiServizio() <= 0) {
            throw new ServiceException(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO);
        }
    }

    private void verificaPostiDisponibiliServizioAPosti(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo) {
        Long postiOccupati = prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(prenotazioneTo.getIdPrenotazione(), servizioPo.getIdServizio(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (postiOccupati >= servizioPo.getPostiMassimiServizio()) {
            throw new ServiceException(AppError.POSTI_SERVIZIO_ESAURITI);
        }
    }

    private PrenotazioneTo gestisciPrenotazioneConCollaboratore(PrenotazioneTo prenotazioneTo, UtentePo utentePo, ServizioPo servizioPo, PrenotazionePo prenotazioneEsistentePo) {
        DayOfWeek giornoSettimanaPrenotazione = prenotazioneTo.getDataPrenotazione().getDayOfWeek();
        CollaboratorePo collaboratorePo = selezionaCollaboratore(prenotazioneTo, servizioPo, giornoSettimanaPrenotazione);
        verificaCollaboratoreAssente(collaboratorePo, prenotazioneTo);
        PrenotazionePo prenotazionePo = preparaPrenotazione(prenotazioneTo, utentePo, servizioPo, collaboratorePo, prenotazioneEsistentePo);
        return salvaPrenotazione(prenotazionePo, prenotazioneTo.getIdPrenotazione() == null);
    }

    private CollaboratorePo selezionaCollaboratore(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo, DayOfWeek giornoSettimanaPrenotazione) {
        if (prenotazioneTo.getIdCollaboratore() != null) {
            return recuperaEValidaCollaboratoreManuale(prenotazioneTo, servizioPo, giornoSettimanaPrenotazione);
        }
        return selezionaCollaboratoreAutomatico(prenotazioneTo, servizioPo, giornoSettimanaPrenotazione);
    }

    private CollaboratorePo recuperaEValidaCollaboratoreManuale(
            PrenotazioneTo prenotazioneTo,
            ServizioPo servizioPo,
            DayOfWeek giornoSettimanaPrenotazione) {

        CollaboratorePo collaboratorePo =
                recuperaCollaboratoreConLock(prenotazioneTo.getIdCollaboratore());

        if (!Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore())) {
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

    private void verificaCollaboratoreAbilitatoAlServizio(CollaboratorePo collaboratorePo, ServizioPo servizioPo) {
        boolean collaboratoreAbilitato = collaboratoreServizioRepository.existsAssociazioneAttiva(collaboratorePo.getIdCollaboratore(), servizioPo.getIdServizio());
        if (!collaboratoreAbilitato) {
            throw new ServiceException(AppError.COLLABORATORE_NON_ABILITATO_AL_SERVIZIO);
        }
    }

    private void verificaCollaboratoreDisponibileDaCalendario(CollaboratorePo collaboratorePo, DayOfWeek giornoSettimanaPrenotazione, PrenotazioneTo prenotazioneTo) {
        boolean collaboratoreDisponibileDaCalendario = calendarioRepository.existsDisponibilitaCalendario(collaboratorePo.getIdCollaboratore(), giornoSettimanaPrenotazione, prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (!collaboratoreDisponibileDaCalendario) {
            throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE_DA_CALENDARIO);
        }
    }

    private void verificaSovrapposizioneCollaboratore(CollaboratorePo collaboratorePo, PrenotazioneTo prenotazioneTo) {
        boolean collaboratoreSovrapposto = prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(prenotazioneTo.getIdPrenotazione(), collaboratorePo.getIdCollaboratore(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (collaboratoreSovrapposto) {
            throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE);
        }
    }

    private CollaboratorePo selezionaCollaboratoreAutomatico(PrenotazioneTo prenotazioneTo, ServizioPo servizioPo, DayOfWeek giornoSettimanaPrenotazione) {
        List<CollaboratoreServizioPo> collaboratoriServizi = collaboratoreServizioRepository.findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(servizioPo.getIdServizio());

        List<CollaboratoreServizioPo> collaboratoriServiziOrdinati = collaboratoriServizi.stream()
                .sorted((primoCollaboratoreServizio, secondoCollaboratoreServizio) ->
                        primoCollaboratoreServizio.getCollaboratorePo().getIdCollaboratore()
                                .compareTo(secondoCollaboratoreServizio.getCollaboratorePo().getIdCollaboratore()))
                .collect(Collectors.toList());

        CollaboratorePo collaboratoreSelezionatoPo = null;
        Long caricoMinore = null;

        for (CollaboratoreServizioPo collaboratoreServizioPo : collaboratoriServiziOrdinati) {
            CollaboratorePo collaboratoreCandidatoPo =
                    recuperaCollaboratoreConLock(
                            collaboratoreServizioPo.getCollaboratorePo().getIdCollaboratore()
                    );

            if (!collaboratoreCandidatoValido(collaboratoreCandidatoPo, giornoSettimanaPrenotazione, prenotazioneTo)) {
                continue;
            }

            Long caricoCollaboratore = prenotazioneRepository.countPrenotazioniCollaboratorePerData(
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
            throw new ServiceException(AppError.NESSUN_COLLABORATORE_DISPONIBILE);
        }

        return collaboratoreSelezionatoPo;
    }

    private boolean collaboratoreCandidatoValido(CollaboratorePo collaboratoreCandidatoPo, DayOfWeek giornoSettimanaPrenotazione, PrenotazioneTo prenotazioneTo) {
        if (!Boolean.TRUE.equals(collaboratoreCandidatoPo.getAttivoCollaboratore())) {
            return false;
        }
        boolean collaboratoreAssente = assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(collaboratoreCandidatoPo.getIdCollaboratore(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (collaboratoreAssente) {
            return false;
        }
        boolean collaboratoreDisponibileDaCalendario = calendarioRepository.existsDisponibilitaCalendario(collaboratoreCandidatoPo.getIdCollaboratore(), giornoSettimanaPrenotazione, prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (!collaboratoreDisponibileDaCalendario) {
            return false;
        }
        boolean collaboratoreSovrapposto = prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(prenotazioneTo.getIdPrenotazione(), collaboratoreCandidatoPo.getIdCollaboratore(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        return !collaboratoreSovrapposto;
    }

    private void verificaCollaboratoreAssente(CollaboratorePo collaboratorePo, PrenotazioneTo prenotazioneTo) {
        boolean collaboratoreAssente = assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(collaboratorePo.getIdCollaboratore(), prenotazioneTo.getDataPrenotazione(), prenotazioneTo.getOraInizioPrenotazione(), prenotazioneTo.getOraFinePrenotazione());
        if (collaboratoreAssente) {
            throw new ServiceException(AppError.COLLABORATORE_ASSENTE);
        }
    }

    private PrenotazionePo preparaPrenotazione(PrenotazioneTo prenotazioneTo, UtentePo utentePo, ServizioPo servizioPo, CollaboratorePo collaboratorePo, PrenotazionePo prenotazioneEsistentePo) {
        boolean nuovaPrenotazione = prenotazioneTo.getIdPrenotazione() == null;
        PrenotazionePo prenotazionePo = nuovaPrenotazione ? new PrenotazionePo() : prenotazioneEsistentePo;
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

    private PrenotazioneTo salvaPrenotazione(PrenotazionePo prenotazionePo, boolean nuovaPrenotazione) {
        try {
            prenotazionePo = prenotazioneRepository.save(prenotazionePo);
            return prenotazioneMapper.toDto(prenotazionePo);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            if (nuovaPrenotazione) {
                throw new ServiceException(AppError.PRENOTAZIONE_CREAZIONE_FALLITA, e);
            }
            throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public PrenotazioneTo getPrenotazioneById(Integer idPrenotazione) {

        if (idPrenotazione == null || idPrenotazione <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazionePo = prenotazioneRepository.findById(idPrenotazione).orElse(null);

        if (prenotazionePo == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
        }

        return prenotazioneMapper.toDto(prenotazionePo);
    }

    @Override
    public List<PrenotazioneTo> getAllPrenotazioni() {
        return prenotazioneRepository.findAll()
                .stream()
                .map(prenotazioneMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrenotazioneTo> getPrenotazioniByUtente(Integer idUtente) {
        return prenotazioneRepository.findByUtentePo_IdUtente(idUtente)
                .stream()
                .map(prenotazioneMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deletePrenotazione(Integer idPrenotazione) {

        if (idPrenotazione == null || idPrenotazione <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        recuperaPrenotazioneConLock(idPrenotazione);

        try {
            prenotazioneRepository.deleteById(idPrenotazione);
            return new EsitoResponse("Prenotazione eliminata correttamente");

        } catch (Exception e) {
            throw new ServiceException(AppError.PRENOTAZIONE_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PrenotazioneTo utenteAnnullaPrenotazione(Integer idPrenotazione) {

        if (idPrenotazione == null || idPrenotazione <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazionePo = recuperaPrenotazioneConLock(idPrenotazione);

        if (prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {

            throw new ServiceException(AppError.PRENOTAZIONE_NON_ANNULLABILE);
        }

        try {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.ANNULLATA_DA_UTENTE);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));

            prenotazionePo = prenotazioneRepository.save(prenotazionePo);

            return prenotazioneMapper.toDto(prenotazionePo);

        } catch (Exception e) {
            throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PrenotazioneTo strutturaAnnullaPrenotazione(Integer idPrenotazione) {

        if (idPrenotazione == null || idPrenotazione <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PrenotazionePo prenotazionePo = recuperaPrenotazioneConLock(idPrenotazione);

        if (prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {

            throw new ServiceException(AppError.PRENOTAZIONE_NON_ANNULLABILE);
        }

        try {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.ANNULLATA_DA_STRUTTURA);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));

            prenotazionePo = prenotazioneRepository.save(prenotazionePo);

            return prenotazioneMapper.toDto(prenotazionePo);

        } catch (Exception e) {
            throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
        }
    }


    @Override
    public List<PrenotazioneTo> getPrenotazioniDaRiprogrammare() {
        return prenotazioneRepository.findByStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE)
                .stream()
                .map(prenotazioneMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<PrenotazioneTo> getPrenotazioniDaRiprogrammareByUtente(Integer idUtente) {

        if (idUtente == null || idUtente <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!utenteRepository.existsById(idUtente)) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        return prenotazioneRepository
                .findByUtentePo_IdUtenteAndStatoPrenotazione(
                        idUtente,
                        StatoPrenotazione.DA_RIPROGRAMMARE
                )
                .stream()
                .map(prenotazioneMapper::toDto)
                .collect(Collectors.toList());
    }
}