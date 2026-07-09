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
public class AssenzaCollaboratoreServiceImpl implements AssenzaCollaboratoreService {

    private static final Logger log = LoggerFactory.getLogger(AssenzaCollaboratoreServiceImpl.class);

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

        log.info("Avvio salvataggio assenza collaboratore | idAssenzaCollaboratore={} | idCollaboratore={} | dataInizio={} | dataFine={}",
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getIdAssenzaCollaboratore() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getIdCollaboratore() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataInizioAssenza() : null,
                assenzaCollaboratoreTo != null ? assenzaCollaboratoreTo.getDataFineAssenza() : null);

        validaAssenzaCollaboratore(assenzaCollaboratoreTo);

        CollaboratorePo collaboratorePo = recuperaCollaboratore(assenzaCollaboratoreTo.getIdCollaboratore());

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

        log.info("{} assenza collaboratore completata | idAssenzaCollaboratore={} | idCollaboratore={} | attiva={}",
                nuovaAssenza ? "Creazione" : "Modifica",
                assenzaCollaboratorePo.getIdAssenzaCollaboratore(),
                assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore(),
                assenzaCollaboratorePo.getAttivoAssenzaCollaboratore());

        return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);
    }

    private CollaboratorePo recuperaCollaboratore(Integer idCollaboratore) {

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            log.warn("Operazione assenza bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

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
            log.warn("Salvataggio assenza bloccato: assenza sovrapposta già presente | idCollaboratore={} | dataInizio={} | dataFine={} | oraInizio={} | oraFine={}",
                    assenzaCollaboratoreTo.getIdCollaboratore(),
                    assenzaCollaboratoreTo.getDataInizioAssenza(),
                    assenzaCollaboratoreTo.getDataFineAssenza(),
                    assenzaCollaboratoreTo.getOraInizioAssenza(),
                    assenzaCollaboratoreTo.getOraFineAssenza());

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_GIA_PRESENTE);
        }
    }

    private AssenzaCollaboratorePo creaAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo,
                                                            CollaboratorePo collaboratorePo) {

        log.info("Preparazione nuova assenza collaboratore | idCollaboratore={} | dataInizio={} | dataFine={}",
                collaboratorePo.getIdCollaboratore(),
                assenzaCollaboratoreTo.getDataInizioAssenza(),
                assenzaCollaboratoreTo.getDataFineAssenza());

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

        log.info("Preparazione modifica assenza collaboratore | idAssenzaCollaboratore={} | idCollaboratore={}",
                assenzaCollaboratoreTo.getIdAssenzaCollaboratore(),
                collaboratorePo.getIdCollaboratore());

        AssenzaCollaboratorePo assenzaCollaboratorePo = assenzaCollaboratoreRepository
                .findById(assenzaCollaboratoreTo.getIdAssenzaCollaboratore())
                .orElse(null);

        if (assenzaCollaboratorePo == null) {
            log.warn("Modifica assenza bloccata: assenza non trovata | idAssenzaCollaboratore={}",
                    assenzaCollaboratoreTo.getIdAssenzaCollaboratore());

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

            log.info("Assenza collaboratore salvata | idAssenzaCollaboratore={} | idCollaboratore={} | nuovaAssenza={}",
                    assenzaCollaboratorePo.getIdAssenzaCollaboratore(),
                    assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore(),
                    nuovaAssenza);

            aggiornaPrenotazioniColpiteDaAssenza(assenzaCollaboratorePo);

            return assenzaCollaboratorePo;

        } catch (ServiceException e) {
            throw e;

        } catch (Exception e) {
            log.error("{} assenza collaboratore fallita per errore tecnico | idAssenzaCollaboratore={} | idCollaboratore={}",
                    nuovaAssenza ? "Creazione" : "Modifica",
                    assenzaCollaboratorePo.getIdAssenzaCollaboratore(),
                    assenzaCollaboratorePo.getCollaboratorePo() != null
                            ? assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore()
                            : null,
                    e);

            if (nuovaAssenza) {
                throw new ServiceException(AppError.ASSENZA_COLLABORATORE_CREAZIONE_FALLITA, e);
            }

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }

    private void aggiornaPrenotazioniColpiteDaAssenza(AssenzaCollaboratorePo assenzaCollaboratorePo) {

        if (!Boolean.TRUE.equals(assenzaCollaboratorePo.getAttivoAssenzaCollaboratore())) {
            log.info("Aggiornamento prenotazioni saltato: assenza non attiva | idAssenzaCollaboratore={}",
                    assenzaCollaboratorePo.getIdAssenzaCollaboratore());

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

        log.info("Prenotazioni aggiornate per assenza collaboratore | idAssenzaCollaboratore={} | idCollaboratore={} | prenotazioniDaRiprogrammare={}",
                assenzaCollaboratorePo.getIdAssenzaCollaboratore(),
                assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore(),
                prenotazioniColpite.size());
    }

    @Override
    public AssenzaCollaboratoreTo getAssenzaCollaboratoreById(Integer idAssenzaCollaboratore) {

        log.info("Ricerca assenza collaboratore per id | idAssenzaCollaboratore={}",
                idAssenzaCollaboratore);

        if (idAssenzaCollaboratore == null || idAssenzaCollaboratore <= 0) {
            log.warn("Ricerca assenza collaboratore bloccata: id non valido | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        AssenzaCollaboratorePo assenzaCollaboratorePo = assenzaCollaboratoreRepository
                .findById(idAssenzaCollaboratore)
                .orElse(null);

        if (assenzaCollaboratorePo == null) {
            log.warn("Ricerca assenza collaboratore fallita: assenza non trovata | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore);

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        log.info("Ricerca assenza collaboratore completata | idAssenzaCollaboratore={} | idCollaboratore={}",
                assenzaCollaboratorePo.getIdAssenzaCollaboratore(),
                assenzaCollaboratorePo.getCollaboratorePo() != null
                        ? assenzaCollaboratorePo.getCollaboratorePo().getIdCollaboratore()
                        : null);

        return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);
    }

    @Override
    public Page<AssenzaCollaboratoreTo> getAllAssenzeCollaboratori(Pageable pageable) {

        log.info("Ricerca assenze collaboratori paginata | page={} | size={} | sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<AssenzaCollaboratoreTo> risultato = assenzaCollaboratoreRepository.findAll(pageable)
                .map(assenzaCollaboratoreMapper::toDto);

        log.info("Ricerca assenze collaboratori completata | elementiPagina={} | totaleElementi={} | totalePagine={}",
                risultato.getNumberOfElements(),
                risultato.getTotalElements(),
                risultato.getTotalPages());

        return risultato;
    }

    @Override
    public List<AssenzaCollaboratoreTo> getAssenzeByCollaboratore(Integer idCollaboratore) {

        log.info("Ricerca assenze per collaboratore | idCollaboratore={}", idCollaboratore);

        if (idCollaboratore == null || idCollaboratore <= 0) {
            log.warn("Ricerca assenze per collaboratore bloccata: id non valido | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreRepository.existsById(idCollaboratore)) {
            log.warn("Ricerca assenze per collaboratore bloccata: collaboratore non trovato | idCollaboratore={}",
                    idCollaboratore);

            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        List<AssenzaCollaboratoreTo> risultato =
                assenzaCollaboratoreRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                        .stream()
                        .map(assenzaCollaboratoreMapper::toDto)
                        .collect(Collectors.toList());

        log.info("Ricerca assenze per collaboratore completata | idCollaboratore={} | elementi={}",
                idCollaboratore,
                risultato.size());

        return risultato;
    }

    @Override
    @Transactional
    public EsitoResponse deleteAssenzaCollaboratore(Integer idAssenzaCollaboratore) {

        log.info("Avvio eliminazione assenza collaboratore | idAssenzaCollaboratore={}",
                idAssenzaCollaboratore);

        if (idAssenzaCollaboratore == null || idAssenzaCollaboratore <= 0) {
            log.warn("Eliminazione assenza collaboratore bloccata: id non valido | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore);

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!assenzaCollaboratoreRepository.existsById(idAssenzaCollaboratore)) {
            log.warn("Eliminazione assenza collaboratore bloccata: assenza non trovata | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore);

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        try {
            assenzaCollaboratoreRepository.deleteById(idAssenzaCollaboratore);

            log.info("Eliminazione assenza collaboratore completata | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore);

            return new EsitoResponse("Assenza collaboratore eliminata correttamente");

        } catch (Exception e) {
            log.error("Eliminazione assenza collaboratore fallita per errore tecnico | idAssenzaCollaboratore={}",
                    idAssenzaCollaboratore,
                    e);

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_ELIMINAZIONE_FALLITA, e);
        }
    }

    private void validaAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        if (assenzaCollaboratoreTo == null) {
            log.warn("Salvataggio assenza collaboratore bloccato: request nulla");
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getIdCollaboratore() == null
                || assenzaCollaboratoreTo.getIdCollaboratore() <= 0) {

            log.warn("Salvataggio assenza collaboratore bloccato: id collaboratore non valido | idCollaboratore={}",
                    assenzaCollaboratoreTo.getIdCollaboratore());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (assenzaCollaboratoreTo.getIdAssenzaCollaboratore() != null
                && assenzaCollaboratoreTo.getIdAssenzaCollaboratore() <= 0) {

            log.warn("Salvataggio assenza collaboratore bloccato: id assenza non valido | idAssenzaCollaboratore={}",
                    assenzaCollaboratoreTo.getIdAssenzaCollaboratore());

            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (assenzaCollaboratoreTo.getDataInizioAssenza() == null
                || assenzaCollaboratoreTo.getDataFineAssenza() == null
                || assenzaCollaboratoreTo.getOraInizioAssenza() == null
                || assenzaCollaboratoreTo.getOraFineAssenza() == null) {

            log.warn("Salvataggio assenza collaboratore bloccato: date o orari mancanti | idCollaboratore={}",
                    assenzaCollaboratoreTo.getIdCollaboratore());

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getDataFineAssenza()
                .isBefore(assenzaCollaboratoreTo.getDataInizioAssenza())) {

            log.warn("Salvataggio assenza collaboratore bloccato: data fine precedente a data inizio | idCollaboratore={} | dataInizio={} | dataFine={}",
                    assenzaCollaboratoreTo.getIdCollaboratore(),
                    assenzaCollaboratoreTo.getDataInizioAssenza(),
                    assenzaCollaboratoreTo.getDataFineAssenza());

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }

        if (assenzaCollaboratoreTo.getOraFineAssenza()
                .isBefore(assenzaCollaboratoreTo.getOraInizioAssenza())
                || assenzaCollaboratoreTo.getOraFineAssenza()
                .equals(assenzaCollaboratoreTo.getOraInizioAssenza())) {

            log.warn("Salvataggio assenza collaboratore bloccato: orario non valido | idCollaboratore={} | oraInizio={} | oraFine={}",
                    assenzaCollaboratoreTo.getIdCollaboratore(),
                    assenzaCollaboratoreTo.getOraInizioAssenza(),
                    assenzaCollaboratoreTo.getOraFineAssenza());

            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_VALIDA);
        }
    }
}