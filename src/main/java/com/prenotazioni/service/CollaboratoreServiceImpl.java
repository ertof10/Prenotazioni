package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CollaboratoreMapper;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.PrenotazionePo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaboratoreServiceImpl implements CollaboratoreService {

    private static final Logger log = LoggerFactory.getLogger(CollaboratoreServiceImpl.class);

    private final CollaboratoreRepository collaboratoreRepository;
    private final CollaboratoreMapper collaboratoreMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final CalendarioRepository calendarioRepository;
    private final AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;
    private final Clock clock;

    public CollaboratoreServiceImpl(CollaboratoreRepository collaboratoreRepository,
                                    CollaboratoreMapper collaboratoreMapper,
                                    PrenotazioneRepository prenotazioneRepository,
                                    CollaboratoreServizioRepository collaboratoreServizioRepository,
                                    CalendarioRepository calendarioRepository,
                                    AssenzaCollaboratoreRepository assenzaCollaboratoreRepository,
                                    Clock clock) {
        this.collaboratoreRepository = collaboratoreRepository;
        this.collaboratoreMapper = collaboratoreMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.calendarioRepository = calendarioRepository;
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CollaboratoreTo saveOrUpdateCollaboratore(CollaboratoreTo collaboratoreTo) {

        log.info("Avvio salvataggio collaboratore | idCollaboratore={} | email={}",
                collaboratoreTo != null ? collaboratoreTo.getIdCollaboratore() : null,
                collaboratoreTo != null ? collaboratoreTo.getEmailCollaboratore() : null);

        validaRichiestaCollaboratore(collaboratoreTo);

        normalizzaDatiCollaboratore(collaboratoreTo);

        if (collaboratoreTo.getIdCollaboratore() == null) {
            return creaCollaboratore(collaboratoreTo);
        }

        return modificaCollaboratore(collaboratoreTo);
    }

    private void validaRichiestaCollaboratore(CollaboratoreTo collaboratoreTo) {

        if (collaboratoreTo == null) {
            log.warn("Salvataggio collaboratore bloccato: request nulla");
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (collaboratoreTo.getIdCollaboratore() != null
                && collaboratoreTo.getIdCollaboratore() <= 0) {

            log.warn("Salvataggio collaboratore bloccato: id non valido | idCollaboratore={}",
                    collaboratoreTo.getIdCollaboratore());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
    }

    private void normalizzaDatiCollaboratore(CollaboratoreTo collaboratoreTo) {

        String emailCollaboratore = collaboratoreTo.getEmailCollaboratore()
                .trim()
                .toLowerCase();

        String nomeCollaboratore = collaboratoreTo.getNomeCollaboratore()
                .trim()
                .toLowerCase();

        String cognomeCollaboratore = collaboratoreTo.getCognomeCollaboratore()
                .trim()
                .toLowerCase();

        String specializzazioneCollaboratore = collaboratoreTo.getSpecializzazioneCollaboratore()
                .trim()
                .toLowerCase();

        collaboratoreTo.setEmailCollaboratore(emailCollaboratore);
        collaboratoreTo.setNomeCollaboratore(nomeCollaboratore);
        collaboratoreTo.setCognomeCollaboratore(cognomeCollaboratore);
        collaboratoreTo.setSpecializzazioneCollaboratore(specializzazioneCollaboratore);
    }

    private CollaboratoreTo creaCollaboratore(CollaboratoreTo collaboratoreTo) {

        log.info("Avvio creazione collaboratore | email={}",
                collaboratoreTo.getEmailCollaboratore());

        if (collaboratoreRepository.existsByEmailCollaboratore(collaboratoreTo.getEmailCollaboratore())) {
            log.warn("Creazione collaboratore bloccata: email già presente | email={}",
                    collaboratoreTo.getEmailCollaboratore());

            throw new ServiceException(AppError.COLLABORATORE_GIA_PRESENTE);
        }

        try {
            CollaboratorePo collaboratorePo = collaboratoreMapper.toEntity(collaboratoreTo);

            collaboratorePo.setAttivoCollaboratore(true);
            collaboratorePo.setDataCreazioneCollaboratore(LocalDateTime.now(clock));

            collaboratorePo = collaboratoreRepository.save(collaboratorePo);

            log.info("Creazione collaboratore completata | idCollaboratore={} | email={}",
                    collaboratorePo.getIdCollaboratore(),
                    collaboratorePo.getEmailCollaboratore());

            return collaboratoreMapper.toDto(collaboratorePo);

        } catch (Exception e) {
            log.error("Creazione collaboratore fallita per errore tecnico | email={}",
                    collaboratoreTo.getEmailCollaboratore(),
                    e);

            throw new ServiceException(AppError.COLLABORATORE_CREAZIONE_FALLITA, e);
        }
    }

    private CollaboratoreTo modificaCollaboratore(CollaboratoreTo collaboratoreTo) {

        log.info("Avvio modifica collaboratore | idCollaboratore={} | email={}",
                collaboratoreTo.getIdCollaboratore(),
                collaboratoreTo.getEmailCollaboratore());

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(collaboratoreTo.getIdCollaboratore())
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Modifica collaboratore bloccata: collaboratore non trovato | idCollaboratore={}",
                    collaboratoreTo.getIdCollaboratore());

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (!collaboratoreTo.getEmailCollaboratore().equalsIgnoreCase(collaboratorePo.getEmailCollaboratore())
                && collaboratoreRepository.existsByEmailCollaboratore(collaboratoreTo.getEmailCollaboratore())) {

            log.warn("Modifica collaboratore bloccata: nuova email già presente | idCollaboratore={} | email={}",
                    collaboratoreTo.getIdCollaboratore(),
                    collaboratoreTo.getEmailCollaboratore());

            throw new ServiceException(AppError.COLLABORATORE_GIA_PRESENTE);
        }

        try {
            collaboratorePo.setNomeCollaboratore(collaboratoreTo.getNomeCollaboratore());
            collaboratorePo.setCognomeCollaboratore(collaboratoreTo.getCognomeCollaboratore());
            collaboratorePo.setEmailCollaboratore(collaboratoreTo.getEmailCollaboratore());
            collaboratorePo.setTelefonoCollaboratore(collaboratoreTo.getTelefonoCollaboratore());
            collaboratorePo.setSpecializzazioneCollaboratore(collaboratoreTo.getSpecializzazioneCollaboratore());
            collaboratorePo.setDescrizioneCollaboratore(collaboratoreTo.getDescrizioneCollaboratore());
            collaboratorePo.setDataModificaCollaboratore(LocalDateTime.now(clock));

            if (collaboratoreTo.getAttivoCollaboratore() != null) {
                collaboratorePo.setAttivoCollaboratore(collaboratoreTo.getAttivoCollaboratore());
            }

            collaboratorePo = collaboratoreRepository.save(collaboratorePo);

            log.info("Modifica collaboratore completata | idCollaboratore={} | email={} | attivo={}",
                    collaboratorePo.getIdCollaboratore(),
                    collaboratorePo.getEmailCollaboratore(),
                    collaboratorePo.getAttivoCollaboratore());

            return collaboratoreMapper.toDto(collaboratorePo);

        } catch (Exception e) {
            log.error("Modifica collaboratore fallita per errore tecnico | idCollaboratore={}",
                    collaboratoreTo.getIdCollaboratore(),
                    e);

            throw new ServiceException(AppError.COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CollaboratoreTo getCollaboratoreById(Integer idCollaboratore) {

        log.info("Ricerca collaboratore per id | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Ricerca collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Ricerca collaboratore fallita: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        log.info("Ricerca collaboratore completata | idCollaboratore={} | email={}",
                collaboratorePo.getIdCollaboratore(),
                collaboratorePo.getEmailCollaboratore());

        return collaboratoreMapper.toDto(collaboratorePo);
    }

    @Override
    public Page<CollaboratoreTo> getAllCollaboratori(Pageable pageable) {

        log.info("Ricerca collaboratori paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<CollaboratoreTo> risultato = collaboratoreRepository.findAll(pageable)
                .map(collaboratoreMapper::toDto);

        log.info("Ricerca collaboratori completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public List<CollaboratoreTo> getCollaboratoriAttivi() {

        log.info("Ricerca collaboratori attivi");

        List<CollaboratoreTo> risultato = collaboratoreRepository.findAll()
                .stream()
                .filter(collaboratorePo -> Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore()))
                .map(collaboratoreMapper::toDto)
                .collect(Collectors.toList());

        log.info("Ricerca collaboratori attivi completata | elementi={}",
                risultato.size());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteCollaboratore(Integer idCollaboratore) {

        log.info("Avvio eliminazione collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Eliminazione collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreRepository.existsById(idCollaboratore)) {
            log.warn("Eliminazione collaboratore bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || collaboratoreServizioRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || calendarioRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || assenzaCollaboratoreRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)) {

            log.warn("Eliminazione collaboratore bloccata: dati associati presenti | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_ELIMINABILE);
        }

        try {
            collaboratoreRepository.deleteById(idCollaboratore);

            log.info("Eliminazione collaboratore completata | idCollaboratore={}",
                    idCollaboratore);

            return new EsitoResponse("Collaboratore eliminato correttamente");

        } catch (Exception e) {
            log.error("Eliminazione collaboratore fallita per errore tecnico | idCollaboratore={}",
                    idCollaboratore,
                    e);

            throw new ServiceException(AppError.COLLABORATORE_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public CollaboratoreTo disattivaCollaboratore(Integer idCollaboratore) {

        log.info("Avvio disattivazione collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Disattivazione collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Disattivazione collaboratore bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        try {
            collaboratorePo.setAttivoCollaboratore(false);

            List<PrenotazionePo> prenotazioniFuture =
                    prenotazioneRepository.findPrenotazioniFutureConfermateByCollaboratore(
                            idCollaboratore,
                            LocalDate.now(clock)
                    );

            for (PrenotazionePo prenotazionePo : prenotazioniFuture) {
                prenotazionePo.setStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE);
                prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));
            }

            prenotazioneRepository.saveAll(prenotazioniFuture);

            collaboratorePo = collaboratoreRepository.save(collaboratorePo);

            log.info("Disattivazione collaboratore completata | idCollaboratore={} | prenotazioniDaRiprogrammare={}",
                    idCollaboratore,
                    prenotazioniFuture.size());

            return collaboratoreMapper.toDto(collaboratorePo);

        } catch (Exception e) {
            log.error("Disattivazione collaboratore fallita per errore tecnico | idCollaboratore={}",
                    idCollaboratore,
                    e);

            throw new ServiceException(AppError.COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public CollaboratoreTo riattivaCollaboratore(Integer idCollaboratore) {

        log.info("Avvio riattivazione collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Riattivazione collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Riattivazione collaboratore bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        try {
            collaboratorePo.setAttivoCollaboratore(true);
            collaboratorePo.setDataModificaCollaboratore(LocalDateTime.now(clock));

            collaboratorePo = collaboratoreRepository.save(collaboratorePo);

            log.info("Riattivazione collaboratore completata | idCollaboratore={}",
                    idCollaboratore);

            return collaboratoreMapper.toDto(collaboratorePo);

        } catch (Exception e) {
            log.error("Riattivazione collaboratore fallita per errore tecnico | idCollaboratore={}",
                    idCollaboratore,
                    e);

            throw new ServiceException(AppError.COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }
}