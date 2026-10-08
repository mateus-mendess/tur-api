package com.m2.tur.core.exception;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String field, String message) {
        super(field, message);
    }
}
