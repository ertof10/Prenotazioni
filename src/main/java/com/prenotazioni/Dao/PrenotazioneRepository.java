package com.prenotazioni.Dao;

import com.prenotazioni.Po.PrenotazionePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<PrenotazionePo, Integer> {

    List<PrenotazionePo> findByUtentePo_IdUtente(Integer idUtente);

    List<PrenotazionePo> findByServizioPo_IdServizio(Integer idServizio);

    @Query("SELECT CASE WHEN COUNT(prenotazionePo) > 0 THEN true ELSE false END " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.utentePo.idUtente = :idUtente " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.Enums.StatoPrenotazione.CONFERMATA " +
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
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.Enums.StatoPrenotazione.CONFERMATA " +
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
            "AND prenotazionePo.statoPrenotazione = com.prenotazioni.Enums.StatoPrenotazione.CONFERMATA")
    Long countPrenotazioniCollaboratorePerData(
            @Param("idPrenotazione") Integer idPrenotazione,
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataPrenotazione") LocalDate dataPrenotazione
    );

    boolean existsByUtentePo_IdUtente(Integer idUtente);

    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    boolean existsByServizioPo_IdServizio(Integer idServizio);
}