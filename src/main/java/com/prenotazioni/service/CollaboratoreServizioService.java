package com.prenotazioni.service;

import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface CollaboratoreServizioService {

    CollaboratoreServizioTo saveOrUpdateCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo);

    CollaboratoreServizioTo getCollaboratoreServizioById(Integer idCollaboratoreServizio);

    List<CollaboratoreServizioTo> getAllCollaboratoriServizi();

    List<CollaboratoreServizioTo> getServiziByCollaboratore(Integer idCollaboratore);

    List<CollaboratoreServizioTo> getCollaboratoriByServizio(Integer idServizio);

    EsitoResponse deleteCollaboratoreServizio(Integer idCollaboratoreServizio);
}