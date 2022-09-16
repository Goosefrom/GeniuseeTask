package com.goose.cinema.exception;

import lombok.Getter;

@Getter
public class CinemaException extends RuntimeException {

    private final ErrorType errorType;

    public CinemaException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
}
