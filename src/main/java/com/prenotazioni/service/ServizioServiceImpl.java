package com.prenotazioni.service;

import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.enums.TipoGestioneServizio;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.ServizioMapper;
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

@Service
public class ServizioServiceImpl implements ServizioService {

    private static final Logger log = LoggerFactory.getLogger(ServizioServiceImpl.class);

    private final Clock clock;
    private final ServizioRepository servizioRepository;
    private final ServizioMapper servizioMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final PreventivoRepository preventivoRepository;

    public ServizioServiceImpl(ServizioRepository servizioRepository,
                               Clock clock,
                               ServizioMapper servizioMapper,
                               PrenotazioneRepository prenotazioneRepository,
                               CollaboratoreServizioRepository collaboratoreServizioRepository,
                               PreventivoRepository preventivoRepository) {
        this.servizioRepository = servizioRepository;
        this.clock = clock;
        this.servizioMapper = servizioMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.preventivoRepository = preventivoRepository;
    }

    @Override
    @Transactional
    public ServizioTo saveOrUpdateServizio(ServizioTo servizioTo) {

        normalizzaDatiServizio(servizioTo);

        TipoGestioneServizio tipoGestioneServizio = convertiTipoGestioneServizio(servizioTo);

        validaConfigurazioneTipoGestioneServizio(servizioTo, tipoGestioneServizio);

        if (servizioTo.getIdServizio() == null) {
            log.info("Avvio creazione servizio | nomeServizio={} | tipoGestioneServizio={}",
                    servizioTo.getNomeServizio(),
                    tipoGestioneServizio);

            return creaServizio(servizioTo, tipoGestioneServizio);
        }

        log.info("Avvio modifica servizio | idServizio={} | nomeServizio={} | tipoGestioneServizio={}",
                servizioTo.getIdServizio(),
                servizioTo.getNomeServizio(),
                tipoGestioneServizio);

        return modificaServizio(servizioTo, tipoGestioneServizio);
    }

    private void normalizzaDatiServizio(ServizioTo servizioTo) {

        String nomeServizio = servizioTo.getNomeServizio()
                .trim()
                .toUpperCase();

        String categoriaServizio = servizioTo.getCategoriaServizio()
                .trim()
                .toUpperCase();

        servizioTo.setNomeServizio(nomeServizio);
        servizioTo.setCategoriaServizio(categoriaServizio);
    }

    private TipoGestioneServizio convertiTipoGestioneServizio(ServizioTo servizioTo) {

        try {
            return TipoGestioneServizio.valueOf(
                    servizioTo.getTipoGestioneServizio().toUpperCase()
            );

        } catch (Exception e) {
            log.warn("Tipo gestione servizio non valido | tipoGestioneServizio={}",
                    servizioTo.getTipoGestioneServizio());

            throw new ServiceException(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO, e);
        }
    }

    private void validaConfigurazioneTipoGestioneServizio(ServizioTo servizioTo,
                                                          TipoGestioneServizio tipoGestioneServizio) {

        if (tipoGestioneServizio == TipoGestioneServizio.A_POSTI) {
            validaServizioAPosti(servizioTo);
        }

        if (tipoGestioneServizio == TipoGestioneServizio.CON_COLLABORATORE) {
            servizioTo.setPostiMassimiServizio(null);
        }
    }

    private void validaServizioAPosti(ServizioTo servizioTo) {

        if (servizioTo.getPostiMassimiServizio() == null
                || servizioTo.getPostiMassimiServizio() <= 0) {

            log.warn("Configurazione servizio A_POSTI non valida | nomeServizio={} | postiMassimiServizio={}",
                    servizioTo.getNomeServizio(),
                    servizioTo.getPostiMassimiServizio());

            throw new ServiceException(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO);
        }
    }

