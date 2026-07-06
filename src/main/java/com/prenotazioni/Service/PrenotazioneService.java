package com.prenotazioni.Service;

import com.prenotazioni.Dto.PrenotazioneTo;
import com.prenotazioni.Response.EsitoResponse;

import java.util.List;

public interface PrenotazioneService {

    PrenotazioneTo saveOrUpdatePrenotazione(PrenotazioneTo prenotazioneTo);

    PrenotazioneTo getPrenotazioneById(Integer idPrenotazione);

    List<PrenotazioneTo> getAllPrenotazioni();

    List<PrenotazioneTo> getPrenotazioniByUtente(Integer idUtente);

    EsitoResponse deletePrenotazione(Integer idPrenotazione);

    PrenotazioneTo utenteAnnullaPrenotazione(Integer idPrenotazione);

    PrenotazioneTo strutturaAnnullaPrenotazione(Integer idPrenotazione);

    List<PrenotazioneTo> getPrenotazioniDaRiprogrammare();

    List<PrenotazioneTo> getPrenotazioniDaRiprogrammareByUtente(Integer idUtente);

}