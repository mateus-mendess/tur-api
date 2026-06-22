package com.m2.tur.infra.exception;

public class EmailAlreadyExistsException extends TurException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
