package com.prenotazioni.Mapper;

import com.prenotazioni.Dto.PrenotazioneTo;
import com.prenotazioni.Po.PrenotazionePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrenotazioneMapper {

    @Mapping(source = "utentePo.idUtente", target = "idUtente")
    @Mapping(source = "servizioPo.idServizio", target = "idServizio")
    @Mapping(source = "collaboratorePo.idCollaboratore", target = "idCollaboratore")
    @Mapping(source = "collaboratorePo.nomeCollaboratore", target = "nomeCollaboratore")
    @Mapping(source = "collaboratorePo.cognomeCollaboratore", target = "cognomeCollaboratore")
    PrenotazioneTo toDto(PrenotazionePo prenotazionePo);
}