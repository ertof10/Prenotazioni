package com.prenotazioni.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthLoginRequest {

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(max = 72)
    private String password;
}