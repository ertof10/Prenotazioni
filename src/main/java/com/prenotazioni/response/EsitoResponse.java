package com.prenotazioni.response;

import lombok.Data;

@Data
public class EsitoResponse {

    private String message;

    public EsitoResponse(String message) {
        this.message = message;
    }
}