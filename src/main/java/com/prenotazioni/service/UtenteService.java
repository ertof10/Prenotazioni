package com.prenotazioni.service;

import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UtenteService {

    UtenteTo saveOrUpdateUtente(UtenteTo utenteTo);

    UtenteTo getUtenteById(Integer idUtente);

    Page<UtenteTo> getAllUtenti(Pageable pageable);

    EsitoResponse deleteUtente(Integer idUtente);

    UtenteTo disattivaUtente(Integer idUtente);

    UtenteTo riattivaUtente(Integer idUtente);
}