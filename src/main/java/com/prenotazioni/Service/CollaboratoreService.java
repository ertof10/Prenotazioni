package com.prenotazioni.Service;

import com.prenotazioni.Dto.CollaboratoreTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface CollaboratoreService {

    CollaboratoreTo saveOrUpdateCollaboratore(CollaboratoreTo collaboratoreTo);

    CollaboratoreTo getCollaboratoreById(Integer idCollaboratore);

    List<CollaboratoreTo> getAllCollaboratori();

    List<CollaboratoreTo> getCollaboratoriAttivi();

    EsitoResponse deleteCollaboratore(Integer idCollaboratore);
}