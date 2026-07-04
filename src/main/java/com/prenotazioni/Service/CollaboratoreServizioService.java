package com.prenotazioni.Service;

import com.prenotazioni.Dto.CollaboratoreServizioTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface CollaboratoreServizioService {

    CollaboratoreServizioTo saveOrUpdateCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo);

    CollaboratoreServizioTo getCollaboratoreServizioById(Integer idCollaboratoreServizio);

    List<CollaboratoreServizioTo> getAllCollaboratoriServizi();

    List<CollaboratoreServizioTo> getServiziByCollaboratore(Integer idCollaboratore);

    List<CollaboratoreServizioTo> getCollaboratoriByServizio(Integer idServizio);

    EsitoResponse deleteCollaboratoreServizio(Integer idCollaboratoreServizio);
}