    private ServizioTo creaServizio(ServizioTo servizioTo,
                                    TipoGestioneServizio tipoGestioneServizio) {

        verificaNomeServizioDisponibile(servizioTo.getNomeServizio());

        try {
            ServizioPo servizioPo = servizioMapper.toEntity(servizioTo);

            servizioPo.setTipoGestioneServizio(tipoGestioneServizio);
            servizioPo.setPostiMassimiServizio(servizioTo.getPostiMassimiServizio());
            servizioPo.setAttivoServizio(true);
            servizioPo.setDataCreazioneServizio(LocalDateTime.now(clock));

            servizioPo = servizioRepository.save(servizioPo);

            log.info("Creazione servizio completata | idServizio={} | nomeServizio={} | tipoGestioneServizio={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getNomeServizio(),
                    servizioPo.getTipoGestioneServizio());

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            log.error("Creazione servizio fallita per errore tecnico | nomeServizio={}",
                    servizioTo.getNomeServizio(),
                    e);

            throw new ServiceException(AppError.SERVIZIO_CREAZIONE_FALLITA, e);
        }
    }

    private void verificaNomeServizioDisponibile(String nomeServizio) {

        if (servizioRepository.existsByNomeServizio(nomeServizio)) {
            log.warn("Creazione servizio bloccata: nome servizio già presente | nomeServizio={}",
                    nomeServizio);

            throw new ServiceException(AppError.SERVIZIO_GIA_PRESENTE);
        }
    }

    private ServizioTo modificaServizio(ServizioTo servizioTo,
                                        TipoGestioneServizio tipoGestioneServizio) {

        ServizioPo servizioPo = recuperaServizioDaModificare(servizioTo.getIdServizio());

        verificaNomeServizioDisponibilePerModifica(servizioTo, servizioPo);

        applicaModificheServizio(servizioPo, servizioTo, tipoGestioneServizio);

        return salvaModificaServizio(servizioPo);
    }

    private ServizioPo recuperaServizioDaModificare(Integer idServizio) {

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            log.warn("Modifica servizio bloccata: servizio non trovato | idServizio={}",
                    idServizio);

            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        return servizioPo;
    }

    private void verificaNomeServizioDisponibilePerModifica(ServizioTo servizioTo,
                                                            ServizioPo servizioPo) {

        boolean nomeServizioCambiato =
                !servizioTo.getNomeServizio().equalsIgnoreCase(servizioPo.getNomeServizio());

        if (nomeServizioCambiato
                && servizioRepository.existsByNomeServizio(servizioTo.getNomeServizio())) {

            log.warn("Modifica servizio bloccata: nuovo nome servizio già presente | idServizio={} | nomeServizio={}",
                    servizioTo.getIdServizio(),
                    servizioTo.getNomeServizio());

            throw new ServiceException(AppError.SERVIZIO_GIA_PRESENTE);
        }
    }

    private void applicaModificheServizio(ServizioPo servizioPo,
                                          ServizioTo servizioTo,
                                          TipoGestioneServizio tipoGestioneServizio) {

        servizioPo.setNomeServizio(servizioTo.getNomeServizio());
        servizioPo.setCategoriaServizio(servizioTo.getCategoriaServizio());
        servizioPo.setDescrizioneServizio(servizioTo.getDescrizioneServizio());
        servizioPo.setPrezzoServizio(servizioTo.getPrezzoServizio());
        servizioPo.setDurataMinutiServizio(servizioTo.getDurataMinutiServizio());
        servizioPo.setTipoGestioneServizio(tipoGestioneServizio);
        servizioPo.setPostiMassimiServizio(servizioTo.getPostiMassimiServizio());
        servizioPo.setUrlImmagineServizio(servizioTo.getUrlImmagineServizio());
        servizioPo.setDataModificaServizio(LocalDateTime.now(clock));

        if (servizioTo.getAttivoServizio() != null) {
            servizioPo.setAttivoServizio(servizioTo.getAttivoServizio());
        }
    }

