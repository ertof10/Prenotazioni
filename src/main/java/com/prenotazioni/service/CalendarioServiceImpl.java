package com.prenotazioni.service;

import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CalendarioMapper;
import com.prenotazioni.po.CalendarioPo;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarioServiceImpl implements CalendarioService {

    private static final Logger log = LoggerFactory.getLogger(CalendarioServiceImpl.class);

    private final CalendarioRepository calendarioRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final CalendarioMapper calendarioMapper;
    private final Clock clock;

    public CalendarioServiceImpl(CalendarioRepository calendarioRepository,
                                 CollaboratoreRepository collaboratoreRepository,
                                 CalendarioMapper calendarioMapper,
                                 Clock clock) {
        this.calendarioRepository = calendarioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.calendarioMapper = calendarioMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CalendarioTo saveOrUpdateCalendario(CalendarioTo calendarioTo) {

        log.info("Avvio salvataggio calendario | idCalendario={} | idCollaboratore={} | giorno={}",
                calendarioTo != null ? calendarioTo.getIdCalendario() : null,
                calendarioTo != null ? calendarioTo.getIdCollaboratore() : null,
                calendarioTo != null ? calendarioTo.getGiornoSettimanaCalendario() : null);

        validaRichiestaCalendario(calendarioTo);

        CollaboratorePo collaboratorePo = recuperaCollaboratoreAttivo(calendarioTo.getIdCollaboratore());

        DayOfWeek giornoSettimanaCalendario = convertiGiornoSettimana(calendarioTo);

        verificaSovrapposizioneCalendario(calendarioTo, giornoSettimanaCalendario);

        if (calendarioTo.getIdCalendario() == null) {
            return creaCalendario(calendarioTo, collaboratorePo, giornoSettimanaCalendario);
        }

        return modificaCalendario(calendarioTo, collaboratorePo, giornoSettimanaCalendario);
    }

    private void validaRichiestaCalendario(CalendarioTo calendarioTo) {

        if (calendarioTo == null) {
            log.warn("Salvataggio calendario bloccato: request nulla");
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        if (calendarioTo.getIdCalendario() != null && calendarioTo.getIdCalendario() <= 0) {
            log.warn("Salvataggio calendario bloccato: id calendario non valido | idCalendario={}",
                    calendarioTo.getIdCalendario());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (calendarioTo.getIdCollaboratore() == null || calendarioTo.getIdCollaboratore() <= 0) {
            log.warn("Salvataggio calendario bloccato: id collaboratore non valido | idCollaboratore={}",
                    calendarioTo.getIdCollaboratore());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (calendarioTo.getGiornoSettimanaCalendario() == null
                || calendarioTo.getGiornoSettimanaCalendario().trim().isEmpty()
                || calendarioTo.getOraInizioCalendario() == null
                || calendarioTo.getOraFineCalendario() == null) {

            log.warn("Salvataggio calendario bloccato: giorno o orari mancanti | idCollaboratore={}",
                    calendarioTo.getIdCollaboratore());

            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        if (calendarioTo.getOraFineCalendario().isBefore(calendarioTo.getOraInizioCalendario())
                || calendarioTo.getOraFineCalendario().equals(calendarioTo.getOraInizioCalendario())) {

            log.warn("Salvataggio calendario bloccato: orario non valido | idCollaboratore={} | oraInizio={} | oraFine={}",
                    calendarioTo.getIdCollaboratore(),
                    calendarioTo.getOraInizioCalendario(),
                    calendarioTo.getOraFineCalendario());

            throw new ServiceException(AppError.ORARIO_CALENDARIO_NON_VALIDO);
        }
    }

    private CollaboratorePo recuperaCollaboratoreAttivo(Integer idCollaboratore) {

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Salvataggio calendario bloccato: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (!Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore())) {
            log.warn("Salvataggio calendario bloccato: collaboratore non attivo | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_ATTIVO);
        }

        return collaboratorePo;
    }

    private DayOfWeek convertiGiornoSettimana(CalendarioTo calendarioTo) {

        try {
            return DayOfWeek.valueOf(
                    calendarioTo.getGiornoSettimanaCalendario().trim().toUpperCase()
            );

        } catch (Exception e) {
            log.warn("Salvataggio calendario bloccato: giorno settimana non valido | valore={}",
                    calendarioTo.getGiornoSettimanaCalendario());

            throw new ServiceException(AppError.GIORNO_CALENDARIO_NON_VALIDO, e);
        }
    }

    private void verificaSovrapposizioneCalendario(CalendarioTo calendarioTo,
                                                   DayOfWeek giornoSettimanaCalendario) {

        boolean calendarioSovrapposto =
                calendarioRepository.existsCalendarioSovrapposto(
                        calendarioTo.getIdCalendario(),
                        calendarioTo.getIdCollaboratore(),
                        giornoSettimanaCalendario,
                        calendarioTo.getOraInizioCalendario(),
                        calendarioTo.getOraFineCalendario()
                );

        if (calendarioSovrapposto) {
            log.warn("Salvataggio calendario bloccato: fascia oraria sovrapposta | idCollaboratore={} | giorno={} | oraInizio={} | oraFine={}",
                    calendarioTo.getIdCollaboratore(),
                    giornoSettimanaCalendario,
                    calendarioTo.getOraInizioCalendario(),
                    calendarioTo.getOraFineCalendario());

            throw new ServiceException(AppError.CALENDARIO_GIA_PRESENTE);
        }
    }

    private CalendarioTo creaCalendario(CalendarioTo calendarioTo,
                                        CollaboratorePo collaboratorePo,
                                        DayOfWeek giornoSettimanaCalendario) {

        log.info("Avvio creazione calendario | idCollaboratore={} | giorno={} | oraInizio={} | oraFine={}",
                collaboratorePo.getIdCollaboratore(),
                giornoSettimanaCalendario,
                calendarioTo.getOraInizioCalendario(),
                calendarioTo.getOraFineCalendario());

        try {
            CalendarioPo calendarioPo = new CalendarioPo();

            calendarioPo.setCollaboratorePo(collaboratorePo);
            calendarioPo.setGiornoSettimanaCalendario(giornoSettimanaCalendario);
            calendarioPo.setOraInizioCalendario(calendarioTo.getOraInizioCalendario());
            calendarioPo.setOraFineCalendario(calendarioTo.getOraFineCalendario());
            calendarioPo.setAttivoCalendario(true);
            calendarioPo.setDataCreazioneCalendario(LocalDateTime.now(clock));

            calendarioPo = calendarioRepository.save(calendarioPo);

            log.info("Creazione calendario completata | idCalendario={} | idCollaboratore={} | giorno={}",
                    calendarioPo.getIdCalendario(),
                    collaboratorePo.getIdCollaboratore(),
                    calendarioPo.getGiornoSettimanaCalendario());

            return calendarioMapper.toDto(calendarioPo);

        } catch (Exception e) {
            log.error("Creazione calendario fallita per errore tecnico | idCollaboratore={} | giorno={}",
                    collaboratorePo.getIdCollaboratore(),
                    giornoSettimanaCalendario,
                    e);

            throw new ServiceException(AppError.CALENDARIO_CREAZIONE_FALLITA, e);
        }
    }

    private CalendarioTo modificaCalendario(CalendarioTo calendarioTo,
                                            CollaboratorePo collaboratorePo,
                                            DayOfWeek giornoSettimanaCalendario) {

        log.info("Avvio modifica calendario | idCalendario={} | idCollaboratore={} | giorno={}",
                calendarioTo.getIdCalendario(),
                collaboratorePo.getIdCollaboratore(),
                giornoSettimanaCalendario);

        CalendarioPo calendarioPo = calendarioRepository
                .findById(calendarioTo.getIdCalendario())
                .orElse(null);

        if (calendarioPo == null) {
            log.warn("Modifica calendario bloccata: calendario non trovato | idCalendario={}",
                    calendarioTo.getIdCalendario());

            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        try {
            calendarioPo.setCollaboratorePo(collaboratorePo);
            calendarioPo.setGiornoSettimanaCalendario(giornoSettimanaCalendario);
            calendarioPo.setOraInizioCalendario(calendarioTo.getOraInizioCalendario());
            calendarioPo.setOraFineCalendario(calendarioTo.getOraFineCalendario());
            calendarioPo.setDataModificaCalendario(LocalDateTime.now(clock));

            if (calendarioTo.getAttivoCalendario() != null) {
                calendarioPo.setAttivoCalendario(calendarioTo.getAttivoCalendario());
            }

            calendarioPo = calendarioRepository.save(calendarioPo);

            log.info("Modifica calendario completata | idCalendario={} | idCollaboratore={} | giorno={} | attivo={}",
                    calendarioPo.getIdCalendario(),
                    collaboratorePo.getIdCollaboratore(),
                    calendarioPo.getGiornoSettimanaCalendario(),
                    calendarioPo.getAttivoCalendario());

            return calendarioMapper.toDto(calendarioPo);

        } catch (Exception e) {
            log.error("Modifica calendario fallita per errore tecnico | idCalendario={}",
                    calendarioTo.getIdCalendario(),
                    e);

            throw new ServiceException(AppError.CALENDARIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CalendarioTo getCalendarioById(Integer idCalendario) {

        log.info("Ricerca calendario per id | idCalendario={}", idCalendario);

        if (idCalendario == null || idCalendario <= 0) {
            log.warn("Ricerca calendario bloccata: id non valido | idCalendario={}", idCalendario);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CalendarioPo calendarioPo = calendarioRepository.findById(idCalendario).orElse(null);

        if (calendarioPo == null) {
            log.warn("Ricerca calendario fallita: calendario non trovato | idCalendario={}", idCalendario);
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        log.info("Ricerca calendario completata | idCalendario={} | idCollaboratore={} | giorno={}",
                calendarioPo.getIdCalendario(),
                calendarioPo.getCollaboratorePo() != null
                        ? calendarioPo.getCollaboratorePo().getIdCollaboratore()
                        : null,
                calendarioPo.getGiornoSettimanaCalendario());

        return calendarioMapper.toDto(calendarioPo);
    }

    @Override
    public Page<CalendarioTo> getAllCalendari(Pageable pageable) {

        log.info("Ricerca calendari paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<CalendarioTo> risultato = calendarioRepository.findAll(pageable)
                .map(calendarioMapper::toDto);

        log.info("Ricerca calendari completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public List<CalendarioTo> getCalendariAttivi() {

        log.info("Ricerca calendari attivi");

        List<CalendarioTo> risultato = calendarioRepository.findByAttivoCalendarioTrue()
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());

        log.info("Ricerca calendari attivi completata | elementi={}",
                risultato.size());

        return risultato;
    }

    @Override
    public List<CalendarioTo> getCalendariByCollaboratore(Integer idCollaboratore) {

        log.info("Ricerca calendari per collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Ricerca calendari per collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        List<CalendarioTo> risultato = calendarioRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());

        log.info("Ricerca calendari per collaboratore completata | idCollaboratore={} | elementi={}",
                idCollaboratore,
                risultato.size());

        return risultato;
    }

    @Override
    public List<CalendarioTo> getCalendariAttiviByCollaboratore(Integer idCollaboratore) {

        log.info("Ricerca calendari attivi per collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Ricerca calendari attivi per collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        List<CalendarioTo> risultato =
                calendarioRepository.findByCollaboratorePo_IdCollaboratoreAndAttivoCalendarioTrue(idCollaboratore)
                        .stream()
                        .map(calendarioMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca calendari attivi per collaboratore completata | idCollaboratore={} | elementi={}",
                idCollaboratore,
                risultato.size());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteCalendario(Integer idCalendario) {

        log.info("Avvio eliminazione calendario | idCalendario={}", idCalendario);

        if (idCalendario == null || idCalendario <= 0) {
            log.warn("Eliminazione calendario bloccata: id non valido | idCalendario={}", idCalendario);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!calendarioRepository.existsById(idCalendario)) {
            log.warn("Eliminazione calendario bloccata: calendario non trovato | idCalendario={}", idCalendario);
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        try {
            calendarioRepository.deleteById(idCalendario);

            log.info("Eliminazione calendario completata | idCalendario={}", idCalendario);

            return new EsitoResponse("Calendario eliminato correttamente");

        } catch (Exception e) {
            log.error("Eliminazione calendario fallita per errore tecnico | idCalendario={}",
                    idCalendario,
                    e);

            throw new ServiceException(AppError.CALENDARIO_ELIMINAZIONE_FALLITA, e);
        }
    }
}