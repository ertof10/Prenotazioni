package com.prenotazioni.Mapper;

import com.prenotazioni.Dto.CollaboratoreServizioTo;
import com.prenotazioni.Po.CollaboratoreServizioPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollaboratoreServizioMapper {

    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "servizioPo.idServizio", target = "idServizio")
    CollaboratoreServizioTo toDto(CollaboratoreServizioPo collaboratoreServizioPo);
}