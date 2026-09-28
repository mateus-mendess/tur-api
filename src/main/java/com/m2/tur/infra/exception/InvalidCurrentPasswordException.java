package com.m2.tur.infra.exception;

public class InvalidCurrentPasswordException extends UnauthorizedException {
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
