package com.prenotazioni.Service;

import com.prenotazioni.Dao.AssenzaCollaboratoreRepository;
import com.prenotazioni.Dao.CollaboratoreRepository;
import com.prenotazioni.Dto.AssenzaCollaboratoreTo;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.AssenzaCollaboratoreMapper;
import com.prenotazioni.Po.AssenzaCollaboratorePo;
import com.prenotazioni.Po.CollaboratorePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssenzaCollaboratoreServiceImpl implements AssenzaCollaboratoreService {

    private final AssenzaCollaboratoreRepository assenzaCollaboratoreRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final AssenzaCollaboratoreMapper assenzaCollaboratoreMapper;

    public AssenzaCollaboratoreServiceImpl(AssenzaCollaboratoreRepository assenzaCollaboratoreRepository,
                                           CollaboratoreRepository collaboratoreRepository,
                                           AssenzaCollaboratoreMapper assenzaCollaboratoreMapper) {
        this.assenzaCollaboratoreRepository = assenzaCollaboratoreRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.assenzaCollaboratoreMapper = assenzaCollaboratoreMapper;
    }

    @Override
    @Transactional
    public AssenzaCollaboratoreTo saveOrUpdateAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo) {

        validaAssenzaCollaboratore(assenzaCollaboratoreTo);

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(assenzaCollaboratoreTo.getIdCollaboratore())
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

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

        if (assenzaCollaboratoreTo.getIdAssenzaCollaboratore() == null) {

            try {
                AssenzaCollaboratorePo assenzaCollaboratorePo = new AssenzaCollaboratorePo();

                assenzaCollaboratorePo.setCollaboratorePo(collaboratorePo);
                assenzaCollaboratorePo.setDataInizioAssenza(assenzaCollaboratoreTo.getDataInizioAssenza());
                assenzaCollaboratorePo.setDataFineAssenza(assenzaCollaboratoreTo.getDataFineAssenza());
                assenzaCollaboratorePo.setOraInizioAssenza(assenzaCollaboratoreTo.getOraInizioAssenza());
                assenzaCollaboratorePo.setOraFineAssenza(assenzaCollaboratoreTo.getOraFineAssenza());
                assenzaCollaboratorePo.setMotivoAssenzaCollaboratore(assenzaCollaboratoreTo.getMotivoAssenzaCollaboratore());
                assenzaCollaboratorePo.setAttivoAssenzaCollaboratore(true);
                assenzaCollaboratorePo.setDataCreazioneAssenzaCollaboratore(LocalDateTime.now());

                assenzaCollaboratorePo = assenzaCollaboratoreRepository.save(assenzaCollaboratorePo);

                return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException(AppError.ASSENZA_COLLABORATORE_CREAZIONE_FALLITA, e);
            }
        }

        AssenzaCollaboratorePo assenzaCollaboratorePo = assenzaCollaboratoreRepository
                .findById(assenzaCollaboratoreTo.getIdAssenzaCollaboratore())
                .orElse(null);

        if (assenzaCollaboratorePo == null) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_NON_TROVATA);
        }

        try {
            assenzaCollaboratorePo.setCollaboratorePo(collaboratorePo);
            assenzaCollaboratorePo.setDataInizioAssenza(assenzaCollaboratoreTo.getDataInizioAssenza());
            assenzaCollaboratorePo.setDataFineAssenza(assenzaCollaboratoreTo.getDataFineAssenza());
            assenzaCollaboratorePo.setOraInizioAssenza(assenzaCollaboratoreTo.getOraInizioAssenza());
            assenzaCollaboratorePo.setOraFineAssenza(assenzaCollaboratoreTo.getOraFineAssenza());
            assenzaCollaboratorePo.setMotivoAssenzaCollaboratore(assenzaCollaboratoreTo.getMotivoAssenzaCollaboratore());

            if (assenzaCollaboratoreTo.getAttivoAssenzaCollaboratore() != null) {
                assenzaCollaboratorePo.setAttivoAssenzaCollaboratore(assenzaCollaboratoreTo.getAttivoAssenzaCollaboratore());
            }

            assenzaCollaboratorePo.setDataModificaAssenzaCollaboratore(LocalDateTime.now());

            assenzaCollaboratorePo = assenzaCollaboratoreRepository.save(assenzaCollaboratorePo);

            return assenzaCollaboratoreMapper.toDto(assenzaCollaboratorePo);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(AppError.ASSENZA_COLLABORATORE_MODIFICA_FALLITA, e);
        }
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