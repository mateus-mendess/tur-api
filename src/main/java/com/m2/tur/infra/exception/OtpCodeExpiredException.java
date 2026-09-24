package com.m2.tur.infra.exception;

public class OtpCodeExpiredException extends TurException {
    public OtpCodeExpiredException(String message) {
        super(message);
    }
}
