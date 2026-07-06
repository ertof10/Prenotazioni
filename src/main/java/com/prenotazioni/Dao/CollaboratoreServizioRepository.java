package com.prenotazioni.Dao;

import com.prenotazioni.Po.CollaboratoreServizioPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratoreServizioRepository extends JpaRepository<CollaboratoreServizioPo, Integer> {

    @Query("SELECT COUNT(collaboratoreServizioPo) > 0 " +
            "FROM CollaboratoreServizioPo collaboratoreServizioPo " +
            "WHERE collaboratoreServizioPo.collaboratorePo.idCollaboratore = :idCollaboratore " +
            "AND collaboratoreServizioPo.servizioPo.idServizio = :idServizio " +
            "AND collaboratoreServizioPo.attivoCollaboratoreServizio = true")
    boolean existsAssociazioneAttiva(Integer idCollaboratore, Integer idServizio);

    List<CollaboratoreServizioPo> findByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    List<CollaboratoreServizioPo> findByServizioPo_IdServizio(Integer idServizio);

    List<CollaboratoreServizioPo> findByServizioPo_IdServizioAndAttivoCollaboratoreServizioTrue(Integer idServizio);

    boolean existsByCollaboratorePo_IdCollaboratore(Integer idCollaboratore);

    boolean existsByServizioPo_IdServizio(Integer idServizio);

}