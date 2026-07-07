package com.prenotazioni.service;

import com.prenotazioni.dto.CollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface CollaboratoreService {

    CollaboratoreTo saveOrUpdateCollaboratore(CollaboratoreTo collaboratoreTo);

    CollaboratoreTo getCollaboratoreById(Integer idCollaboratore);

    List<CollaboratoreTo> getAllCollaboratori();

    List<CollaboratoreTo> getCollaboratoriAttivi();

    EsitoResponse deleteCollaboratore(Integer idCollaboratore);

    CollaboratoreTo disattivaCollaboratore(Integer idCollaboratore);

    CollaboratoreTo riattivaCollaboratore(Integer idCollaboratore);
}