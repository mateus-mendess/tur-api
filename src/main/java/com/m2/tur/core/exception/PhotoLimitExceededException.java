package com.m2.tur.infra.exception;

public class PhotoLimitExceededException extends BusinessException {
    public PhotoLimitExceededException(String message) {
        super(message);
    }
}
