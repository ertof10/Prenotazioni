package com.prenotazioni.mapper;

import com.prenotazioni.dto.AssenzaCollaboratoreTo;
import com.prenotazioni.po.AssenzaCollaboratorePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssenzaCollaboratoreMapper {

    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "collaboratorePo.nomeCollaboratore", target = "nomeCollaboratore")
    @Mapping(source = "collaboratorePo.cognomeCollaboratore", target = "cognomeCollaboratore")
    AssenzaCollaboratoreTo toDto(AssenzaCollaboratorePo assenzaCollaboratorePo);
}