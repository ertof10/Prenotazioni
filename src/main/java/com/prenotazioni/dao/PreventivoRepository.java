package com.prenotazioni.dao;

import com.prenotazioni.enums.StatoPreventivo;
import com.prenotazioni.po.PreventivoPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreventivoRepository extends JpaRepository<PreventivoPo, Integer> {

    Page<PreventivoPo> findByUtentePo_IdUtente(Integer idUtente, Pageable pageable);

    Page<PreventivoPo> findByStatoPreventivo(StatoPreventivo statoPreventivo, Pageable pageable);

    boolean existsByUtentePo_IdUtente(Integer idUtente);

    boolean existsByServizioPo_IdServizio(Integer idServizio);
}