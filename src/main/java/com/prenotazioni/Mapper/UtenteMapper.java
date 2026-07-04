package com.prenotazioni.Mapper;



import com.prenotazioni.Dto.UtenteTo;

import com.prenotazioni.Po.UtentePo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    UtenteTo toDto(UtentePo utentePo);

    UtentePo toEntity(UtenteTo utenteTo);
}