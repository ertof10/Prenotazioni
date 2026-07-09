package com.prenotazioni.service;

import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CollaboratoreService {

    CollaboratoreTo saveOrUpdateCollaboratore(CollaboratoreTo collaboratoreTo);

    CollaboratoreTo getCollaboratoreById(Integer idCollaboratore);

    Page<CollaboratoreTo> getAllCollaboratori(Pageable pageable);

    List<CollaboratoreTo> getCollaboratoriAttivi();

    EsitoResponse deleteCollaboratore(Integer idCollaboratore);

    CollaboratoreTo disattivaCollaboratore(Integer idCollaboratore);

    CollaboratoreTo riattivaCollaboratore(Integer idCollaboratore);
}