    private ServizioTo salvaModificaServizio(ServizioPo servizioPo) {

        try {
            servizioPo = servizioRepository.save(servizioPo);

            log.info("Modifica servizio completata | idServizio={} | nomeServizio={} | attivoServizio={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getNomeServizio(),
                    servizioPo.getAttivoServizio());

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            log.error("Modifica servizio fallita per errore tecnico | idServizio={}",
                    servizioPo.getIdServizio(),
                    e);

            throw new ServiceException(AppError.SERVIZIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public ServizioTo getServizioById(Integer idServizio) {

        log.info("Ricerca servizio per id | idServizio={}", idServizio);

        if (idServizio == null || idServizio <= 0) {
            log.warn("Ricerca servizio bloccata: id non valido | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository.findById(idServizio).orElse(null);

        if (servizioPo == null) {
            log.warn("Ricerca servizio fallita: servizio non trovato | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        log.info("Ricerca servizio completata | idServizio={} | nomeServizio={}",
                servizioPo.getIdServizio(),
                servizioPo.getNomeServizio());

        return servizioMapper.toDto(servizioPo);
    }

    @Override
    public Page<ServizioTo> getAllServizi(Pageable pageable) {

        log.info("Ricerca servizi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<ServizioTo> risultato = servizioRepository.findAll(pageable)
                .map(servizioMapper::toDto);

        log.info("Ricerca servizi completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public Page<ServizioTo> getServiziAttivi(Pageable pageable) {

        log.info("Ricerca servizi attivi paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<ServizioTo> risultato = servizioRepository.findByAttivoServizioTrue(pageable)
                .map(servizioMapper::toDto);

        log.info("Ricerca servizi attivi completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteServizio(Integer idServizio) {

        log.info("Avvio eliminazione servizio | idServizio={}", idServizio);

        if (idServizio == null || idServizio <= 0) {
            log.warn("Eliminazione servizio bloccata: id non valido | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!servizioRepository.existsById(idServizio)) {
            log.warn("Eliminazione servizio bloccata: servizio non trovato | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByServizioPo_IdServizio(idServizio)
                || collaboratoreServizioRepository.existsByServizioPo_IdServizio(idServizio)) {

            log.warn("Eliminazione servizio bloccata: prenotazioni o collaboratori associati presenti | idServizio={}",
                    idServizio);

            throw new ServiceException(AppError.SERVIZIO_NON_ELIMINABILE);
        }

        if (preventivoRepository.existsByServizioPo_IdServizio(idServizio)) {
            log.warn("Eliminazione servizio bloccata: preventivi associati presenti | idServizio={}",
                    idServizio);

            throw new ServiceException(AppError.SERVIZIO_CON_PREVENTIVI_ASSOCIATI);
        }

        try {
            servizioRepository.deleteById(idServizio);

            log.info("Eliminazione servizio completata | idServizio={}", idServizio);

            return new EsitoResponse("Servizio eliminato correttamente");

        } catch (Exception e) {
            log.error("Eliminazione servizio fallita per errore tecnico | idServizio={}",
                    idServizio,
                    e);

            throw new ServiceException(AppError.SERVIZIO_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public ServizioTo disattivaServizio(Integer idServizio) {

        log.info("Avvio disattivazione servizio | idServizio={}", idServizio);

        if (idServizio == null || idServizio <= 0) {
            log.warn("Disattivazione servizio bloccata: id non valido | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            log.warn("Disattivazione servizio bloccata: servizio non trovato | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        try {
            servizioPo.setAttivoServizio(false);
            servizioPo.setDataModificaServizio(LocalDateTime.now(clock));

            servizioPo = servizioRepository.save(servizioPo);

            log.info("Disattivazione servizio completata | idServizio={} | nomeServizio={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getNomeServizio());

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            log.error("Disattivazione servizio fallita per errore tecnico | idServizio={}",
                    idServizio,
                    e);

            throw new ServiceException(AppError.SERVIZIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public ServizioTo riattivaServizio(Integer idServizio) {

        log.info("Avvio riattivazione servizio | idServizio={}", idServizio);

        if (idServizio == null || idServizio <= 0) {
            log.warn("Riattivazione servizio bloccata: id non valido | idServizio={}", idServizio);
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            log.warn("Riattivazione servizio bloccata: servizio non trovato | idServizio={}", idServizio);
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        try {
            servizioPo.setAttivoServizio(true);
            servizioPo.setDataModificaServizio(LocalDateTime.now(clock));

            servizioPo = servizioRepository.save(servizioPo);

            log.info("Riattivazione servizio completata | idServizio={} | nomeServizio={}",
                    servizioPo.getIdServizio(),
                    servizioPo.getNomeServizio());

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            log.error("Riattivazione servizio fallita per errore tecnico | idServizio={}",
                    idServizio,
                    e);

            throw new ServiceException(AppError.SERVIZIO_MODIFICA_FALLITA, e);
        }
    }
}