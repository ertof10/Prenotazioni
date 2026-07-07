package com.prenotazioni.service;


import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface UtenteService {

    UtenteTo saveOrUpdateUtente(UtenteTo utenteTo);

    UtenteTo getUtenteById(Integer idUtente);

    List<UtenteTo> getAllUtenti();

    EsitoResponse deleteUtente(Integer idUtente);

    UtenteTo disattivaUtente(Integer idUtente);

    UtenteTo riattivaUtente(Integer idUtente);
}