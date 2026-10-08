package com.m2.tur.core.exception;

public class InvalidCurrentPasswordException extends UnauthorizedException {
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
