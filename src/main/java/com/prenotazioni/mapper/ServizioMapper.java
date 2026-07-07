package com.prenotazioni.mapper;


import com.prenotazioni.dto.ServizioTo;
import com.prenotazioni.po.ServizioPo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServizioMapper {

    ServizioTo toDto(ServizioPo servizioPo);

    ServizioPo toEntity(ServizioTo servizioTo);
}