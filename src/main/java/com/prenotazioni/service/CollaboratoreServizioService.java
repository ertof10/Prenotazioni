package com.prenotazioni.service;

import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CollaboratoreServizioService {

    CollaboratoreServizioTo saveOrUpdateCollaboratoreServizio(CollaboratoreServizioTo collaboratoreServizioTo);

    CollaboratoreServizioTo getCollaboratoreServizioById(Integer idCollaboratoreServizio);

    Page<CollaboratoreServizioTo> getAllCollaboratoriServizi(Pageable pageable);

    List<CollaboratoreServizioTo> getServiziByCollaboratore(Integer idCollaboratore);

    List<CollaboratoreServizioTo> getCollaboratoriByServizio(Integer idServizio);

    EsitoResponse deleteCollaboratoreServizio(Integer idCollaboratoreServizio);
}