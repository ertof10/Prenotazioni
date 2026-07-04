package com.prenotazioni.Dao;

import com.prenotazioni.Po.CalendarioPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CalendarioRepository extends JpaRepository<CalendarioPo, Integer> {

    List<CalendarioPo> findByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    List<CalendarioPo> findByCollaboratorePo_IdCollaboratoreAndAttivoCalendarioTrue(Integer idCollaboratore);

    List<CalendarioPo> findByAttivoCalendarioTrue();

    @Query("SELECT COUNT(calendarioPo) > 0 " +
            "FROM CalendarioPo calendarioPo " +
            "WHERE (:idCalendario IS NULL OR calendarioPo.idCalendario <> :idCalendario) " +
            "AND calendarioPo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND calendarioPo.giornoSettimanaCalendario = :giornoSettimanaCalendario " +
            "AND calendarioPo.oraInizioCalendario < :oraFineCalendario " +
            "AND calendarioPo.oraFineCalendario > :oraInizioCalendario")
    boolean existsCalendarioSovrapposto(Integer idCalendario,
                                        Integer idCollaboratore,
                                        DayOfWeek giornoSettimanaCalendario,
                                        LocalTime oraInizioCalendario,
                                        LocalTime oraFineCalendario);

    @Query("SELECT COUNT(calendarioPo) > 0 " +
            "FROM CalendarioPo calendarioPo " +
            "WHERE calendarioPo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND calendarioPo.giornoSettimanaCalendario = :giornoSettimanaCalendario " +
            "AND calendarioPo.attivoCalendario = true " +
            "AND calendarioPo.oraInizioCalendario <= :oraInizioPrenotazione " +
            "AND calendarioPo.oraFineCalendario >= :oraFinePrenotazione")
    boolean existsDisponibilitaCalendario(Integer idCollaboratore,
                                          DayOfWeek giornoSettimanaCalendario,
                                          LocalTime oraInizioPrenotazione,
                                          LocalTime oraFinePrenotazione);


    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);
}