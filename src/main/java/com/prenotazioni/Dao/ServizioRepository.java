package com.prenotazioni.Dao;


import com.prenotazioni.Po.ServizioPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServizioRepository extends JpaRepository<ServizioPo, Integer> {

    boolean existsByNomeServizio(String nomeServizio);
}