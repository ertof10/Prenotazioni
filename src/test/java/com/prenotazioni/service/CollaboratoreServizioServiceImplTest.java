package com.prenotazioni.service;

import com.prenotazioni.dao.CollaboratoreRepository;
import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dto.CollaboratoreServizioTo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.CollaboratoreServizioMapper;
import com.prenotazioni.po.CollaboratorePo;
import com.prenotazioni.po.CollaboratoreServizioPo;
import com.prenotazioni.po.ServizioPo;
import com.prenotazioni.response.EsitoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaboratoreServizioServiceImplTest {
    @Mock
    private CollaboratoreServizioRepository collaboratoreServizioRepository;
    @Mock
    private CollaboratoreRepository collaboratoreRepository;
    @Mock
    private ServizioRepository servizioRepository;
    @Mock
    private CollaboratoreServizioMapper collaboratoreServizioMapper;
    @Mock
    private Clock clock;
    @InjectMocks
    private CollaboratoreServizioServiceImpl collaboratoreServizioService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-07T10:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Europe/Rome"));
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoNuovaAssociazioneValida_salvaAttivaTrue() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore();
        ServizioPo servizioPo = creaServizio();
        CollaboratoreServizioTo risposta = creaCollaboratoreServizioBase();
        risposta.setIdCollaboratoreServizio(1);
        risposta.setAttivoCollaboratoreServizio(true);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(3, 2)).thenReturn(false);
        when(collaboratoreServizioRepository.save(any(CollaboratoreServizioPo.class))).thenAnswer(invocation -> {
            CollaboratoreServizioPo collaboratoreServizioPo = invocation.getArgument(0);
            collaboratoreServizioPo.setIdCollaboratoreServizio(1);
            return collaboratoreServizioPo;
        });
        when(collaboratoreServizioMapper.toDto(any(CollaboratoreServizioPo.class))).thenReturn(risposta);
        CollaboratoreServizioTo risultato = collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdCollaboratoreServizio());
        assertTrue(risultato.getAttivoCollaboratoreServizio());
        ArgumentCaptor<CollaboratoreServizioPo> captor = ArgumentCaptor.forClass(CollaboratoreServizioPo.class);
        verify(collaboratoreServizioRepository).save(captor.capture());
        assertEquals(collaboratorePo, captor.getValue().getCollaboratorePo());
        assertEquals(servizioPo, captor.getValue().getServizioPo());
        assertTrue(captor.getValue().getAttivoCollaboratoreServizio());
        assertEquals(45, captor.getValue().getDurataPersonalizzataMinuti());
        assertEquals(BigDecimal.valueOf(25), captor.getValue().getPrezzoPersonalizzato());
        assertNotNull(captor.getValue().getDataCreazioneCollaboratoreServizio());
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoCollaboratoreNonEsiste_lanciaServiceException() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo));
        assertEquals(AppError.COLLABORATORE_NON_TROVATO, exception.getError());
        verify(collaboratoreRepository).findById(3);
        verifyNoInteractions(servizioRepository);
        verify(collaboratoreServizioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoServizioNonEsiste_lanciaServiceException() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore();
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo));
        assertEquals(AppError.SERVIZIO_NON_TROVATO, exception.getError());
        verify(servizioRepository).findById(2);
        verify(collaboratoreServizioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoAssociazioneGiaPresente_lanciaServiceException() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        CollaboratorePo collaboratorePo = creaCollaboratore();
        ServizioPo servizioPo = creaServizio();
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(collaboratoreServizioRepository.existsAssociazioneAttiva(3, 2)).thenReturn(true);
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo));
        assertEquals(AppError.COLLABORATORE_SERVIZIO_GIA_PRESENTE, exception.getError());
        verify(collaboratoreServizioRepository, never()).save(any());
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoModificaValida_salvaModifica() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        collaboratoreServizioTo.setIdCollaboratoreServizio(1);
        collaboratoreServizioTo.setAttivoCollaboratoreServizio(false);
        collaboratoreServizioTo.setDurataPersonalizzataMinuti(60);
        collaboratoreServizioTo.setPrezzoPersonalizzato(BigDecimal.valueOf(30));
        CollaboratorePo collaboratorePo = creaCollaboratore();
        ServizioPo servizioPo = creaServizio();
        CollaboratoreServizioPo collaboratoreServizioPo = new CollaboratoreServizioPo();
        collaboratoreServizioPo.setIdCollaboratoreServizio(1);
        collaboratoreServizioPo.setAttivoCollaboratoreServizio(true);
        CollaboratoreServizioTo risposta = creaCollaboratoreServizioBase();
        risposta.setIdCollaboratoreServizio(1);
        risposta.setAttivoCollaboratoreServizio(false);
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(collaboratoreServizioRepository.findById(1)).thenReturn(Optional.of(collaboratoreServizioPo));
        when(collaboratoreServizioRepository.save(collaboratoreServizioPo)).thenReturn(collaboratoreServizioPo);
        when(collaboratoreServizioMapper.toDto(collaboratoreServizioPo)).thenReturn(risposta);
        CollaboratoreServizioTo risultato = collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo);
        assertNotNull(risultato);
        assertFalse(risultato.getAttivoCollaboratoreServizio());
        assertEquals(collaboratorePo, collaboratoreServizioPo.getCollaboratorePo());
        assertEquals(servizioPo, collaboratoreServizioPo.getServizioPo());
        assertFalse(collaboratoreServizioPo.getAttivoCollaboratoreServizio());
        assertEquals(60, collaboratoreServizioPo.getDurataPersonalizzataMinuti());
        assertEquals(BigDecimal.valueOf(30), collaboratoreServizioPo.getPrezzoPersonalizzato());
        assertNotNull(collaboratoreServizioPo.getDataModificaCollaboratoreServizio());
        verify(collaboratoreServizioRepository).save(collaboratoreServizioPo);
    }

    @Test
    void saveOrUpdateCollaboratoreServizio_quandoModificaNonTrovata_lanciaServiceException() {
        CollaboratoreServizioTo collaboratoreServizioTo = creaCollaboratoreServizioBase();
        collaboratoreServizioTo.setIdCollaboratoreServizio(99);
        CollaboratorePo collaboratorePo = creaCollaboratore();
        ServizioPo servizioPo = creaServizio();
        when(collaboratoreRepository.findById(3)).thenReturn(Optional.of(collaboratorePo));
        when(servizioRepository.findById(2)).thenReturn(Optional.of(servizioPo));
        when(collaboratoreServizioRepository.findById(99)).thenReturn(Optional.empty());
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.saveOrUpdateCollaboratoreServizio(collaboratoreServizioTo));
        assertEquals(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO, exception.getError());
        verify(collaboratoreServizioRepository, never()).save(any());
    }

    @Test
    void getCollaboratoreServizioById_quandoEsiste_restituisceDto() {
        CollaboratoreServizioPo collaboratoreServizioPo = new CollaboratoreServizioPo();
        collaboratoreServizioPo.setIdCollaboratoreServizio(1);
        CollaboratoreServizioTo collaboratoreServizioTo = new CollaboratoreServizioTo();
        collaboratoreServizioTo.setIdCollaboratoreServizio(1);
        when(collaboratoreServizioRepository.findById(1)).thenReturn(Optional.of(collaboratoreServizioPo));
        when(collaboratoreServizioMapper.toDto(collaboratoreServizioPo)).thenReturn(collaboratoreServizioTo);
        CollaboratoreServizioTo risultato = collaboratoreServizioService.getCollaboratoreServizioById(1);
        assertNotNull(risultato);
        assertEquals(1, risultato.getIdCollaboratoreServizio());
        verify(collaboratoreServizioRepository).findById(1);
    }

    @Test
    void getCollaboratoreServizioById_quandoIdNonValido_lanciaServiceException() {
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.getCollaboratoreServizioById(0));
        assertEquals(AppError.ID_NON_VALIDO, exception.getError());
        verifyNoInteractions(collaboratoreServizioRepository);
    }

    @Test
    void getAllCollaboratoriServizi_quandoPresenti_restituisceLista() {
        CollaboratoreServizioPo collaboratoreServizioPo = new CollaboratoreServizioPo();
        collaboratoreServizioPo.setIdCollaboratoreServizio(1);
        CollaboratoreServizioTo collaboratoreServizioTo = new CollaboratoreServizioTo();
        collaboratoreServizioTo.setIdCollaboratoreServizio(1);
        when(collaboratoreServizioRepository.findAll()).thenReturn(Collections.singletonList(collaboratoreServizioPo));
        when(collaboratoreServizioMapper.toDto(collaboratoreServizioPo)).thenReturn(collaboratoreServizioTo);
        List<CollaboratoreServizioTo> risultato = collaboratoreServizioService.getAllCollaboratoriServizi();
        assertEquals(1, risultato.size());
        assertEquals(1, risultato.get(0).getIdCollaboratoreServizio());
        verify(collaboratoreServizioRepository).findAll();
    }

    @Test
    void deleteCollaboratoreServizio_quandoEsiste_eliminaERitornaEsito() {
        when(collaboratoreServizioRepository.existsById(1)).thenReturn(true);
        EsitoResponse risultato = collaboratoreServizioService.deleteCollaboratoreServizio(1);
        assertNotNull(risultato);
        assertEquals("Associazione collaboratore servizio eliminata correttamente", risultato.getMessage());
        verify(collaboratoreServizioRepository).existsById(1);
        verify(collaboratoreServizioRepository).deleteById(1);
    }

    @Test
    void deleteCollaboratoreServizio_quandoNonEsiste_lanciaServiceException() {
        when(collaboratoreServizioRepository.existsById(99)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class, () -> collaboratoreServizioService.deleteCollaboratoreServizio(99));
        assertEquals(AppError.COLLABORATORE_SERVIZIO_NON_TROVATO, exception.getError());
        verify(collaboratoreServizioRepository).existsById(99);
        verify(collaboratoreServizioRepository, never()).deleteById(any());
    }

    private CollaboratoreServizioTo creaCollaboratoreServizioBase() {
        CollaboratoreServizioTo collaboratoreServizioTo = new CollaboratoreServizioTo();
        collaboratoreServizioTo.setIdCollaboratore(3);
        collaboratoreServizioTo.setIdServizio(2);
        collaboratoreServizioTo.setDurataPersonalizzataMinuti(45);
        collaboratoreServizioTo.setPrezzoPersonalizzato(BigDecimal.valueOf(25));
        collaboratoreServizioTo.setNoteCollaboratoreServizio("Test associazione");
        return collaboratoreServizioTo;
    }

    private CollaboratorePo creaCollaboratore() {
        CollaboratorePo collaboratorePo = new CollaboratorePo();
        collaboratorePo.setIdCollaboratore(3);
        collaboratorePo.setAttivoCollaboratore(true);
        return collaboratorePo;
    }

    private ServizioPo creaServizio() {
        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(2);
        servizioPo.setAttivoServizio(true);
        return servizioPo;
    }
}