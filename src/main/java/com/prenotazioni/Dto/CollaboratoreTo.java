package com.prenotazioni.Dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CollaboratoreTo {

    private Integer idCollaboratore;

    @NotBlank(message = "Il nome collaboratore è obbligatorio")
    @Size(max = 60, message = "Nome collaboratore troppo lungo")
    private String nomeCollaboratore;

    @NotBlank(message = "Il cognome collaboratore è obbligatorio")
    @Size(max = 60, message = "Cognome collaboratore troppo lungo")
    private String cognomeCollaboratore;

    @NotBlank(message = "L'email collaboratore è obbligatoria")
    @Email(message = "Formato email collaboratore non valido")
    @Size(max = 120, message = "Email collaboratore troppo lunga")
    private String emailCollaboratore;

    @Size(max = 20, message = "Telefono collaboratore troppo lungo")
    private String telefonoCollaboratore;

    @NotBlank(message = "La specializzazione collaboratore è obbligatoria")
    @Size(max = 100, message = "Specializzazione collaboratore troppo lunga")
    private String specializzazioneCollaboratore;

    @Size(max = 500, message = "Descrizione collaboratore troppo lunga")
    private String descrizioneCollaboratore;

    private Boolean attivoCollaboratore;

    private LocalDateTime dataCreazioneCollaboratore;

    private LocalDateTime dataModificaCollaboratore;

    public CollaboratoreTo() {
    }
}