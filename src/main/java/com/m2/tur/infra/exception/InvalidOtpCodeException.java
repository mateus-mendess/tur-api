package com.m2.tur.infra.exception;

public class InvalidOtpCodeException extends TurException {
    public InvalidOtpCodeException(String message) {
        super(message);
    }
}
