package com.prenotazioni.Dao;

import com.prenotazioni.Po.AssenzaCollaboratorePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AssenzaCollaboratoreRepository extends JpaRepository<AssenzaCollaboratorePo, Integer> {

    List<AssenzaCollaboratorePo> findByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AssenzaCollaboratorePo a " +
            "WHERE a.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND a.attivoAssenzaCollaboratore = true " +
            "AND (:idAssenzaCollaboratore IS NULL OR a.idAssenzaCollaboratore <> :idAssenzaCollaboratore) " +
            "AND :dataInizioAssenza <= a.dataFineAssenza " +
            "AND :dataFineAssenza >= a.dataInizioAssenza " +
            "AND :oraInizioAssenza < a.oraFineAssenza " +
            "AND :oraFineAssenza > a.oraInizioAssenza")
    boolean existsAssenzaSovrapposta(
            @Param("idAssenzaCollaboratore") Integer idAssenzaCollaboratore,
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataInizioAssenza") LocalDate dataInizioAssenza,
            @Param("dataFineAssenza") LocalDate dataFineAssenza,
            @Param("oraInizioAssenza") LocalTime oraInizioAssenza,
            @Param("oraFineAssenza") LocalTime oraFineAssenza
    );

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AssenzaCollaboratorePo a " +
            "WHERE a.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND a.attivoAssenzaCollaboratore = true " +
            "AND :dataPrenotazione BETWEEN a.dataInizioAssenza AND a.dataFineAssenza " +
            "AND :oraInizioPrenotazione < a.oraFineAssenza " +
            "AND :oraFinePrenotazione > a.oraInizioAssenza")
    boolean existsAssenzaAttivaSuPrenotazione(
            @Param("idCollaboratore") Integer idCollaboratore,
            @Param("dataPrenotazione") LocalDate dataPrenotazione,
            @Param("oraInizioPrenotazione") LocalTime oraInizioPrenotazione,
            @Param("oraFinePrenotazione") LocalTime oraFinePrenotazione
    );


}