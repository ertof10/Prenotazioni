package com.prenotazioni.service;

import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.response.EsitoResponse;

import java.util.List;

public interface CalendarioService {

    CalendarioTo saveOrUpdateCalendario(CalendarioTo calendarioTo);

    CalendarioTo getCalendarioById(Integer idCalendario);

    List<CalendarioTo> getAllCalendari();

    List<CalendarioTo> getCalendariAttivi();

    List<CalendarioTo> getCalendariByCollaboratore(Integer idCollaboratore);

    List<CalendarioTo> getCalendariAttiviByCollaboratore(Integer idCollaboratore);

    EsitoResponse deleteCalendario(Integer idCalendario);
}