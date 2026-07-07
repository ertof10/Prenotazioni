package com.prenotazioni.Service;

import com.prenotazioni.Dao.*;
import com.prenotazioni.Dto.CollaboratoreTo;
import com.prenotazioni.Enums.StatoPrenotazione;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.CollaboratoreMapper;
import com.prenotazioni.Po.CollaboratorePo;
import com.prenotazioni.Po.PrenotazionePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaboratoreServiceImpl implements CollaboratoreService {

    private final CollaboratoreRepository collaboratoreRepository;
    private final CollaboratoreMapper collaboratoreMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final CalendarioRepository calendarioRepository;
    private final AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;

    public CollaboratoreServiceImpl(CollaboratoreRepository collaboratoreRepository,
                                    CollaboratoreMapper collaboratoreMapper , PrenotazioneRepository prenotazioneRepository ,CollaboratoreServizioRepository collaboratoreServizioRepository, CalendarioRepository calendarioRepository, AssenzaCollaboratoreRepository assenzaCollaboratoreRepository) {
        this.collaboratoreRepository = collaboratoreRepository;
        this.collaboratoreMapper = collaboratoreMapper;
        this.prenotazioneRepository = prenotazioneRepository;
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.calendarioRepository = calendarioRepository;
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
    }

    @Override
    @Transactional
    public CollaboratoreTo saveOrUpdateCollaboratore(CollaboratoreTo collaboratoreTo) {

        String emailCollaboratore = collaboratoreTo.getEmailCollaboratore().trim().toLowerCase();
        collaboratoreTo.setEmailCollaboratore(emailCollaboratore);

        String nomeCollaboratore = collaboratoreTo.getNomeCollaboratore().trim().toLowerCase();
        String cognomeCollaboratore = collaboratoreTo.getCognomeCollaboratore().trim().toLowerCase();
        String specializzazioneCollaboratore = collaboratoreTo.getSpecializzazioneCollaboratore().trim().toLowerCase();

        collaboratoreTo.setNomeCollaboratore(nomeCollaboratore);
        collaboratoreTo.setCognomeCollaboratore(cognomeCollaboratore);
        collaboratoreTo.setSpecializzazioneCollaboratore(specializzazioneCollaboratore);

        if (collaboratoreTo.getIdCollaboratore() == null) {

            if (collaboratoreRepository.existsByEmailCollaboratore(emailCollaboratore)) {
                throw new ServiceException(AppError.COLLABORATORE_GIA_PRESENTE);
            }

            try {
                CollaboratorePo collaboratorePo = collaboratoreMapper.toEntity(collaboratoreTo);

                collaboratorePo.setAttivoCollaboratore(true);
                collaboratorePo.setDataCreazioneCollaboratore(LocalDateTime.now());

                collaboratorePo = collaboratoreRepository.save(collaboratorePo);
                return collaboratoreMapper.toDto(collaboratorePo);

            } catch (Exception e) {
                throw new ServiceException(AppError.COLLABORATORE_CREAZIONE_FALLITA, e);
            }
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(collaboratoreTo.getIdCollaboratore())
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (!emailCollaboratore.equalsIgnoreCase(collaboratorePo.getEmailCollaboratore())
                && collaboratoreRepository.existsByEmailCollaboratore(emailCollaboratore)) {
            throw new ServiceException(AppError.COLLABORATORE_GIA_PRESENTE);
        }

        try {
            collaboratorePo.setNomeCollaboratore(collaboratoreTo.getNomeCollaboratore());
            collaboratorePo.setCognomeCollaboratore(collaboratoreTo.getCognomeCollaboratore());
            collaboratorePo.setEmailCollaboratore(collaboratoreTo.getEmailCollaboratore());
            collaboratorePo.setTelefonoCollaboratore(collaboratoreTo.getTelefonoCollaboratore());
            collaboratorePo.setSpecializzazioneCollaboratore(collaboratoreTo.getSpecializzazioneCollaboratore());
            collaboratorePo.setDescrizioneCollaboratore(collaboratoreTo.getDescrizioneCollaboratore());
            collaboratorePo.setDataModificaCollaboratore(LocalDateTime.now());

            if (collaboratoreTo.getAttivoCollaboratore() != null) {
                collaboratorePo.setAttivoCollaboratore(collaboratoreTo.getAttivoCollaboratore());
            }

            collaboratorePo = collaboratoreRepository.save(collaboratorePo);
            return collaboratoreMapper.toDto(collaboratorePo);

        } catch (Exception e) {
            throw new ServiceException(AppError.COLLABORATORE_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CollaboratoreTo getCollaboratoreById(Integer idCollaboratore) {

        if (idCollaboratore == null || idCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository.findById(idCollaboratore).orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        return collaboratoreMapper.toDto(collaboratorePo);
    }

    @Override
    public List<CollaboratoreTo> getAllCollaboratori() {
        return collaboratoreRepository.findAll()
                .stream()
                .map(collaboratoreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollaboratoreTo> getCollaboratoriAttivi() {
        return collaboratoreRepository.findAll()
                .stream()
                .filter(collaboratorePo -> Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore()))
                .map(collaboratoreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteCollaboratore(Integer idCollaboratore) {

        if (idCollaboratore == null || idCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreRepository.existsById(idCollaboratore)) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (prenotazioneRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || collaboratoreServizioRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || calendarioRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)
                || assenzaCollaboratoreRepository.existsByCollaboratorePo_IdCollaboratore(idCollaboratore)) {
            throw new ServiceException(AppError.COLLABORATORE_NON_ELIMINABILE);
        }

        try {
            collaboratoreRepository.deleteById(idCollaboratore);
            return new EsitoResponse("Collaboratore eliminato correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.COLLABORATORE_ELIMINAZIONE_FALLITA, e);
        }
    }

    @Override
    @Transactional
    public CollaboratoreTo disattivaCollaboratore(Integer idCollaboratore) {

        if (idCollaboratore == null || idCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        collaboratorePo.setAttivoCollaboratore(false);

        List<PrenotazionePo> prenotazioniFuture =
                prenotazioneRepository.findPrenotazioniFutureConfermateByCollaboratore(
                        idCollaboratore,
                        LocalDate.now()
                );

        for (PrenotazionePo prenotazionePo : prenotazioniFuture) {
            prenotazionePo.setStatoPrenotazione(StatoPrenotazione.DA_RIPROGRAMMARE);
            prenotazionePo.setDataModificaPrenotazione(LocalDateTime.now());
        }

        prenotazioneRepository.saveAll(prenotazioniFuture);

        collaboratorePo = collaboratoreRepository.save(collaboratorePo);

        return collaboratoreMapper.toDto(collaboratorePo);
    }

    @Override
    @Transactional
    public CollaboratoreTo riattivaCollaboratore(Integer idCollaboratore) {

        if (idCollaboratore == null || idCollaboratore <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(idCollaboratore)
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        collaboratorePo.setAttivoCollaboratore(true);

        collaboratorePo = collaboratoreRepository.save(collaboratorePo);

        return collaboratoreMapper.toDto(collaboratorePo);
    }
}