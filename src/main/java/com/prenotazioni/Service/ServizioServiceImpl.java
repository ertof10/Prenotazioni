package com.prenotazioni.Service;


import com.prenotazioni.Dao.CollaboratoreServizioRepository;
import com.prenotazioni.Dao.PrenotazioneRepository;
import com.prenotazioni.Dao.PreventivoRepository;
import com.prenotazioni.Dao.ServizioRepository;
import com.prenotazioni.Dto.ServizioTo;
import com.prenotazioni.Enums.TipoGestioneServizio;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.ServizioMapper;

import com.prenotazioni.Po.ServizioPo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServizioServiceImpl implements ServizioService {

    private final ServizioRepository servizioRepository;
    private final ServizioMapper servizioMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final PreventivoRepository preventivoRepository;

    public ServizioServiceImpl(ServizioRepository servizioRepository, ServizioMapper servizioMapper, PrenotazioneRepository prenotazioneRepository, CollaboratoreServizioRepository collaboratoreServizioRepository, PreventivoRepository preventivoRepository) {
        this.servizioRepository = servizioRepository;
        this.servizioMapper = servizioMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.preventivoRepository = preventivoRepository;
    }

    @Override
    @Transactional
    public ServizioTo saveOrUpdateServizio(ServizioTo servizioTo) {

        String nomeServizio = servizioTo.getNomeServizio().trim().toUpperCase();
        String categoriaServizio = servizioTo.getCategoriaServizio().trim().toUpperCase();

        servizioTo.setNomeServizio(nomeServizio);
        servizioTo.setCategoriaServizio(categoriaServizio);

        TipoGestioneServizio tipoGestioneServizio;

        try {
            tipoGestioneServizio = TipoGestioneServizio.valueOf(
                    servizioTo.getTipoGestioneServizio().toUpperCase()
            );
        } catch (Exception e) {
            throw new ServiceException(AppError.TIPO_GESTIONE_SERVIZIO_NON_VALIDO, e);
        }

        if (tipoGestioneServizio == TipoGestioneServizio.A_POSTI) {

            if (servizioTo.getPostiMassimiServizio() == null
                    || servizioTo.getPostiMassimiServizio() <= 0) {
                throw new ServiceException(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO);
            }
        }

        if (tipoGestioneServizio == TipoGestioneServizio.CON_COLLABORATORE) {
            servizioTo.setPostiMassimiServizio(null);
        }

        if (servizioTo.getIdServizio() == null) {

            if (servizioRepository.existsByNomeServizio(nomeServizio)) {
                throw new ServiceException(AppError.SERVIZIO_GIA_PRESENTE);
            }

            try {
                ServizioPo servizioPo = servizioMapper.toEntity(servizioTo);

                servizioPo.setTipoGestioneServizio(tipoGestioneServizio);
                servizioPo.setPostiMassimiServizio(servizioTo.getPostiMassimiServizio());
                servizioPo.setAttivoServizio(true);
                servizioPo.setDataCreazioneServizio(LocalDateTime.now());

                servizioPo = servizioRepository.save(servizioPo);

                return servizioMapper.toDto(servizioPo);

            } catch (Exception e) {
                throw new ServiceException(AppError.SERVIZIO_CREAZIONE_FALLITA, e);
            }
        }

        ServizioPo servizioPo = servizioRepository
                .findById(servizioTo.getIdServizio())
                .orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (!nomeServizio.equalsIgnoreCase(servizioPo.getNomeServizio())
                && servizioRepository.existsByNomeServizio(nomeServizio)) {
            throw new ServiceException(AppError.SERVIZIO_GIA_PRESENTE);
        }

        try {
            servizioPo.setNomeServizio(servizioTo.getNomeServizio());
            servizioPo.setCategoriaServizio(servizioTo.getCategoriaServizio());
            servizioPo.setDescrizioneServizio(servizioTo.getDescrizioneServizio());
            servizioPo.setPrezzoServizio(servizioTo.getPrezzoServizio());
            servizioPo.setDurataMinutiServizio(servizioTo.getDurataMinutiServizio());
            servizioPo.setTipoGestioneServizio(tipoGestioneServizio);
            servizioPo.setPostiMassimiServizio(servizioTo.getPostiMassimiServizio());
            servizioPo.setUrlImmagineServizio(servizioTo.getUrlImmagineServizio());
            servizioPo.setDataModificaServizio(LocalDateTime.now());

            if (servizioTo.getAttivoServizio() != null) {
                servizioPo.setAttivoServizio(servizioTo.getAttivoServizio());
            }

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

        return servizioMapper.toDto(servizioPo);
    }
}

