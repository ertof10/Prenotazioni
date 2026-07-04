package com.prenotazioni.Mapper;

import com.prenotazioni.Dto.CalendarioTo;
import com.prenotazioni.Po.CalendarioPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalendarioMapper {

    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "collaboratorePo.nomeCollaboratore", target = "nomeCollaboratore")
    @Mapping(source = "collaboratorePo.cognomeCollaboratore", target = "cognomeCollaboratore")
    CalendarioTo toDto(CalendarioPo calendarioPo);
}