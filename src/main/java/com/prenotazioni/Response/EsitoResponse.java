package com.prenotazioni.Response;

import lombok.Data;

@Data
public class EsitoResponse {

    private String message;

    public EsitoResponse(String message) {
        this.message = message;
    }
}