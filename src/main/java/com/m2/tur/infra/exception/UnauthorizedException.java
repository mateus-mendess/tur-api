package com.m2.tur.infra.exception;

public class UnauthorizedException extends TurException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
