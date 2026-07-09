package com.prenotazioni.service;

import com.prenotazioni.dto.PrenotazioneTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrenotazioneService {

    PrenotazioneTo saveOrUpdatePrenotazione(PrenotazioneTo prenotazioneTo);

    PrenotazioneTo getPrenotazioneById(Integer idPrenotazione);

    Page<PrenotazioneTo> getAllPrenotazioni(Pageable pageable);

    List<PrenotazioneTo> getPrenotazioniByUtente(Integer idUtente);

    EsitoResponse deletePrenotazione(Integer idPrenotazione);

    PrenotazioneTo utenteAnnullaPrenotazione(Integer idPrenotazione);

    PrenotazioneTo strutturaAnnullaPrenotazione(Integer idPrenotazione);

    List<PrenotazioneTo> getPrenotazioniDaRiprogrammare();

    List<PrenotazioneTo> getPrenotazioniDaRiprogrammareByUtente(Integer idUtente);

}