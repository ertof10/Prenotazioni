package com.prenotazioni.Service;

import com.prenotazioni.Dao.CalendarioRepository;
import com.prenotazioni.Dao.CollaboratoreRepository;
import com.prenotazioni.Dto.CalendarioTo;
import com.prenotazioni.Error.AppError;
import com.prenotazioni.Error.ServiceException;
import com.prenotazioni.Mapper.CalendarioMapper;
import com.prenotazioni.Po.CalendarioPo;
import com.prenotazioni.Po.CollaboratorePo;
import com.prenotazioni.Response.EsitoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarioServiceImpl implements CalendarioService {

    private final CalendarioRepository calendarioRepository;
    private final CollaboratoreRepository collaboratoreRepository;
    private final CalendarioMapper calendarioMapper;

    public CalendarioServiceImpl(CalendarioRepository calendarioRepository,
                                 CollaboratoreRepository collaboratoreRepository,
                                 CalendarioMapper calendarioMapper) {
        this.calendarioRepository = calendarioRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.calendarioMapper = calendarioMapper;
    }

    @Override
    @Transactional
    public CalendarioTo saveOrUpdateCalendario(CalendarioTo calendarioTo) {

        if (calendarioTo.getOraFineCalendario().isBefore(calendarioTo.getOraInizioCalendario())
                || calendarioTo.getOraFineCalendario().equals(calendarioTo.getOraInizioCalendario())) {
            throw new ServiceException(AppError.ORARIO_CALENDARIO_NON_VALIDO);
        }

        CollaboratorePo collaboratorePo = collaboratoreRepository
                .findById(calendarioTo.getIdCollaboratore())
                .orElse(null);

        if (collaboratorePo == null) {
            throw new ServiceException(AppError.COLLABORATORE_NON_TROVATO);
        }

        if (!Boolean.TRUE.equals(collaboratorePo.getAttivoCollaboratore())) {
            throw new ServiceException(AppError.COLLABORATORE_NON_ATTIVO);
        }

        DayOfWeek giornoSettimanaCalendario;

        try {
            giornoSettimanaCalendario = DayOfWeek.valueOf(
                    calendarioTo.getGiornoSettimanaCalendario().trim().toUpperCase()
            );
        } catch (Exception e) {
            throw new ServiceException(AppError.GIORNO_CALENDARIO_NON_VALIDO, e);
        }

        boolean calendarioSovrapposto =
                calendarioRepository.existsCalendarioSovrapposto(
                        calendarioTo.getIdCalendario(),
                        calendarioTo.getIdCollaboratore(),
                        giornoSettimanaCalendario,
                        calendarioTo.getOraInizioCalendario(),
                        calendarioTo.getOraFineCalendario()
                );

        if (calendarioSovrapposto) {
            throw new ServiceException(AppError.CALENDARIO_GIA_PRESENTE);
        }

        if (calendarioTo.getIdCalendario() == null) {

            try {
                CalendarioPo calendarioPo = new CalendarioPo();

                calendarioPo.setCollaboratorePo(collaboratorePo);
                calendarioPo.setGiornoSettimanaCalendario(giornoSettimanaCalendario);
                calendarioPo.setOraInizioCalendario(calendarioTo.getOraInizioCalendario());
                calendarioPo.setOraFineCalendario(calendarioTo.getOraFineCalendario());
                calendarioPo.setAttivoCalendario(true);
                calendarioPo.setDataCreazioneCalendario(LocalDateTime.now());

                calendarioPo = calendarioRepository.save(calendarioPo);

                return calendarioMapper.toDto(calendarioPo);

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException(AppError.CALENDARIO_CREAZIONE_FALLITA, e);
            }
        }

        CalendarioPo calendarioPo = calendarioRepository
                .findById(calendarioTo.getIdCalendario())
                .orElse(null);

        if (calendarioPo == null) {
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        try {
            calendarioPo.setCollaboratorePo(collaboratorePo);
            calendarioPo.setGiornoSettimanaCalendario(giornoSettimanaCalendario);
            calendarioPo.setOraInizioCalendario(calendarioTo.getOraInizioCalendario());
            calendarioPo.setOraFineCalendario(calendarioTo.getOraFineCalendario());
            calendarioPo.setDataModificaCalendario(LocalDateTime.now());

            if (calendarioTo.getAttivoCalendario() != null) {
                calendarioPo.setAttivoCalendario(calendarioTo.getAttivoCalendario());
            }

            calendarioPo = calendarioRepository.save(calendarioPo);

            return calendarioMapper.toDto(calendarioPo);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(AppError.CALENDARIO_MODIFICA_FALLITA, e);
        }
    }

    @Override
    public CalendarioTo getCalendarioById(Integer idCalendario) {

        if (idCalendario == null || idCalendario <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        CalendarioPo calendarioPo = calendarioRepository.findById(idCalendario).orElse(null);

        if (calendarioPo == null) {
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        return calendarioMapper.toDto(calendarioPo);
    }

    @Override
    public List<CalendarioTo> getAllCalendari() {
        return calendarioRepository.findAll()
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarioTo> getCalendariAttivi() {
        return calendarioRepository.findByAttivoCalendarioTrue()
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarioTo> getCalendariByCollaboratore(Integer idCollaboratore) {
        return calendarioRepository.findByCollaboratorePo_IdCollaboratore(idCollaboratore)
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarioTo> getCalendariAttiviByCollaboratore(Integer idCollaboratore) {
        return calendarioRepository.findByCollaboratorePo_IdCollaboratoreAndAttivoCalendarioTrue(idCollaboratore)
                .stream()
                .map(calendarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EsitoResponse deleteCalendario(Integer idCalendario) {

        if (idCalendario == null || idCalendario <= 0) {
            throw new ServiceException(AppError.ID_NON_VALIDO);
        }

        if (!calendarioRepository.existsById(idCalendario)) {
            throw new ServiceException(AppError.CALENDARIO_NON_TROVATO);
        }

        try {
            calendarioRepository.deleteById(idCalendario);
            return new EsitoResponse("Calendario eliminato correttamente");
        } catch (Exception e) {
            throw new ServiceException(AppError.CALENDARIO_ELIMINAZIONE_FALLITA, e);
        }
    }
}