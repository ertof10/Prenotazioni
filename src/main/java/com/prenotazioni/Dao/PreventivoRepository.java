package com.prenotazioni.Dao;

import com.prenotazioni.Enums.StatoPreventivo;
import com.prenotazioni.Po.PreventivoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreventivoRepository extends JpaRepository<PreventivoPo, Integer> {
    List<PreventivoPo> findByUtentePo_IdUtente(Integer idUtente);

    List<PreventivoPo> findByStatoPreventivo(StatoPreventivo statoPreventivo);

    boolean existsByUtentePo_IdUtente(Integer idUtente);

    boolean existsByServizioPo_IdServizio(Integer idServizio);
}