package com.prenotazioni.service;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssenzaCollaboratoreService {

    AssenzaCollaboratoreTo saveOrUpdateAssenzaCollaboratore(AssenzaCollaboratoreTo assenzaCollaboratoreTo);

    AssenzaCollaboratoreTo getAssenzaCollaboratoreById(Integer idAssenzaCollaboratore);

    Page<AssenzaCollaboratoreTo> getAllAssenzeCollaboratori(org.springframework.data.domain.Pageable pageable);

    List<AssenzaCollaboratoreTo> getAssenzeByCollaboratore(Integer idCollaboratore);

    EsitoResponse deleteAssenzaCollaboratore(Integer idAssenzaCollaboratore);

}