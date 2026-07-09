package com.prenotazioni.service;

import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.response.EsitoResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CalendarioService {

    CalendarioTo saveOrUpdateCalendario(CalendarioTo calendarioTo);

    CalendarioTo getCalendarioById(Integer idCalendario);

    Page<CalendarioTo> getAllCalendari(org.springframework.data.domain.Pageable pageable);

    List<CalendarioTo> getCalendariAttivi();

    List<CalendarioTo> getCalendariByCollaboratore(Integer idCollaboratore);

    List<CalendarioTo> getCalendariAttiviByCollaboratore(Integer idCollaboratore);

    EsitoResponse deleteCalendario(Integer idCalendario);
}