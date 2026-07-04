package com.prenotazioni.Dao;

import com.prenotazioni.Po.CollaboratorePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaboratoreRepository extends JpaRepository<CollaboratorePo, Integer> {

    boolean existsByEmailCollaboratore(String emailCollaboratore);
}