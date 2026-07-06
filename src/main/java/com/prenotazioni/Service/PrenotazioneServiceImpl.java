package com.prenotazioni.Service;

import com.prenotazioni.Dao.*;
import com.prenotazioni.Dto.PrenotazioneTo;
import com.prenotazioni.Enums.StatoPrenotazione;
import com.prenotazioni.Enums.TipoGestioneServizio;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.PrenotazioneMapper;
import com.prenotazioni.Po.CollaboratorePo;
import com.prenotazioni.Po.CollaboratoreServizioPo;
import com.prenotazioni.Po.PrenotazionePo;
import com.prenotazioni.Po.ServizioPo;
import com.prenotazioni.Po.UtentePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PrenotazioneServiceImpl(PrenotazioneRepository prenotazioneRepository,
                                   UtenteRepository utenteRepository,
                                   ServizioRepository servizioRepository,
                                   CollaboratoreRepository collaboratoreRepository,
                                   CollaboratoreServizioRepository collaboratoreServizioRepository,
                                   PrenotazioneMapper prenotazioneMapper,
                                   CalendarioRepository calendarioRepository,
                                   AssenzaCollaboratoreRepository assenzaCollaboratoreRepository) {

        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.servizioRepository = servizioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.prenotazioneMapper = prenotazioneMapper;
        this.calendarioRepository = calendarioRepository;
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
    }

    @Override
    @Transactional
    public PrenotazioneTo saveOrUpdatePrenotazione(PrenotazioneTo prenotazioneTo) {

        if (prenotazioneTo == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_VALIDA);
        }

        if (prenotazioneTo.getOraFinePrenotazione().isBefore(prenotazioneTo.getOraInizioPrenotazione())
                || prenotazioneTo.getOraFinePrenotazione().equals(prenotazioneTo.getOraInizioPrenotazione())) {
            throw new ServiceException(AppError.ORARIO_PRENOTAZIONE_NON_VALIDO);
        }

        if (prenotazioneTo.getDataPrenotazione().isBefore(LocalDate.now())) {
            throw new ServiceException(AppError.DATA_PRENOTAZIONE_NON_VALIDA);
        }

        UtentePo utentePo = utenteRepository.findById(prenotazioneTo.getIdUtente()).orElse(null);

        if (utentePo == null) {
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        ServizioPo servizioPo = servizioRepository.findById(prenotazioneTo.getIdServizio()).orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (!Boolean.TRUE.equals(servizioPo.getAttivoServizio())) {
            throw new ServiceException(AppError.SERVIZIO_NON_ATTIVO);
        }

        if (servizioPo.getTipoGestioneServizio() == null) {
            throw new ServiceException(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO);
        }

        PrenotazionePo prenotazioneEsistentePo = null;

        if (prenotazioneTo.getIdPrenotazione() != null) {

            prenotazioneEsistentePo = prenotazioneRepository
                    .findById(prenotazioneTo.getIdPrenotazione())
                    .orElse(null);

            if (prenotazioneEsistentePo == null) {
                throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
            }

            if (prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                    && prenotazioneEsistentePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {
                throw new ServiceException(AppError.PRENOTAZIONE_NON_MODIFICABILE);
            }
        }

        boolean prenotazioneSovrappostaUtente =
                prenotazioneRepository.existsPrenotazioneSovrappostaUtente(
                        prenotazioneTo.getIdPrenotazione(),
                        prenotazioneTo.getIdUtente(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (prenotazioneSovrappostaUtente) {
            throw new ServiceException(AppError.PRENOTAZIONE_GIA_PRESENTE);
        }

        if (servizioPo.getTipoGestioneServizio() == TipoGestioneServizio.A_POSTI) {

            if (prenotazioneTo.getIdCollaboratore() != null) {
                throw new ServiceException(AppError.COLLABORATORE_NON_PREVISTO_PER_SERVIZIO_A_POSTI);
            }

            if (servizioPo.getPostiMassimiServizio() == null
                    || servizioPo.getPostiMassimiServizio() <= 0) {
                throw new ServiceException(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO);
            }

            Long postiOccupati =
                    prenotazioneRepository.countPrenotazioniConfermateServizioAPosti(
                            prenotazioneTo.getIdPrenotazione(),
                            servizioPo.getIdServizio(),
                            prenotazioneTo.getDataPrenotazione(),
                            prenotazioneTo.getOraInizioPrenotazione(),
                            prenotazioneTo.getOraFinePrenotazione()
                    );

            if (postiOccupati >= servizioPo.getPostiMassimiServizio()) {
                throw new ServiceException(AppError.POSTI_SERVIZIO_ESAURITI);
            }

            if (prenotazioneTo.getIdPrenotazione() == null) {

                try {
                    PrenotazionePo prenotazionePo = new PrenotazionePo();

                    prenotazionePo.setUtentePo(utentePo);
                    prenotazionePo.setServizioPo(servizioPo);
                    prenotazionePo.setCollaboratorePo(null);
                    prenotazionePo.setDataPrenotazione(prenotazioneTo.getDataPrenotazione());
                    prenotazionePo.setOraInizioPrenotazione(prenotazioneTo.getOraInizioPrenotazione());
                    prenotazionePo.setOraFinePrenotazione(prenotazioneTo.getOraFinePrenotazione());
                    prenotazionePo.setNotePrenotazione(prenotazioneTo.getNotePrenotazione());
                    prenotazionePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);
                    prenotazionePo.setDataCreazionePrenotazione(LocalDateTime.now());

                    prenotazionePo = prenotazioneRepository.save(prenotazionePo);

                    return prenotazioneMapper.toDto(prenotazionePo);

                } catch (ServiceException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ServiceException(AppError.PRENOTAZIONE_CREAZIONE_FALLITA, e);
                }
            }

            try {
                prenotazioneEsistentePo.setUtentePo(utentePo);
                prenotazioneEsistentePo.setServizioPo(servizioPo);
                prenotazioneEsistentePo.setCollaboratorePo(null);
                prenotazioneEsistentePo.setDataPrenotazione(prenotazioneTo.getDataPrenotazione());
                prenotazioneEsistentePo.setOraInizioPrenotazione(prenotazioneTo.getOraInizioPrenotazione());
                prenotazioneEsistentePo.setOraFinePrenotazione(prenotazioneTo.getOraFinePrenotazione());
                prenotazioneEsistentePo.setNotePrenotazione(prenotazioneTo.getNotePrenotazione());
                prenotazioneEsistentePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);
                prenotazioneEsistentePo.setDataModificaPrenotazione(LocalDateTime.now());

                prenotazioneEsistentePo = prenotazioneRepository.save(prenotazioneEsistentePo);

                return prenotazioneMapper.toDto(prenotazioneEsistentePo);

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException(AppError.PRENOTAZIONE_MODIFICA_FALLITA, e);
            }
        }

        DayOfWeek giornoSettimanaPrenotazione = prenotazioneTo.getDataPrenotazione().getDayOfWeek();

        CollaboratorePo collaboratorePo = null;

        if (prenotazioneTo.getIdCollaboratore() != null) {

            collaboratorePo = collaboratoreRepository.findById(prenotazioneTo.getIdCollaboratore()).orElse(null);

            if (collaboratorePo == null) {
                throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
            }

            if (!Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore())) {
                throw new ServiceException(AppError.COLLABORATORE_NON_ATTIVO);
            }

            boolean collaboratoreAbilitato =
                    collaboratoreServizioRepository.existsAssociazioneAttiva(
                            collaboratorePo.getIdCollaboratore(),
                            servizioPo.getIdServizio()
                    );

            if (!collaboratoreAbilitato) {
                throw new ServiceException(AppError.COLLABORATORE_NON_ABILITATO_AL_SERVIZIO);
            }

            boolean collaboratoreDisponibileDaCalendario =
                    calendarioRepository.existsDisponibilitaCalendario(
                            collaboratorePo.getIdCollaboratore(),
                            giornoSettimanaPrenotazione,
                            prenotazioneTo.getOraInizioPrenotazione(),
                            prenotazioneTo.getOraFinePrenotazione()
                    );

            if (!collaboratoreDisponibileDaCalendario) {
                throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE_DA_CALENDARIO);
            }

            boolean collaboratoreSovrapposto =
                    prenotazioneRepository.existsPrenotazioneSovrappostaCollaboratore(
                            prenotazioneTo.getIdPrenotazione(),
                            collaboratorePo.getIdCollaboratore(),
                            prenotazioneTo.getDataPrenotazione(),
                            prenotazioneTo.getOraInizioPrenotazione(),
                            prenotazioneTo.getOraFinePrenotazione()
                    );

            if (collaboratoreSovrapposto) {
                throw new ServiceException(AppError.COLLABORATORE_NON_DISPONIBILE);
            }

        } else {

            List<CollaboratoreServizioPo> collaboratoriServizi =
                    collaboratoreServizioRepository.findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(
                            servizioPo.getIdServizio()
                    );

            Long caricoMinore = null;

            for (CollaboratoreServizioPo collaboratoreServizioPo : collaboratoriServizi) {

                CollaboratorePo collaboratoreCandidatoPo = collaboratoreServizioPo.getCollaboratorePo();

                if (!Boolean.TRUE.equals(collaboratoreCandidatoPo.getAttivoCollaboratore())) {
                    continue;
                }

                boolean collaboratoreAssente =
                        assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(
                                collaboratoreCandidatoPo.getIdCollaboratore(),
                                prenotazioneTo.getDataPrenotazione(),
                                prenotazioneTo.getOraInizioPrenotazione(),
                                prenotazioneTo.getOraFinePrenotazione()
                        );

                if (collaboratoreAssente) {
                    continue;
                }

                boolean collaboratoreDisponibileDaCalendario =
                        calendarioRepository.existsDisponibilitaCalendario(
                                collaboratoreCandidatoPo.getIdCollaboratore(),
                                giornoSettimanaPrenotazione,
                                prenotazioneTo.getOraInizioPrenotazione(),
                                prenotazioneTo.getOraFinePrenotazione()
                        );

                if (!collaboratoreDisponibileDaCalendario) {
                    continue;
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
                    continue;
                }

                Long caricoCollaboratore =
                        prenotazioneRepository.countPrenotazioniCollaboratorePerData(
                                prenotazioneTo.getIdPrenotazione(),
                                collaboratoreCandidatoPo.getIdCollaboratore(),
                                prenotazioneTo.getDataPrenotazione()
                        );

                if (collaboratorePo == null || caricoCollaboratore < caricoMinore) {
                    collaboratorePo = collaboratoreCandidatoPo;
                    caricoMinore = caricoCollaboratore;
                }
            }

            if (collaboratorePo == null) {
                throw new ServiceException(AppError.NESSUN_COLLABORATORE_DISPONIBILE);
            }
        }

        boolean collaboratoreAssente =
                assenzaCollaboratoreRepository.existsAssenzaAttivaSuPrenotazione(
                        collaboratorePo.getIdCollaboratore(),
                        prenotazioneTo.getDataPrenotazione(),
                        prenotazioneTo.getOraInizioPrenotazione(),
                        prenotazioneTo.getOraFinePrenotazione()
                );

        if (collaboratoreAssente) {
            throw new ServiceException(AppError.COLLABORATORE_ASSENTE);
        }

        if (prenotazioneTo.getIdPrenotazione() == null) {

            try {
                PrenotazionePo prenotazionePo = new PrenotazionePo();

                prenotazionePo.setUtentePo(utentePo);
                prenotazionePo.setServizioPo(servizioPo);
                prenotazionePo.setCollaboratorePo(collaboratorePo);
                prenotazionePo.setDataPrenotazione(prenotazioneTo.getDataPrenotazione());
                prenotazionePo.setOraInizioPrenotazione(prenotazioneTo.getOraInizioPrenotazione());
                prenotazionePo.setOraFinePrenotazione(prenotazioneTo.getOraFinePrenotazione());
                prenotazionePo.setNotePrenotazione(prenotazioneTo.getNotePrenotazione());
                prenotazionePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);
                prenotazionePo.setDataCreazionePrenotazione(LocalDateTime.now());

                prenotazionePo = prenotazioneRepository.save(prenotazionePo);

                return prenotazioneMapper.toDto(prenotazionePo);

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException(AppError.PRENOTAZIONE_CREAZIONE_FALLITA, e);
            }
        }

        try {
            prenotazioneEsistentePo.setUtentePo(utentePo);
            prenotazioneEsistentePo.setServizioPo(servizioPo);
            prenotazioneEsistentePo.setCollaboratorePo(collaboratorePo);
            prenotazioneEsistentePo.setDataPrenotazione(prenotazioneTo.getDataPrenotazione());
            prenotazioneEsistentePo.setOraInizioPrenotazione(prenotazioneTo.getOraInizioPrenotazione());
            prenotazioneEsistentePo.setOraFinePrenotazione(prenotazioneTo.getOraFinePrenotazione());
            prenotazioneEsistentePo.setNotePrenotazione(prenotazioneTo.getNotePrenotazione());
            prenotazioneEsistentePo.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);
            prenotazioneEsistentePo.setDataModificaPrenotazione(LocalDateTime.now());

            prenotazioneEsistentePo = prenotazioneRepository.save(prenotazioneEsistentePo);

            return prenotazioneMapper.toDto(prenotazioneEsistentePo);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
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

        if (!prenotazioneRepository.existsById(idPrenotazione)) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
        }

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

        PrenotazionePo prenotazionePo = prenotazioneRepository
                .findById(idPrenotazione)
                .orElse(null);

        if (prenotazionePo == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
        }

        if (prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_ANNULLABILE);
        }

        try {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.ANNULLATA_DA_UTENTE);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now());

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

        PrenotazionePo prenotazionePo = prenotazioneRepository
                .findById(idPrenotazione)
                .orElse(null);

        if (prenotazionePo == null) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_TROVATA);
        }

        if (prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.CONFERMATA
                && prenotazionePo.getStatoPrenotazione() != StatoPrenotazione.DA_RIPROGRAMMARE) {
            throw new ServiceException(AppError.PRENOTAZIONE_NON_ANNULLABILE);
        }

        try {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.ANNULLATA_DA_STRUTTURA);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now());

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