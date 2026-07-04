package com.prenotazioni.Error;

public class ServiceException extends RuntimeException {

    private final AppError error;

    public ServiceException(AppError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ServiceException(AppError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    public AppError getError() {
        return error;
    }
}