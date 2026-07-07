package com.m2.tur.infra.exception;

public class PhotoLimitExceededException extends TurException {
    public PhotoLimitExceededException(String message) {
        super(message);
    }
}
