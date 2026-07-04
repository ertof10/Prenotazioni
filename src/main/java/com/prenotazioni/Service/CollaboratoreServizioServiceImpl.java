package com.prenotazioni.Service;

import com.prenotazioni.Dto.CollaboratoreServizioTo;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.CollaboratoreServizioMapper;
import com.prenotazioni.Po.CollaboratorePo;
import com.prenotazioni.Po.CollaboratoreServizioPo;
import com.prenotazioni.Po.ServizioPo;
import com.prenotazioni.Dao.CollaboratoreRepository;
import com.prenotazioni.Dao.CollaboratoreServizioRepository;
import com.prenotazioni.Dao.ServizioRepository;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaboratoreServizioServiceImpl implements CollaboratoreServizioService {

    private final CollaboratoreServizioRepository collaboratoreServizioRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final ServizioRepository servizioRepository;
    private final CollaboratoreServizioMapper collaboratoreServizioMapper;

    public CollaboratoreServizioServiceImpl(CollaboratoreServizioRepository collaboratoreServizioRepository,
                                            CollaboratoreRepository collaboratoreRepository,
                                            ServizioRepository servizioRepository,
                                            CollaboratoreServizioMapper collaboratoreServizioMapper) {
        this.collaboratoreServizioRepository = collaboratoreServizioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.servizioRepository = servizioRepository;
        this.collaboratoreServizioMapper = collaboratoreServizioMapper;
    }

    @Override
    @Transactional
    public CollaboratoreServizioTo saveOrUpdateCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo) {

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(collaboratoreServizioTo.getIdCollaboratore())
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        ServizioPo servizioPo = servizioRepository
                .findById(collaboratoreServizioTo.getIdServizio())
                .orElse(null);

        if (servizioPo == null) {
            throw new ServiceException(AppError.SERVIZIO_NON_TROVATO);
        }

        if (collaboratoreServizioTo.getIdCollaboratoreServizio() == null) {

            if (collaboratoreServizioRepository.existsAssociazioneAttiva(
                    collaboratoreServizioTo.getIdCollaboratore(),
                    collaboratoreServizioTo.getIdServizio())) {
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
                collaboratoreServizioPo.setDataCreazioneCollaboratoreServizio(LocalDateTime.now());

                collaboratoreServizioPo = collaboratoreServizioRepository.save(collaboratoreServizioPo);
                return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);

            } catch (Exception e) {
                throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_CREAZIONE_FALLITA, e);
            }
        }

        CollaboratoreServizioPo collaboratoreServizioPo = collaboratoreServizioRepository
                .findById(collaboratoreServizioTo.getIdCollaboratoreServizio())
                .orElse(null);

        if (collaboratoreServizioPo == null) {
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        try {
            collaboratoreServizioPo.setCollaboratorePo(collaboratorePo);
            collaboratoreServizioPo.setServizioPo(servizioPo);
            collaboratoreServizioPo.setDurataPersonalizzataMinuti(collaboratoreServizioTo.getDurataPersonalizzataMinuti());
            collaboratoreServizioPo.setPrezzoPersonalizzato(collaboratoreServizioTo.getPrezzoPersonalizzato());
            collaboratoreServizioPo.setNoteCollaboratoreServizio(collaboratoreServizioTo.getNoteCollaboratoreServizio());
            collaboratoreServizioPo.setDataModificaCollaboratoreServizio(LocalDateTime.now());

            if (collaboratoreServizioTo.getAttivoCollaboratoreServizio() != null) {
                collaboratoreServizioPo.setAttivoCollaboratoreServizio(collaboratoreServizioTo.getAttivoCollaboratoreServizio());
            }

            collaboratoreServizioPo = collaboratoreServizioRepository.save(collaboratoreServizioPo);
            return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);

        } catch (Exception e) {
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CollaboratoreServizioTo getCollaboratoreServizioById(Integer idCollaboratoreServizio) {

        if (idCollaboratoreServizio == null || idCollaboratoreServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CollaboratoreServizioPo collaboratoreServizioPo = collaboratoreServizioRepository
                .findById(idCollaboratoreServizio)
                .orElse(null);

        if (collaboratoreServizioPo == null) {
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        return collaboratoreServizioMapper.toDto(collaboratoreServizioPo);
    }

    @Override
    public List<CollaboratoreServizioTo> getAllCollaboratoriServizi() {
        return collaboratoreServizioRepository.findAll()
                .stream()
                .map(collaboratoreServizioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollaboratoreServizioTo> getServiziByCollaboratore(Integer idCollaboratore) {
        return collaboratoreServizioRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                .stream()
                .map(collaboratoreServizioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollaboratoreServizioTo> getCollaboratoriByServizio(Integer idServizio) {
        return collaboratoreServizioRepository.findByServizioPo_IdServizio(idServizio)
                .stream()
                .map(collaboratoreServizioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteCollaboratoreServizio(Integer idCollaboratoreServizio) {

        if (idCollaboratoreServizio == null || idCollaboratoreServizio <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!collaboratoreServizioRepository.existsById(idCollaboratoreServizio)) {
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO);
        }

        try {
            collaboratoreServizioRepository.deleteById(idCollaboratoreServizio);
            return new EsitoResponse("Associazione collaboratore servizio eliminata correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.COLLABORATORE_SERVIZIO_ELIMINAZIONE_FALLITA, e);
        }
    }
}