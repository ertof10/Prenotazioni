package com.prenotazioni.service;import com.prenotazioni.dao.CalendarioRepository;
import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dto.CalendarioTo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CalendarioMapper;
import com.prenotazioni.po.CalendarioPo;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.response.EsitoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarioServiceImplTest {
    @Mock
    private CalendarioRepository calendarioRepository;
    @Mock
    private CollaboratoreRepository collaboratoreRepository;
    @Mock
    private CalendarioMapper calendarioMapper;
    @Mock
    private Clock clock;
    @InjectMocks
    private CalendarioServiceImpl calendarioService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-07T10:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Europe/Rome"));
    }

    @Test
    void saveOrUpdateCalendario_quandoNuovoCalendarioValido_salvaAttivoTrue() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore(true);
        CalendarioTo risposta = creaCalendarioBase();
        risposta.setIdCalendario(1);
        risposta.setAttivoCalendario(true);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(calendarioRepository.existsCalendarioSovrapposto(any(), eq(3), eq(DayOfWeek.MONDAY), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(calendarioRepository.save(any(CalendarioPo.class))).thenAnswer(invocation -> {
            CalendarioPo calendarioPo = invocation.getArgument(0);
            calendarioPo.setIdCalendario(1);
            return calendarioPo;
        });
        when(calendarioMapper.toDto(any(CalendarioPo.class))).thenReturn(risposta);
        CalendarioTo risultato = calendarioService.saveOrUpdateCalendario(calendarioTo);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdCalendario());
        assertTrue(risultato.getAttivoCalendario());
        ArgumentCaptor<CalendarioPo> captor = ArgumentCaptor.forClass(CalendarioPo.class);
        verify(calendarioRepository).save(captor.capture());
        assertEquals(collaboratorePo, captor.getValue().getCollaboratorePo());
        assertEquals(DayOfWeek.MONDAY, captor.getValue().getGiornoSettimanaCalendario());
        assertTrue(captor.getValue().getAttivoCalendario());
        assertNotNull(captor.getValue().getDataCreazioneCalendario());
    }

    @Test
    void saveOrUpdateCalendario_quandoOrarioNonValido_lanciaServiceException() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        calendarioTo.setOraInizioCalendario(LocalTime.of(13, 0));
        calendarioTo.setOraFineCalendario(LocalTime.of(9, 0));
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.saveOrUpdateCalendario(calendarioTo));
        assertEquals(AppError.ORARIO_CALENDARIO_NON_VALIDO, exception.getError());
        verifyNoInteractions(collaboratoreRepository);
        verifyNoInteractions(calendarioRepository);
    }

    @Test
    void saveOrUpdateCalendario_quandoCollaboratoreNonEsiste_lanciaServiceException() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.saveOrUpdateCalendario(calendarioTo));
        assertEquals(AppError.COLLABORATORE_NON_TROVATO, exception.getError());
        verify(collaboratoreRepository).findById(3);
        verify(calendarioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCalendario_quandoCollaboratoreNonAttivo_lanciaServiceException() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore(false);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.saveOrUpdateCalendario(calendarioTo));
        assertEquals(AppError.COLLABORATORE_NON_ATTIVO, exception.getError());
        verify(collaboratoreRepository).findById(3);
        verify(calendarioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCalendario_quandoGiornoNonValido_lanciaServiceException() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        calendarioTo.setGiornoSettimanaCalendario("LUNEDI");
        CollaboratorePo collaboratorePo = creaCollaboratore(true);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.saveOrUpdateCalendario(calendarioTo));
        assertEquals(AppError.GIORNO_CALENDARIO_NON_VALIDO, exception.getError());
        verify(calendarioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCalendario_quandoCalendarioSovrapposto_lanciaServiceException() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore(true);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(calendarioRepository.existsCalendarioSovrapposto(any(), eq(3), eq(DayOfWeek.MONDAY), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.saveOrUpdateCalendario(calendarioTo));
        assertEquals(AppError.CALENDARIO_GIA_PRESENTE, exception.getError());
        verify(calendarioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCalendario_quandoModificaCalendarioValido_salvaModifica() {
        CalendarioTo calendarioTo = creaCalendarioBase();
        calendarioTo.setIdCalendario(1);
        calendarioTo.setAttivoCalendario(false);
        CollaboratorePo collaboratorePo = creaCollaboratore(true);
        CalendarioPo calendarioPo = new CalendarioPo();
        calendarioPo.setIdCalendario(1);
        calendarioPo.setAttivoCalendario(true);
        CalendarioTo risposta = creaCalendarioBase();
        risposta.setIdCalendario(1);
        risposta.setAttivoCalendario(false);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(calendarioRepository.existsCalendarioSovrapposto(eq(1), eq(3), eq(DayOfWeek.MONDAY), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(calendarioRepository.findById(1)).thenReturn(Optional.of(calendarioPo));
        when(calendarioRepository.save(calendarioPo)).thenReturn(calendarioPo);
        when(calendarioMapper.toDto(calendarioPo)).thenReturn(risposta);
        CalendarioTo risultato = calendarioService.saveOrUpdateCalendario(calendarioTo);
        assertNotNull(risultato);
        assertFalse(risultato.getAttivoCalendario());
        assertEquals(collaboratorePo, calendarioPo.getCollaboratorePo());
        assertEquals(DayOfWeek.MONDAY, calendarioPo.getGiornoSettimanaCalendario());
        assertFalse(calendarioPo.getAttivoCalendario());
        assertNotNull(calendarioPo.getDataModificaCalendario());
        verify(calendarioRepository).save(calendarioPo);
    }

    @Test
    void getCalendarioById_quandoEsiste_restituisceDto() {
        CalendarioPo calendarioPo = new CalendarioPo();
        calendarioPo.setIdCalendario(1);
        CalendarioTo calendarioTo = new CalendarioTo();
        calendarioTo.setIdCalendario(1);
        when(calendarioRepository.findById(1)).thenReturn(Optional.of(calendarioPo));
        when(calendarioMapper.toDto(calendarioPo)).thenReturn(calendarioTo);
        CalendarioTo risultato = calendarioService.getCalendarioById(1);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdCalendario());
        verify(calendarioRepository).findById(1);
        verify(calendarioMapper).toDto(calendarioPo);
    }

    @Test
    void getCalendarioById_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.getCalendarioById(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(calendarioRepository);
    }

    @Test
    void getAllCalendari_quandoPresenti_restituisceLista() {
        CalendarioPo calendarioPo = new CalendarioPo();
        calendarioPo.setIdCalendario(1);
        CalendarioTo calendarioTo = new CalendarioTo();
        calendarioTo.setIdCalendario(1);
        when(calendarioRepository.findAll()).thenReturn(Collections.singletonList(calendarioPo));
        when(calendarioMapper.toDto(calendarioPo)).thenReturn(calendarioTo);
        List<CalendarioTo> risultato = calendarioService.getAllCalendari();
        assertEquals(1, risultato.size());
        assertEquals(1, risultato.get(0).getIdCalendario());
        verify(calendarioRepository).findAll();
    }

    @Test
    void deleteCalendario_quandoEsiste_eliminaERitornaEsito() {
        when(calendarioRepository.existsById(1)).thenReturn(true);
        EsitoResponse risultato = calendarioService.deleteCalendario(1);
        assertNotNull(risultato);
        assertEquals("Calendario eliminato correttamente", risultato.getMessage());
        verify(calendarioRepository).existsById(1);
        verify(calendarioRepository).deleteById(1);
    }

    @Test
    void deleteCalendario_quandoNonEsiste_lanciaServiceException() {
        when(calendarioRepository.existsById(99)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> calendarioService.deleteCalendario(99));
        assertEquals(AppError.CALENDARIO_NON_TROVATO, exception.getError());
        verify(calendarioRepository).existsById(99);
        verify(calendarioRepository, never()).deleteById(any());
    }

    private CalendarioTo creaCalendarioBase() {
        CalendarioTo calendarioTo = new CalendarioTo();
        calendarioTo.setIdCollaboratore(3);
        calendarioTo.setGiornoSettimanaCalendario("MONDAY");
        calendarioTo.setOraInizioCalendario(LocalTime.of(9, 0));
        calendarioTo.setOraFineCalendario(LocalTime.of(13, 0));
        return calendarioTo;
    }

    private CollaboratorePo creaCollaboratore(boolean attivo) {
        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(3);
        collaboratorePo.setAttivoCollaboratore(attivo);
        return collaboratorePo;
    }
}