package com.prenotazioni.service;

import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.PreventivoTo;
import com.prenotazioni.enums.StatoPreventivo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.PreventivoMapper;
import com.prenotazioni.po.PreventivoPo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.po.UtentePo;
import com.prenotazioni.response.EsitoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class PreventivoServiceImpl implements PreventivoService {

    private static final Logger log = LoggerFactory.getLogger(PreventivoServiceImpl.class);

    private final PreventivoRepository preventivoRepository;
    private final UtenteRepository utenteRepository;
    private final ServizioRepository servizioRepository;
    private final PreventivoMapper preventivoMapper;
    private final Clock clock;

    public PreventivoServiceImpl(PreventivoRepository preventivoRepository,
                                 UtenteRepository utenteRepository,
                                 ServizioRepository servizioRepository,
                                 PreventivoMapper preventivoMapper,
                                 Clock clock) {
        this.preventivoRepository = preventivoRepository;
        this.utenteRepository = utenteRepository;
        this.servizioRepository = servizioRepository;
        this.preventivoMapper = preventivoMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PreventivoTo richiediPreventivo(PreventivoTo preventivoTo) {

        log.info("Avvio richiesta preventivo");

        validaRichiestaNuovoPreventivo(preventivoTo);

        UtentePo utentePo = recuperaUtenteAttivo(preventivoTo.getIdUtente());

        ServizioPo servizioPo = recuperaServizioAttivoSePresente(preventivoTo.getIdServizio());

        try {
            PreventivoPo preventivoPo = new PreventivoPo();

            preventivoPo.setUtentePo(utentePo);
            preventivoPo.setServizioPo(servizioPo);
            preventivoPo.setTitoloPreventivo(preventivoTo.getTitoloPreventivo().trim());
            preventivoPo.setDescrizioneRichiestaPreventivo(preventivoTo.getDescrizioneRichiestaPreventivo().trim());
            preventivoPo.setImportoPreventivo(null);
            preventivoPo.setRispostaPreventivo(null);
            preventivoPo.setDataScadenzaPreventivo(null);
            preventivoPo.setStatoPreventivo(StatoPreventivo.RICHIESTO);
            preventivoPo.setDataCreazionePreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Richiesta preventivo completata | idPreventivo={} | idUtente={} | idServizio={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    utentePo.getIdUtente(),
                    servizioPo != null ? servizioPo.getIdServizio() : null,
                    preventivoPo.getStatoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Richiesta preventivo fallita per errore tecnico | idUtente={} | idServizio={}",
                    preventivoTo.getIdUtente(),
                    preventivoTo.getIdServizio(),
                    e);

            throw new ServiceException(AppError.PREVENTIVO_CREAZIONE_FALLITA, e);
        }
    }

    private void validaRichiestaNuovoPreventivo(PreventivoTo preventivoTo) {

        if (preventivoTo == null) {
            log.warn("Richiesta preventivo bloccata: request nulla");
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }

        if (preventivoTo.getIdUtente() == null || preventivoTo.getIdUtente() <= 0) {
            log.warn("Richiesta preventivo bloccata: id utente non valido | idUtente={}",
                    preventivoTo.getIdUtente());
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (preventivoTo.getTitoloPreventivo() == null
                || preventivoTo.getTitoloPreventivo().trim().isEmpty()
                || preventivoTo.getDescrizioneRichiestaPreventivo() == null
                || preventivoTo.getDescrizioneRichiestaPreventivo().trim().isEmpty()) {

            log.warn("Richiesta preventivo bloccata: titolo o descrizione non validi | idUtente={}",
                    preventivoTo.getIdUtente());

            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }
    }

    private UtentePo recuperaUtenteAttivo(Integer idUtente) {

        UtentePo utentePo = utenteRepository.findById(idUtente).orElse(null);

        if (utentePo == null) {
            log.warn("Operazione preventivo bloccata: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        if (!Boolean.TRUE.equals(utentePo.getAttivoUtente())) {
            log.warn("Operazione preventivo bloccata: utente non attivo | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_ATTIVO);
        }

        return utentePo;
    }

    private ServizioPo recuperaServizioAttivoSePresente(Integer idServizio) {

        if (idServizio == null) {
            return null;
        }

        if (idServizio <= 0) {
            log.warn("Operazione preventivo bloccata: id servizio non valido | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository.findById(idServizio).orElse(null);

        if (servizioPo == null) {
            log.warn("Operazione preventivo bloccata: servizio non trovato | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (!Boolean.TRUE.equals(servizioPo.getAttivoServizio())) {
            log.warn("Operazione preventivo bloccata: servizio non attivo | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_ATTIVO);
        }

        return servizioPo;
    }

    @Override
    @Transactional
    public PreventivoTo modificaPreventivo(PreventivoTo preventivoTo) {

        log.info("Avvio modifica preventivo | idPreventivo={}",
                preventivoTo != null ? preventivoTo.getIdPreventivo() : null);

        validaRichiestaModificaPreventivo(preventivoTo);

        PreventivoPo preventivoPo = recuperaPreventivo(preventivoTo.getIdPreventivo());

        verificaPreventivoModificabile(preventivoPo);

        UtentePo utentePo = recuperaUtente(preventivoTo.getIdUtente());

        ServizioPo servizioPo = recuperaServizioSePresente(preventivoTo.getIdServizio());

        validaDatiModificaPreventivo(preventivoTo);

        applicaModifichePreventivo(preventivoPo, preventivoTo, utentePo, servizioPo);

        return salvaModificaPreventivo(preventivoPo);
    }

    private void validaRichiestaModificaPreventivo(PreventivoTo preventivoTo) {

        if (preventivoTo == null) {
            log.warn("Modifica preventivo bloccata: request nulla");
            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }

        if (preventivoTo.getIdPreventivo() == null || preventivoTo.getIdPreventivo() <= 0) {
            log.warn("Modifica preventivo bloccata: id preventivo non valido | idPreventivo={}",
                    preventivoTo.getIdPreventivo());
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (preventivoTo.getIdUtente() == null || preventivoTo.getIdUtente() <= 0) {
            log.warn("Modifica preventivo bloccata: id utente non valido | idUtente={}",
                    preventivoTo.getIdUtente());
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }
    }

    private PreventivoPo recuperaPreventivo(Integer idPreventivo) {

        PreventivoPo preventivoPo = preventivoRepository
                .findById(idPreventivo)
                .orElse(null);

        if (preventivoPo == null) {
            log.warn("Preventivo non trovato | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }

        return preventivoPo;
    }

    private void verificaPreventivoModificabile(PreventivoPo preventivoPo) {

        if (preventivoPo.getStatoPreventivo() == StatoPreventivo.ACCETTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.RIFIUTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.SCADUTO) {

            log.warn("Preventivo non modificabile | idPreventivo={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }
    }

    private UtentePo recuperaUtente(Integer idUtente) {

        UtentePo utentePo = utenteRepository
                .findById(idUtente)
                .orElse(null);

        if (utentePo == null) {
            log.warn("Utente non trovato per preventivo | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        return utentePo;
    }

    private ServizioPo recuperaServizioSePresente(Integer idServizio) {

        if (idServizio == null) {
            return null;
        }

        if (idServizio <= 0) {
            log.warn("Servizio non valido per preventivo | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            log.warn("Servizio non trovato per preventivo | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        return servizioPo;
    }

    private void validaDatiModificaPreventivo(PreventivoTo preventivoTo) {

        if (preventivoTo.getTitoloPreventivo() == null
                || preventivoTo.getTitoloPreventivo().trim().isEmpty()
                || preventivoTo.getDescrizioneRichiestaPreventivo() == null
                || preventivoTo.getDescrizioneRichiestaPreventivo().trim().isEmpty()) {

            log.warn("Modifica preventivo bloccata: titolo o descrizione non validi | idPreventivo={}",
                    preventivoTo.getIdPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_VALIDO);
        }

        if (preventivoTo.getImportoPreventivo() != null
                && preventivoTo.getImportoPreventivo().compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Modifica preventivo bloccata: importo non valido | idPreventivo={} | importo={}",
                    preventivoTo.getIdPreventivo(),
                    preventivoTo.getImportoPreventivo());

            throw new ServiceException(AppError.IMPORTO_PREVENTIVO_NON_VALIDO);
        }
    }

    private void applicaModifichePreventivo(PreventivoPo preventivoPo,
                                            PreventivoTo preventivoTo,
                                            UtentePo utentePo,
                                            ServizioPo servizioPo) {

        preventivoPo.setUtentePo(utentePo);
        preventivoPo.setServizioPo(servizioPo);
        preventivoPo.setTitoloPreventivo(preventivoTo.getTitoloPreventivo().trim());
        preventivoPo.setDescrizioneRichiestaPreventivo(
                preventivoTo.getDescrizioneRichiestaPreventivo().trim()
        );
        preventivoPo.setImportoPreventivo(preventivoTo.getImportoPreventivo());
        preventivoPo.setRispostaPreventivo(preventivoTo.getRispostaPreventivo());
        preventivoPo.setDataScadenzaPreventivo(preventivoTo.getDataScadenzaPreventivo());
        preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));
    }

    private PreventivoTo salvaModificaPreventivo(PreventivoPo preventivoPo) {

        try {
            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Modifica preventivo completata | idPreventivo={} | stato={} | importo={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo(),
                    preventivoPo.getImportoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Modifica preventivo fallita per errore tecnico | idPreventivo={}",
                    preventivoPo.getIdPreventivo(),
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo prendiInLavorazionePreventivo(Integer idPreventivo) {

        log.info("Avvio presa in lavorazione preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Presa in lavorazione preventivo bloccata: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.RICHIESTO) {
            log.warn("Presa in lavorazione preventivo bloccata: stato non valido | idPreventivo={} | stato={}",
                    idPreventivo,
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.IN_LAVORAZIONE);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Presa in lavorazione preventivo completata | idPreventivo={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Presa in lavorazione preventivo fallita per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo inviaPreventivo(Integer idPreventivo) {

        log.info("Avvio invio preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Invio preventivo bloccato: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.RICHIESTO
                && preventivoPo.getStatoPreventivo() != StatoPreventivo.IN_LAVORAZIONE) {

            log.warn("Invio preventivo bloccato: stato non inviabile | idPreventivo={} | stato={}",
                    idPreventivo,
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_INVIABILE);
        }

        if (preventivoPo.getImportoPreventivo() == null
                || preventivoPo.getImportoPreventivo().compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Invio preventivo bloccato: importo non valido | idPreventivo={} | importo={}",
                    idPreventivo,
                    preventivoPo.getImportoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_INVIABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.INVIATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Invio preventivo completato | idPreventivo={} | stato={} | importo={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo(),
                    preventivoPo.getImportoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Invio preventivo fallito per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo accettaPreventivo(Integer idPreventivo) {

        log.info("Avvio accettazione preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Accettazione preventivo bloccata: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.INVIATO) {
            log.warn("Accettazione preventivo bloccata: stato non accettabile | idPreventivo={} | stato={}",
                    idPreventivo,
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_ACCETTABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.ACCETTATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Accettazione preventivo completata | idPreventivo={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Accettazione preventivo fallita per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo rifiutaPreventivo(Integer idPreventivo) {

        log.info("Avvio rifiuto preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Rifiuto preventivo bloccato: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        if (preventivoPo.getStatoPreventivo() != StatoPreventivo.INVIATO) {
            log.warn("Rifiuto preventivo bloccato: stato non rifiutabile | idPreventivo={} | stato={}",
                    idPreventivo,
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_RIFIUTABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.RIFIUTATO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Rifiuto preventivo completato | idPreventivo={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Rifiuto preventivo fallito per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public PreventivoTo getPreventivoById(Integer idPreventivo) {

        log.info("Ricerca preventivo per id | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Ricerca preventivo bloccata: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        log.info("Ricerca preventivo completata | idPreventivo={} | stato={}",
                preventivoPo.getIdPreventivo(),
                preventivoPo.getStatoPreventivo());

        return preventivoMapper.toDto(preventivoPo);
    }

    @Override
    public Page<PreventivoTo> getAllPreventivi(Pageable pageable) {

        log.info("Ricerca preventivi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<PreventivoTo> risultato = preventivoRepository.findAll(pageable)
                .map(preventivoMapper::toDto);

        log.info("Ricerca preventivi completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public Page<PreventivoTo> getPreventiviByUtente(Integer idUtente, Pageable pageable) {

        log.info("Ricerca preventivi per utente | idUtente={} | page={} | size={} | sort={}",
                idUtente,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        if (idUtente == null || idUtente <= 0) {
            log.warn("Ricerca preventivi per utente bloccata: id non valido | idUtente={}", idUtente);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!utenteRepository.existsById(idUtente)) {
            log.warn("Ricerca preventivi per utente bloccata: utente non trovato | idUtente={}", idUtente);
            throw new ServiceException(AppError.UTENTE_NON_TROVATO);
        }

        Page<PreventivoTo> risultato = preventivoRepository.findByUtentePo_IdUtente(idUtente, pageable)
                .map(preventivoMapper::toDto);

        log.info("Ricerca preventivi per utente completata | idUtente={} | elementiPagina={} | totaleElementi={} | totalePagine={}",
                idUtente,
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public Page<PreventivoTo> getPreventiviByStato(String statoPreventivo, Pageable pageable) {

        log.info("Ricerca preventivi per stato | statoPreventivo={} | page={} | size={} | sort={}",
                statoPreventivo,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        StatoPreventivo stato;

        try {
            stato = StatoPreventivo.valueOf(statoPreventivo.toUpperCase());
        } catch (Exception e) {
            log.warn("Ricerca preventivi per stato bloccata: stato non valido | statoPreventivo={}",
                    statoPreventivo);

            throw new ServiceException(AppError.STATO_PREVENTIVO_NON_VALIDO, e);
        }

        Page<PreventivoTo> risultato = preventivoRepository.findByStatoPreventivo(stato, pageable)
                .map(preventivoMapper::toDto);

        log.info("Ricerca preventivi per stato completata | stato={} | elementiPagina={} | totaleElementi={} | totalePagine={}",
                stato,
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deletePreventivo(Integer idPreventivo) {

        log.info("Avvio eliminazione preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Eliminazione preventivo bloccata: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!preventivoRepository.existsById(idPreventivo)) {
            log.warn("Eliminazione preventivo bloccata: preventivo non trovato | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.PREVENTIVO_NON_TROVATO);
        }

        try {
            preventivoRepository.deleteById(idPreventivo);

            log.info("Eliminazione preventivo completata | idPreventivo={}", idPreventivo);

            return new EsitoResponse("Preventivo eliminato correttamente");

        } catch (Exception e) {
            log.error("Eliminazione preventivo fallita per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public PreventivoTo chiudiManualmentePreventivo(Integer idPreventivo) {

        log.info("Avvio chiusura manuale preventivo | idPreventivo={}", idPreventivo);

        if (idPreventivo == null || idPreventivo <= 0) {
            log.warn("Chiusura manuale preventivo bloccata: id non valido | idPreventivo={}", idPreventivo);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        PreventivoPo preventivoPo = recuperaPreventivo(idPreventivo);

        if (preventivoPo.getStatoPreventivo() == StatoPreventivo.ACCETTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.RIFIUTATO
                || preventivoPo.getStatoPreventivo() == StatoPreventivo.SCADUTO) {

            log.warn("Chiusura manuale preventivo bloccata: stato non modificabile | idPreventivo={} | stato={}",
                    idPreventivo,
                    preventivoPo.getStatoPreventivo());

            throw new ServiceException(AppError.PREVENTIVO_NON_MODIFICABILE);
        }

        try {
            preventivoPo.setStatoPreventivo(StatoPreventivo.SCADUTO);
            preventivoPo.setDataModificaPreventivo(LocalDateTime.now(clock));

            preventivoPo = preventivoRepository.save(preventivoPo);

            log.info("Chiusura manuale preventivo completata | idPreventivo={} | stato={}",
                    preventivoPo.getIdPreventivo(),
                    preventivoPo.getStatoPreventivo());

            return preventivoMapper.toDto(preventivoPo);

        } catch (Exception e) {
            log.error("Chiusura manuale preventivo fallita per errore tecnico | idPreventivo={}",
                    idPreventivo,
                    e);

            throw new ServiceException(AppError.PREVENTIVO_MODIFICA_FALLITA, e);
        }
    }
}