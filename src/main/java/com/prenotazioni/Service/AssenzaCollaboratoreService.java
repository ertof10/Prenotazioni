package com.prenotazioni.Service;

import com.prenotazioni.Dto.AssenzaCollaboratoreTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface AssenzaCollaboratoreService {

    AssenzaCollaboratoreTo saveOrUpdateAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo);

    AssenzaCollaboratoreTo getAssenzaCollaboratoreById(Integer idAssenzaCollaboratore);

    List<AssenzaCollaboratoreTo> getAllAssenzeCollaboratori();

    List<AssenzaCollaboratoreTo> getAssenzeByCollaboratore(Integer idCollaboratore);

    EsitoResponse deleteAssenzaCollaboratore(Integer idAssenzaCollaboratore);

}