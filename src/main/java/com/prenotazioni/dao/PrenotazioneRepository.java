package com.prenotazioni.dao;

import com.prenotazioni.enums.StatoPrenotazione;
import com.prenotazioni.po.PrenotazionePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<PrenotazionePo, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT prenotazionePo " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE prenotazionePo.idPrenotazione = :idPrenotazione")
    Optional<PrenotazionePo> findByIdForUpdate(
            @Param("idPrenotazione") Integer idPrenotazione
    );

    List<PrenotazionePo> findByUtentePo_IdUtente(Integer idUtente);

    List<PrenotazionePo> findByServizioPo_IdServizio(Integer idServizio);

    @Query("SELECT CASE WHEN COUNT(prenotazionePo) > 0 THEN true ELSE false END " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.utentePo.idUtente = :idUtente " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFinePrenotazione " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioPrenotazione")
    boolean existsPrenotazioneSovrappostaUtente(
            @Param("idPrenotazione") Integer idPrenotazione,
            @Param("idUtente") Integer idUtente,
            @Param("dataPrenotazione") LocalDate dataPrenotazione,
            @Param("oraInizioPrenotazione") LocalTime oraInizioPrenotazione,
            @Param("oraFinePrenotazione") LocalTime oraFinePrenotazione
    );

    @Query("SELECT CASE WHEN COUNT(prenotazionePo) > 0 THEN true ELSE false END " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFinePrenotazione " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioPrenotazione")
    boolean existsPrenotazioneSovrappostaCollaboratore(
            @Param("idPrenotazione") Integer idPrenotazione,
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataPrenotazione") LocalDate dataPrenotazione,
            @Param("oraInizioPrenotazione") LocalTime oraInizioPrenotazione,
            @Param("oraFinePrenotazione") LocalTime oraFinePrenotazione
    );

    @Query("SELECT COUNT(prenotazionePo) " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA")
    Long countPrenotazioniCollaboratorePerData(
            @Param("idPrenotazione") Integer idPrenotazione,
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataPrenotazione") LocalDate dataPrenotazione
    );

    @Query("SELECT prenotazionePo " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA " +
            "AND prenotazionePo.dataPrenotazione BETWEEN :dataInizioAssenza AND :dataFineAssenza " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFineAssenza " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioAssenza")
    List<PrenotazionePo> findPrenotazioniConfermateCheHannoAssenza(
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataInizioAssenza") LocalDate dataInizioAssenza,
            @Param("dataFineAssenza") LocalDate dataFineAssenza,
            @Param("oraInizioAssenza") LocalTime oraInizioAssenza,
            @Param("oraFineAssenza") LocalTime oraFineAssenza
    );

    @Query("SELECT COUNT(prenotazionePo) " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.servizioPo.idServizio = :idServizio " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFinePrenotazione " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioPrenotazione")
    Long countPrenotazioniConfermateServizioAPosti(
            @Param("idPrenotazione") Integer idPrenotazione,
            @Param("idServizio") Integer idServizio,
            @Param("dataPrenotazione") LocalDate dataPrenotazione,
            @Param("oraInizioPrenotazione") LocalTime oraInizioPrenotazione,
            @Param("oraFinePrenotazione") LocalTime oraFinePrenotazione
    );

    @Query("SELECT prenotazionePo " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.enums.StatoPrenotazione.CONFERMATA " +
            "AND prenotazionePo.dataPrenotazione >= :dataDa")
    List<PrenotazionePo> findPrenotazioniFutureConfermateByCollaboratore(
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataDa") LocalDate dataDa
    );

    boolean existsByUtentePo_IdUtente(Integer idUtente);

    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    boolean existsByServizioPo_IdServizio(Integer idServizio);

    List<PrenotazionePo> findByStatoPrenotazione(StatoPrenotazione statoPrenotazione);

    List<PrenotazionePo> findByUtentePo_IdUtenteAndStatoPrenotazione(
            Integer idUtente,
            StatoPrenotazione statoPrenotazione
    );
}