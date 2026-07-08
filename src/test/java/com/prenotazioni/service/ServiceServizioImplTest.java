package com.prenotazioni.service;

import com.prenotazioni.dao.CollaboratoreServizioRepository;
import com.prenotazioni.dao.PrenotazioneRepository;
import com.prenotazioni.dao.PreventivoRepository;
import com.prenotazioni.dao.ServizioRepository;
import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.enums.TipoGestioneServizio;
import com.prenotazioni.error.AppError;
import com.prenotazioni.error.ServiceException;
import com.prenotazioni.mapper.ServizioMapper;
import com.prenotazioni.po.ServizioPo;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServizioServiceImplTest {

    @Mock
    private ServizioRepository servizioRepository;

    @Mock
    private ServizioMapper servizioMapper;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @Mock
    private CollaboratoreServizioRepository collaboratoreServizioRepository;

    @Mock
    private PreventivoRepository preventivoRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private ServizioServiceImpl servizioService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-07T10:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Europe/Rome"));
    }

    @Test
    void disattivaServizio_quandoServizioEsiste_impostaAttivoFalse() {

        Integer idServizio = 2;

        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(idServizio);
        servizioPo.setAttivoServizio(true);

        ServizioTo servizioTo = new ServizioTo();
        servizioTo.setIdServizio(idServizio);
        servizioTo.setAttivoServizio(false);

        when(servizioRepository.findById(idServizio)).thenReturn(Optional.of(servizioPo));
        when(servizioRepository.save(servizioPo)).thenReturn(servizioPo);
        when(servizioMapper.toDto(servizioPo)).thenReturn(servizioTo);

        ServizioTo risultato = servizioService.disattivaServizio(idServizio);

        assertNotNull(risultato);
        assertFalse(risultato.getAttivoServizio());

        verify(servizioRepository).findById(idServizio);
        verify(servizioRepository).save(servizioPo);
        verify(servizioMapper).toDto(servizioPo);
    }

    @Test
    void riattivaServizio_quandoServizioEsiste_impostaAttivoTrue() {

        Integer idServizio = 2;

        ServizioPo servizioPo = new ServizioPo();
        servizioPo.setIdServizio(idServizio);
        servizioPo.setAttivoServizio(false);

        ServizioTo servizioTo = new ServizioTo();
        servizioTo.setIdServizio(idServizio);
        servizioTo.setAttivoServizio(true);

        when(servizioRepository.findById(idServizio)).thenReturn(Optional.of(servizioPo));
        when(servizioRepository.save(servizioPo)).thenReturn(servizioPo);
        when(servizioMapper.toDto(servizioPo)).thenReturn(servizioTo);

        ServizioTo risultato = servizioService.riattivaServizio(idServizio);

        assertNotNull(risultato);
        assertTrue(risultato.getAttivoServizio());

        verify(servizioRepository).findById(idServizio);
        verify(servizioRepository).save(servizioPo);
        verify(servizioMapper).toDto(servizioPo);
    }

    @Test
    void saveOrUpdateServizio_quandoServizioAPostiSenzaPosti_lanciaServiceException() {

        ServizioTo servizioTo = new ServizioTo();
        servizioTo.setNomeServizio("Ingresso evento");
        servizioTo.setCategoriaServizio("Evento");
        servizioTo.setPrezzoServizio(BigDecimal.TEN);
        servizioTo.setDurataMinutiServizio(60);
        servizioTo.setTipoGestioneServizio("A_POSTI");
        servizioTo.setPostiMassimiServizio(null);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> servizioService.saveOrUpdateServizio(servizioTo)
        );

        assertEquals(AppError.SERVIZIO_A_POSTI_NON_CONFIGURATO, exception.getError());

        verifyNoInteractions(servizioRepository);
    }

    @Test
    void saveOrUpdateServizio_quandoNuovoServizioValido_salvaAttivoTrue() {

        ServizioTo servizioTo = new ServizioTo();
        servizioTo.setNomeServizio("Taglio uomo");
        servizioTo.setCategoriaServizio("Capelli");
        servizioTo.setDescrizioneServizio("Taglio capelli uomo");
        servizioTo.setPrezzoServizio(BigDecimal.valueOf(20));
        servizioTo.setDurataMinutiServizio(35);
        servizioTo.setTipoGestioneServizio("CON_COLLABORATORE");
        servizioTo.setPostiMassimiServizio(10);

        ServizioPo servizioPo = new ServizioPo();

        ServizioTo risposta = new ServizioTo();
        risposta.setIdServizio(2);
        risposta.setNomeServizio("TAGLIO UOMO");
        risposta.setAttivoServizio(true);

        when(servizioRepository.existsByNomeServizio("TAGLIO UOMO")).thenReturn(false);
        when(servizioMapper.toEntity(servizioTo)).thenReturn(servizioPo);

        when(servizioRepository.save(any(ServizioPo.class)))
                .thenAnswer(invocation -> {
                    ServizioPo salvato = invocation.getArgument(0);
                    salvato.setIdServizio(2);
                    return salvato;
                });

        when(servizioMapper.toDto(any(ServizioPo.class))).thenReturn(risposta);

        ServizioTo risultato = servizioService.saveOrUpdateServizio(servizioTo);

        assertNotNull(risultato);
        assertTrue(risultato.getAttivoServizio());

        ArgumentCaptor<ServizioPo> captor = ArgumentCaptor.forClass(ServizioPo.class);
        verify(servizioRepository).save(captor.capture());

        assertTrue(captor.getValue().getAttivoServizio());
        assertEquals(TipoGestioneServizio.CON_COLLABORATORE, captor.getValue().getTipoGestioneServizio());
        assertNull(captor.getValue().getPostiMassimiServizio());
    }
}