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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServizioServiceImpl implements ServizioService {

    private static final Logger log = LoggerFactory.getLogger(ServizioServiceImpl.class);

    private final Clock clock;
    private final ServizioRepository servizioRepository;
    private final ServizioMapper servizioMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final PreventivoRepository preventivoRepository;

    public ServizioServiceImpl(ServizioRepository servizioRepository, Clock clock ,ServizioMapper servizioMapper, PrenotazioneRepository prenotazioneRepository, CollaboratoreServizioRepository collaboratoreServizioRepository, PreventivoRepository preventivoRepository) {
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

        TipoGestioneServizio tipoGestioneServizio =
                convertiTipoGestioneServizio(servizioTo);

        validaConfigurazioneTipoGestioneServizio(
                servizioTo,
                tipoGestioneServizio
        );

        if (servizioTo.getIdServizio() == null) {
            return creaServizio(
                    servizioTo,
                    tipoGestioneServizio
            );
        }

        return modificaServizio(
                servizioTo,
                tipoGestioneServizio
        );
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

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            throw new ServiceException(AppError.SERVIZIO_CREAZIONE_FALLITA, e);
        }
    }
    private void verificaNomeServizioDisponibile(String nomeServizio) {

        if (servizioRepository.existsByNomeServizio(nomeServizio)) {
            throw new ServiceException(AppError.SERVIZIO_GIA_PRESENTE);
        }
    }
    private ServizioTo modificaServizio(ServizioTo servizioTo,
                                        TipoGestioneServizio tipoGestioneServizio) {

        ServizioPo servizioPo = recuperaServizioDaModificare(
                servizioTo.getIdServizio()
        );

        verificaNomeServizioDisponibilePerModifica(
                servizioTo,
                servizioPo
        );

        applicaModificheServizio(
                servizioPo,
                servizioTo,
                tipoGestioneServizio
        );

        return salvaModificaServizio(servizioPo);
    }
    private ServizioPo recuperaServizioDaModificare(Integer idServizio) {

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        return servizioPo;
    }
    private void verificaNomeServizioDisponibilePerModifica(ServizioTo servizioTo,
                                                            ServizioPo servizioPo) {

        boolean nomeServizioCambiato =
                !servizioTo.getNomeServizio().equalsIgnoreCase(
                        servizioPo.getNomeServizio()
                );

        if (nomeServizioCambiato
                && servizioRepository.existsByNomeServizio(servizioTo.getNomeServizio())) {
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

            return servizioMapper.toDto(servizioPo);

        } catch (Exception e) {
            throw new ServiceException(AppError.SERVIZIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public ServizioTo getServizioById(Integer idServizio) {

        if (idServizio == null || idServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository.findById(idServizio).orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        return servizioMapper.toDto(servizioPo);
    }

    @Override
    public List<ServizioTo> getAllServizi() {
        return servizioRepository.findAll()
                .stream()
                .map(servizioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServizioTo> getServiziAttivi() {
        return servizioRepository.findAll()
                .stream()
                .filter(servizioPo -> Boolean.TRUE.equals(servizioPo.getAttivoServizio()))
                .map(servizioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteServizio(Integer idServizio) {

        if (idServizio == null || idServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!servizioRepository.existsById(idServizio)) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByServizioPo_IdServizio(idServizio)
                || collaboratoreServizioRepository.existsByServizioPo_IdServizio(idServizio)) {
            throw new ServiceException(AppError.SERVIZIO_NON_ELIMINABILE);
        }
        if (preventivoRepository.existsByServizioPo_IdServizio((idServizio))) {
            throw new ServiceException(AppError.SERVIZIO_CON_PREVENTIVI_ASSOCIATI);
        }


        try {
            servizioRepository.deleteById(idServizio);
            return new EsitoResponse("Servizio eliminato correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.SERVIZIO_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public ServizioTo disattivaServizio(Integer idServizio) {

        if (idServizio == null || idServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        servizioPo.setAttivoServizio(false);

        servizioPo = servizioRepository.save(servizioPo);
        log.info("Servizio disattivato | idServizio={}", idServizio);

        return servizioMapper.toDto(servizioPo);
    }

    @Override
    @Transactional
    public ServizioTo riattivaServizio(Integer idServizio) {

        if (idServizio == null || idServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(idServizio)
                .orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        servizioPo.setAttivoServizio(true);

        servizioPo = servizioRepository.save(servizioPo);
        log.info("Servizio riattivato | idServizio={}", idServizio);

        return servizioMapper.toDto(servizioPo);
    }
}

