package com.prenotazioni.dto;

import com.prenotazioni.enums.AppRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponse {

    private String token;
    private String tokenType;
    private Integer idAccount;
    private Integer idUtente;
    private Integer idCollaboratore;
    private String email;
    private AppRole ruolo;
}