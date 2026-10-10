package com.m2.tur.core.exception;

public class InvalidCurrentPasswordException extends BusinessException {
    public InvalidCurrentPasswordException(String message, String field) {
        super(message, field);
    }
}
