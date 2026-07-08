package com.prenotazioni.service;

import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.UtenteRepository;
import com.prenotazioni.dto.UtenteTo;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.UtenteMapper;
import com.prenotazioni.po.UtentePo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtenteServiceImplTest {

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private UtenteMapper utenteMapper;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @Mock
    private PreventivoRepository preventivoRepository;

    @InjectMocks
    private UtenteServiceImpl utenteService;

    @Test
    void disattivaUtente_quandoUtenteEsiste_impostaAttivoFalse() {

        Integer idUtente = 3;

        UtentePo utentePo = new UtentePo();
        utentePo.setIdUtente(idUtente);
        utentePo.setNomeUtente("Mario");
        utentePo.setCognomeUtente("Rossi");
        utentePo.setEmailUtente("mario.rossi@email.com");
        utentePo.setAttivoUtente(true);

        UtenteTo utenteTo = new UtenteTo();
        utenteTo.setIdUtente(idUtente);
        utenteTo.setNomeUtente("Mario");
        utenteTo.setCognomeUtente("Rossi");
        utenteTo.setEmailUtente("mario.rossi@email.com");
        utenteTo.setAttivoUtente(false);

        when(utenteRepository.findById(idUtente)).thenReturn(Optional.of(utentePo));
        when(utenteRepository.save(utentePo)).thenReturn(utentePo);
        when(utenteMapper.toDto(utentePo)).thenReturn(utenteTo);

        UtenteTo risultato = utenteService.disattivaUtente(idUtente);

        assertNotNull(risultato);
        assertFalse(risultato.getAttivoUtente());

        verify(utenteRepository).findById(idUtente);
        verify(utenteRepository).save(utentePo);
        verify(utenteMapper).toDto(utentePo);
    }

    @Test
    void riattivaUtente_quandoUtenteEsiste_impostaAttivoTrue() {

        Integer idUtente = 3;

        UtentePo utentePo = new UtentePo();
        utentePo.setIdUtente(idUtente);
        utentePo.setNomeUtente("Mario");
        utentePo.setCognomeUtente("Rossi");
        utentePo.setEmailUtente("mario.rossi@email.com");
        utentePo.setAttivoUtente(false);

        UtenteTo utenteTo = new UtenteTo();
        utenteTo.setIdUtente(idUtente);
        utenteTo.setNomeUtente("Mario");
        utenteTo.setCognomeUtente("Rossi");
        utenteTo.setEmailUtente("mario.rossi@email.com");
        utenteTo.setAttivoUtente(true);

        when(utenteRepository.findById(idUtente)).thenReturn(Optional.of(utentePo));
        when(utenteRepository.save(utentePo)).thenReturn(utentePo);
        when(utenteMapper.toDto(utentePo)).thenReturn(utenteTo);

        UtenteTo risultato = utenteService.riattivaUtente(idUtente);

        assertNotNull(risultato);
        assertTrue(risultato.getAttivoUtente());

        verify(utenteRepository).findById(idUtente);
        verify(utenteRepository).save(utentePo);
        verify(utenteMapper).toDto(utentePo);
    }

    @Test
    void disattivaUtente_quandoIdNonValido_lanciaServiceException() {

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> utenteService.disattivaUtente(0)
        );

        assertEquals(AppError.ID_NON_VALIDO, exception.getError());

        verifyNoInteractions(utenteRepository);
    }

    @Test
    void disattivaUtente_quandoUtenteNonEsiste_lanciaServiceException() {

        Integer idUtente = 999;

        when(utenteRepository.findById(idUtente)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> utenteService.disattivaUtente(idUtente)
        );

        assertEquals(AppError.UTENTE_NON_TROVATO, exception.getError());

        verify(utenteRepository).findById(idUtente);
        verify(utenteRepository, never()).save(any());
    }
}