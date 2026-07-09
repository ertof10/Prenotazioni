package com.prenotazioni.dao;

import com.prenotazioni.po.ServizioPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ServizioRepository extends JpaRepository<ServizioPo, Integer> {

    boolean existsByNomeServizio(String nomeServizio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT servizioPo " +
            "FROM ServizioPo servizioPo " +
            "WHERE servizioPo.idServizio = :idServizio")
    Optional<ServizioPo> findByIdForUpdate(
            @Param("idServizio") Integer idServizio
    );
    Page<ServizioPo> findByAttivoServizioTrue(Pageable pageable);
}