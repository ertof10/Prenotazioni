package com.prenotazioni.service;

import com.prenotazioni.dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.AssenzaCollaboratoreMapper;
import com.prenotazioni.po.AssenzaCollaboratorePo;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.PrenotazionePo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssenzaCollaboratoreServiceImpl implements AssenzaCollaboratoreService {
    private final Clock clock;
    private final AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final AssenzaCollaboratoreMapper assenzaCollaboratoreMapper;
    private final PrenotazioneRepository prenotazioneRepository;

    public AssenzaCollaboratoreServiceImpl(AssenzaCollaboratoreRepository assenzaCollaboratoreRepository,
                                           CollaboratoreRepository collaboratoreRepository,
                                           AssenzaCollaboratoreMapper assenzaCollaboratoreMapper,
                                           PrenotazioneRepository prenotazioneRepository,
                                           Clock clock) {
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.assenzaCollaboratoreMapper = assenzaCollaboratoreMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AssenzaCollaboratoreTo saveOrUpdateAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        validaAssenzaCollaboratore(assenzaCollaboratoreTo);

        CollaboratorePo collaboratorePo =
                recuperaCollaboratore(assenzaCollaboratoreTo.getIdCollaboratore());

        verificaAssenzaSovrapposta(assenzaCollaboratoreTo);

        boolean nuovaAssenza = assenzaCollaboratoreTo.getIdAssenzaCollaboratore() == null;

        AssenzaCollaboratorePo assenzaCollaboratorePo;

        if (nuovaAssenza) {
            assenzaCollaboratorePo = creaAssenzaCollaboratore(
                    assenzaCollaboratoreTo,
                    collaboratorePo
            );
        } else {
            assenzaCollaboratorePo = modificaAssenzaCollaboratore(
                    assenzaCollaboratoreTo,
                    collaboratorePo
            );
        }

        assenzaCollaboratorePo = salvaAssenzaEAggiornaPrenotazioni(
                assenzaCollaboratorePo,
                nuovaAssenza
        );

        return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);
    }

    private CollaboratorePo recuperaCollaboratore(Integer idCollaboratore) {

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        return collaboratorePo;
    }
    private void verificaAssenzaSovrapposta(AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        boolean assenzaSovrapposta =
                assenzaCollaboratoreRepository.existsAssenzaSovrapposta(
                        assenzaCollaboratoreTo.getIdAssenzaCollaboratore(),
                        assenzaCollaboratoreTo.getIdCollaboratore(),
                        assenzaCollaboratoreTo.getDataInizioAssenza(),
                        assenzaCollaboratoreTo.getDataFineAssenza(),
                        assenzaCollaboratoreTo.getOraInizioAssenza(),
                        assenzaCollaboratoreTo.getOraFineAssenza()
                );

        if (assenzaSovrapposta) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_GIA_PRESENTE);
        }
    }
    private AssenzaCollaboratorePo creaAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo,
                                                            CollaboratorePo collaboratorePo) {

        AssenzaCollaboratorePo assenzaCollaboratorePo = new AssenzaCollaboratorePo();

        assenzaCollaboratorePo.setCollaboratorePo(collaboratorePo);
        assenzaCollaboratorePo.setDataInizioAssenza(assenzaCollaboratoreTo.getDataInizioAssenza());
        assenzaCollaboratorePo.setDataFineAssenza(assenzaCollaboratoreTo.getDataFineAssenza());
        assenzaCollaboratorePo.setOraInizioAssenza(assenzaCollaboratoreTo.getOraInizioAssenza());
        assenzaCollaboratorePo.setOraFineAssenza(assenzaCollaboratoreTo.getOraFineAssenza());
        assenzaCollaboratorePo.setMotivoAssenzaCollaboratore(assenzaCollaboratoreTo.getMotivoAssenzaCollaboratore());
        assenzaCollaboratorePo.setAttivoAssenzaCollaboratore(true);
        assenzaCollaboratorePo.setDataCreazioneAssenzaCollaboratore(LocalDateTime.now(clock));

        return assenzaCollaboratorePo;
    }
    private AssenzaCollaboratorePo modificaAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo,
                                                                CollaboratorePo collaboratorePo) {

        AssenzaCollaboratorePo assenzaCollaboratorePo = assenzaCollaboratoreRepository
                .findById(assenzaCollaboratoreTo.getIdAssenzaCollaboratore())
                .orElse(null);

        if (assenzaCollaboratorePo == null) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        assenzaCollaboratorePo.setCollaboratorePo(collaboratorePo);
        assenzaCollaboratorePo.setDataInizioAssenza(assenzaCollaboratoreTo.getDataInizioAssenza());
        assenzaCollaboratorePo.setDataFineAssenza(assenzaCollaboratoreTo.getDataFineAssenza());
        assenzaCollaboratorePo.setOraInizioAssenza(assenzaCollaboratoreTo.getOraInizioAssenza());
        assenzaCollaboratorePo.setOraFineAssenza(assenzaCollaboratoreTo.getOraFineAssenza());
        assenzaCollaboratorePo.setMotivoAssenzaCollaboratore(assenzaCollaboratoreTo.getMotivoAssenzaCollaboratore());

        if (assenzaCollaboratoreTo.getAttivoAssenzaCollaboratore() != null) {
            assenzaCollaboratorePo.setAttivoAssenzaCollaboratore(
                    assenzaCollaboratoreTo.getAttivoAssenzaCollaboratore()
            );
        }

        assenzaCollaboratorePo.setDataModificaAssenzaCollaboratore(LocalDateTime.now(clock));

        return assenzaCollaboratorePo;
    }
    private AssenzaCollaboratorePo salvaAssenzaEAggiornaPrenotazioni(AssenzaCollaboratorePo assenzaCollaboratorePo,
                                                                     boolean nuovaAssenza) {

        try {
            assenzaCollaboratorePo = assenzaCollaboratoreRepository.save(assenzaCollaboratorePo);

            aggiornaPrenotazioniColpiteDaAssenza(assenzaCollaboratorePo);

            return assenzaCollaboratorePo;

        } catch (ServiceException e) {
            throw e;

        } catch (Exception e) {

            if (nuovaAssenza) {
                throw new ServiceException(AppError.ASSENZA_COLLABORATORE_CREAZIONE_FALLITA, e);
            }

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }
    private void aggiornaPrenotazioniColpiteDaAssenza(AssenzaCollaboratorePo assenzaCollaboratorePo) {

        if (!Boolean.TRUE.equals(assenzaCollaboratorePo.getAttivoAssenzaCollaboratore())) {
            return;
        }

        List<PrenotazionePo> prenotazioniColpite =
                prenotazioneRepository.findPrenotazioniConfermateCheHannoAssenza(
                        assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore(),
                        assenzaCollaboratorePo.getDataInizioAssenza(),
                        assenzaCollaboratorePo.getDataFineAssenza(),
                        assenzaCollaboratorePo.getOraInizioAssenza(),
                        assenzaCollaboratorePo.getOraFineAssenza()
                );

        for (PrenotazionePo prenotazionePo : prenotazioniColpite) {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now(clock));
        }

        prenotazioneRepository.saveAll(prenotazioniColpite);
    }

    @Override
    public AssenzaCollaboratoreTo getAssenzaCollaboratoreById(Integer idAssenzaCollaboratore) {

        if (idAssenzaCollaboratore == null || idAssenzaCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        AssenzaCollaboratorePo assenzaCollaboratorePo = assenzaCollaboratoreRepository
                .findById(idAssenzaCollaboratore)
                .orElse(null);

        if (assenzaCollaboratorePo == null) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);
    }

    @Override
    public List<AssenzaCollaboratoreTo> getAllAssenzeCollaboratori() {
        return assenzaCollaboratoreRepository.findAll()
                .stream()
                .map(assenzaCollaboratoreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssenzaCollaboratoreTo> getAssenzeByCollaboratore(Integer idCollaboratore) {

        if (idCollaboratore == null || idCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreRepository.existsById(idCollaboratore)) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        return assenzaCollaboratoreRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                .stream()
                .map(assenzaCollaboratoreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteAssenzaCollaboratore(Integer idAssenzaCollaboratore) {

        if (idAssenzaCollaboratore == null || idAssenzaCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!assenzaCollaboratoreRepository.existsById(idAssenzaCollaboratore)) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        try {
            assenzaCollaboratoreRepository.deleteById(idAssenzaCollaboratore);
            return new EsitoResponse("Assenza collaboratore eliminata correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_ELIMINAZIONE_FALLITA, e);
        }
    }

    private void validaAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        if (assenzaCollaboratoreTo == null) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getIdCollaboratore() == null
                || assenzaCollaboratoreTo.getIdCollaboratore() <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (assenzaCollaboratoreTo.getDataInizioAssenza() == null
                || assenzaCollaboratoreTo.getDataFineAssenza() == null
                || assenzaCollaboratoreTo.getOraInizioAssenza() == null
                || assenzaCollaboratoreTo.getOraFineAssenza() == null) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getDataFineAssenza()
                .isBefore(assenzaCollaboratoreTo.getDataInizioAssenza())) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getOraFineAssenza()
                .isBefore(assenzaCollaboratoreTo.getOraInizioAssenza())
                || assenzaCollaboratoreTo.getOraFineAssenza()
                .equals(assenzaCollaboratoreTo.getOraInizioAssenza())) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }
    }
}