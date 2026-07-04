package com.prenotazioni.Mapper;


import com.prenotazioni.Dto.ServizioTo;
import com.prenotazioni.Po.ServizioPo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServizioMapper {

    ServizioTo toDto(ServizioPo servizioPo);

    ServizioPo toEntity(ServizioTo servizioTo);
}