package com.prenotazioni.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AuthRegisterRequest {

    @NotBlank
    @Size(max = 60)
    private String nomeUtente;

    @NotBlank
    @Size(max = 60)
    private String cognomeUtente;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @Size(max = 20)
    @Pattern(regexp = "^$|^[+]?[0-9\\s().-]{6,20}$")
    private String telefonoUtente;
}