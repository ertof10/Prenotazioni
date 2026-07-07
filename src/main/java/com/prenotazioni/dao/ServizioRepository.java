package com.prenotazioni.dao;


import com.prenotazioni.po.ServizioPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServizioRepository extends JpaRepository<ServizioPo, Integer> {

    boolean existsByNomeServizio(String nomeServizio);
}