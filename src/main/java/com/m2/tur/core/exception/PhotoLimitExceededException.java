package com.m2.tur.core.exception;

public class PhotoLimitExceededException extends BusinessException {
    public PhotoLimitExceededException(String message) {
        super(message);
    }
}
