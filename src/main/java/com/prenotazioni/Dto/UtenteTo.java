package com.prenotazioni.Dto;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UtenteTo {

    private Integer idUtente;

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 60, message = "Nome troppo lungo")
    private String nomeUtente;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Size(max = 60, message = "Cognome troppo lungo")
    private String cognomeUtente;

    @NotBlank(message = "L'email non può essere vuota")
    @Email(message = "Formato email non valido")
    @Size(max = 120, message = "Email troppo lunga")
    private String emailUtente;

    @Size(max = 20, message = "Telefono troppo lungo")
    private String telefonoUtente;



    public UtenteTo() {
    }
}