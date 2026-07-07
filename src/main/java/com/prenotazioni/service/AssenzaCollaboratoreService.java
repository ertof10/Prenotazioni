package com.prenotazioni.service;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface AssenzaCollaboratoreService {

    AssenzaCollaboratoreTo saveOrUpdateAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo);

    AssenzaCollaboratoreTo getAssenzaCollaboratoreById(Integer idAssenzaCollaboratore);

    List<AssenzaCollaboratoreTo> getAllAssenzeCollaboratori();

    List<AssenzaCollaboratoreTo> getAssenzeByCollaboratore(Integer idCollaboratore);

    EsitoResponse deleteAssenzaCollaboratore(Integer idAssenzaCollaboratore);

}