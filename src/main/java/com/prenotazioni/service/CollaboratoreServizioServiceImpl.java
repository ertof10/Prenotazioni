package com.prenotazioni.service;

import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CollaboratoreServizioMapper;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.CollaboratoreServizioPo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaboratoreServizioServiceImpl implements CollaboratoreServizioService {

    private static final Logger log = LoggerFactory.getLogger(CollaboratoreServizioServiceImpl.class);

    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final ServizioRepository servizioRepository;
    private final CollaboratoreServizioMapper collaboratoreServizioMapper;
    private final Clock clock;

    public CollaboratoreServizioServiceImpl(CollaboratoreServizioRepository collaboratoreServizioRepository,
                                            CollaboratoreRepository collaboratoreRepository,
                                            ServizioRepository servizioRepository,
                                            CollaboratoreServizioMapper collaboratoreServizioMapper,
                                            Clock clock) {
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.servizioRepository = servizioRepository;
        this.collaboratoreServizioMapper = collaboratoreServizioMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CollaboratoreServizioTo saveOrUpdateCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo) {

        log.info("Avvio salvataggio associazione collaboratore-servizio | idCollaboratoreServizio={} | idCollaboratore={} | idServizio={}",
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdCollaboratoreServizio() : null,
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdCollaboratore() : null,
                collaboratoreServizioTo != null ? collaboratoreServizioTo.getIdServizio() : null);

        validaRichiestaCollaboratoreServizio(collaboratoreServizioTo);

        CollaboratorePo collaboratorePo = recuperaCollaboratore(collaboratoreServizioTo.getIdCollaboratore());

        ServizioPo servizioPo = recuperaServizio(collaboratoreServizioTo.getIdServizio());

        if (collaboratoreServizioTo.getIdCollaboratoreServizio() == null) {
            return creaCollaboratoreServizio(collaboratoreServizioTo, collaboratorePo, servizioPo);
        }

        return modificaCollaboratoreServizio(collaboratoreServizioTo, collaboratorePo, servizioPo);
    }

    private void validaRichiestaCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo) {

        if (collaboratoreServizioTo == null) {
            log.warn("Salvataggio associazione collaboratore-servizio bloccato: request nulla");
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        if (collaboratoreServizioTo.getIdCollaboratore() == null
                || collaboratoreServizioTo.getIdCollaboratore() <= 0
                || collaboratoreServizioTo.getIdServizio() == null
                || collaboratoreServizioTo.getIdServizio() <= 0) {

            log.warn("Salvataggio associazione collaboratore-servizio bloccato: id collaboratore o servizio non valido | idCollaboratore={} | idServizio={}",
                    collaboratoreServizioTo.getIdCollaboratore(),
                    collaboratoreServizioTo.getIdServizio());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (collaboratoreServizioTo.getIdCollaboratoreServizio() != null
                && collaboratoreServizioTo.getIdCollaboratoreServizio() <= 0) {

            log.warn("Modifica associazione collaboratore-servizio bloccata: id associazione non valido | idCollaboratoreServizio={}",
                    collaboratoreServizioTo.getIdCollaboratoreServizio());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
    }

    private CollaboratorePo recuperaCollaboratore(Integer idCollaboratore) {

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Associazione collaboratore-servizio bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        return collaboratorePo;
    }

    private ServizioPo recuperaServizio(Integer idServizio) {

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            log.warn("Associazione collaboratore-servizio bloccata: servizio non trovato | idServizio={}",
                    idServizio);

            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        return servizioPo;
    }

    private CollaboratoreServizioTo creaCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo,
                                                              CollaboratorePo collaboratorePo,
                                                              ServizioPo servizioPo) {

        log.info("Avvio creazione associazione collaboratore-servizio | idCollaboratore={} | idServizio={}",
                collaboratoreServizioTo.getIdCollaboratore(),
                collaboratoreServizioTo.getIdServizio());

        if (collaboratoreServizioRepository.existsAssociazioneAttiva(
                collaboratoreServizioTo.getIdCollaboratore(),
                collaboratoreServizioTo.getIdServizio())) {

            log.warn("Creazione associazione bloccata: associazione attiva già presente | idCollaboratore={} | idServizio={}",
                    collaboratoreServizioTo.getIdCollaboratore(),
                    collaboratoreServizioTo.getIdServizio());

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_GIA_PRESENTE);
        }

        try {
            CollaboratoreServizioPo collaboratoreServizioPo = new CollaboratoreServizioPo();

            collaboratoreServizioPo.setCollaboratorePo(collaboratorePo);
            collaboratoreServizioPo.setServizioPo(servizioPo);
            collaboratoreServizioPo.setAttivoCollaboratoreServizio(true);
            collaboratoreServizioPo.setDurataPersonalizzataMinuti(collaboratoreServizioTo.getDurataPersonalizzataMinuti());
            collaboratoreServizioPo.setPrezzoPersonalizzato(collaboratoreServizioTo.getPrezzoPersonalizzato());
            collaboratoreServizioPo.setNoteCollaboratoreServizio(collaboratoreServizioTo.getNoteCollaboratoreServizio());
            collaboratoreServizioPo.setDataCreazioneCollaboratoreServizio(LocalDateTime.now(clock));

            collaboratoreServizioPo = collaboratoreServizioRepository.save(collaboratoreServizioPo);

            log.info("Creazione associazione collaboratore-servizio completata | idCollaboratoreServizio={} | idCollaboratore={} | idServizio={}",
                    collaboratoreServizioPo.getIdCollaboratoreServizio(),
                    collaboratorePo.getIdCollaboratore(),
                    servizioPo.getIdServizio());

            return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);

        } catch (Exception e) {
            log.error("Creazione associazione collaboratore-servizio fallita per errore tecnico | idCollaboratore={} | idServizio={}",
                    collaboratoreServizioTo.getIdCollaboratore(),
                    collaboratoreServizioTo.getIdServizio(),
                    e);

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_CREAZIONE_FALLITA, e);
        }
    }

    private CollaboratoreServizioTo modificaCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo,
                                                                  CollaboratorePo collaboratorePo,
                                                                  ServizioPo servizioPo) {

        log.info("Avvio modifica associazione collaboratore-servizio | idCollaboratoreServizio={}",
                collaboratoreServizioTo.getIdCollaboratoreServizio());

        CollaboratoreServizioPo collaboratoreServizioPo = collaboratoreServizioRepository
                .findById(collaboratoreServizioTo.getIdCollaboratoreServizio())
                .orElse(null);

        if (collaboratoreServizioPo == null) {
            log.warn("Modifica associazione bloccata: associazione non trovata | idCollaboratoreServizio={}",
                    collaboratoreServizioTo.getIdCollaboratoreServizio());

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        try {
            collaboratoreServizioPo.setCollaboratorePo(collaboratorePo);
            collaboratoreServizioPo.setServizioPo(servizioPo);
            collaboratoreServizioPo.setDurataPersonalizzataMinuti(collaboratoreServizioTo.getDurataPersonalizzataMinuti());
            collaboratoreServizioPo.setPrezzoPersonalizzato(collaboratoreServizioTo.getPrezzoPersonalizzato());
            collaboratoreServizioPo.setNoteCollaboratoreServizio(collaboratoreServizioTo.getNoteCollaboratoreServizio());
            collaboratoreServizioPo.setDataModificaCollaboratoreServizio(LocalDateTime.now(clock));

            if (collaboratoreServizioTo.getAttivoCollaboratoreServizio() != null) {
                collaboratoreServizioPo.setAttivoCollaboratoreServizio(
                        collaboratoreServizioTo.getAttivoCollaboratoreServizio()
                );
            }

            collaboratoreServizioPo = collaboratoreServizioRepository.save(collaboratoreServizioPo);

            log.info("Modifica associazione collaboratore-servizio completata | idCollaboratoreServizio={} | idCollaboratore={} | idServizio={} | attivo={}",
                    collaboratoreServizioPo.getIdCollaboratoreServizio(),
                    collaboratorePo.getIdCollaboratore(),
                    servizioPo.getIdServizio(),
                    collaboratoreServizioPo.getAttivoCollaboratoreServizio());

            return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);

        } catch (Exception e) {
            log.error("Modifica associazione collaboratore-servizio fallita per errore tecnico | idCollaboratoreServizio={}",
                    collaboratoreServizioTo.getIdCollaboratoreServizio(),
                    e);

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CollaboratoreServizioTo getCollaboratoreServizioById(Integer idCollaboratoreServizio) {

        log.info("Ricerca associazione collaboratore-servizio per id | idCollaboratoreServizio={}",
                idCollaboratoreServizio);

        if (idCollaboratoreServizio == null || idCollaboratoreServizio <= 0) {
            log.warn("Ricerca associazione bloccata: id non valido | idCollaboratoreServizio={}",
                    idCollaboratoreServizio);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratoreServizioPo collaboratoreServizioPo = collaboratoreServizioRepository
                .findById(idCollaboratoreServizio)
                .orElse(null);

        if (collaboratoreServizioPo == null) {
            log.warn("Ricerca associazione fallita: associazione non trovata | idCollaboratoreServizio={}",
                    idCollaboratoreServizio);

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        log.info("Ricerca associazione completata | idCollaboratoreServizio={}",
                idCollaboratoreServizio);

        return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);
    }

    @Override
    public Page<CollaboratoreServizioTo> getAllCollaboratoriServizi(Pageable pageable) {

        log.info("Ricerca associazioni collaboratore-servizio paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<CollaboratoreServizioTo> risultato = collaboratoreServizioRepository.findAll(pageable)
                .map(collaboratoreServizioMapper::toDto);

        log.info("Ricerca associazioni collaboratore-servizio completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public List<CollaboratoreServizioTo> getServiziByCollaboratore(Integer idCollaboratore) {

        log.info("Ricerca servizi per collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Ricerca servizi per collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        List<CollaboratoreServizioTo> risultato =
                collaboratoreServizioRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                        .stream()
                        .map(collaboratoreServizioMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca servizi per collaboratore completata | idCollaboratore={} | elementi={}",
                idCollaboratore,
                risultato.size());

        return risultato;
    }

    @Override
    public List<CollaboratoreServizioTo> getCollaboratoriByServizio(Integer idServizio) {

        log.info("Ricerca collaboratori per servizio | idServizio={}", idServizio);

        if (idServizio == null || idServizio <= 0) {
            log.warn("Ricerca collaboratori per servizio bloccata: id non valido | idServizio={}",
                    idServizio);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        List<CollaboratoreServizioTo> risultato =
                collaboratoreServizioRepository.findByServizioPo_IdServizio(idServizio)
                        .stream()
                        .map(collaboratoreServizioMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca collaboratori per servizio completata | idServizio={} | elementi={}",
                idServizio,
                risultato.size());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteCollaboratoreServizio(Integer idCollaboratoreServizio) {

        log.info("Avvio eliminazione associazione collaboratore-servizio | idCollaboratoreServizio={}",
                idCollaboratoreServizio);

        if (idCollaboratoreServizio == null || idCollaboratoreServizio <= 0) {
            log.warn("Eliminazione associazione bloccata: id non valido | idCollaboratoreServizio={}",
                    idCollaboratoreServizio);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreServizioRepository.existsById(idCollaboratoreServizio)) {
            log.warn("Eliminazione associazione bloccata: associazione non trovata | idCollaboratoreServizio={}",
                    idCollaboratoreServizio);

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        try {
            collaboratoreServizioRepository.deleteById(idCollaboratoreServizio);

            log.info("Eliminazione associazione collaboratore-servizio completata | idCollaboratoreServizio={}",
                    idCollaboratoreServizio);

            return new EsitoResponse("Associazione collaboratore servizio eliminata correttamente");

        } catch (Exception e) {
            log.error("Eliminazione associazione collaboratore-servizio fallita per errore tecnico | idCollaboratoreServizio={}",
                    idCollaboratoreServizio,
                    e);

            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_ELIMINAZIONE_FALLITA, e);
        }
    }
}