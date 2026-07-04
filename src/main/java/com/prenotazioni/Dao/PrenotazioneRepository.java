package com.prenotazioni.Dao;

import com.prenotazioni.Po.PrenotazionePo;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<PrenotazionePo, Integer> {

    List<PrenotazionePo> findByUtentePo_IdUtente(Integer idUtente);

    List<PrenotazionePo> findByServizioPo_IdServizio(Integer idServizio);

    @Query("SELECT COUNT(prenotazionePo) > 0 " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.utentePo.idUtente = :idUtente " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFinePrenotazione " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioPrenotazione")
    boolean existsPrenotazioneSovrappostaUtente(Integer idPrenotazione,
                                                Integer idUtente,
                                                LocalDate dataPrenotazione,
                                                LocalTime oraInizioPrenotazione,
                                                LocalTime oraFinePrenotazione);

    @Query("SELECT COUNT(prenotazionePo) > 0 " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione " +
            "AND prenotazionePo.oraInizioPrenotazione < :oraFinePrenotazione " +
            "AND prenotazionePo.oraFinePrenotazione > :oraInizioPrenotazione")
    boolean existsPrenotazioneSovrappostaCollaboratore(Integer idPrenotazione,
                                                       Integer idCollaboratore,
                                                       LocalDate dataPrenotazione,
                                                       LocalTime oraInizioPrenotazione,
                                                       LocalTime oraFinePrenotazione);

    @Query("SELECT COUNT(prenotazionePo) " +
            "FROM PrenotazionePo prenotazionePo " +
            "WHERE (:idPrenotazione IS NULL OR prenotazionePo.idPrenotazione <> :idPrenotazione) " +
            "AND prenotazionePo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND prenotazionePo.dataPrenotazione = :dataPrenotazione")
    Long countPrenotazioniCollaboratorePerData(Integer idPrenotazione,
                                               Integer idCollaboratore,
                                               LocalDate dataPrenotazione);


    boolean existsByUtentePo_IdUtente(Integer idUtente);

    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    boolean existsByServizioPo_IdServizio(Integer idServizio);
}