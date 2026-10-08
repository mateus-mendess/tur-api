package com.m2.tur.infra.exception;

import lombok.Getter;

@Getter
public class BusinessException extends TurException {
    private String field;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String field, String message) {
        super(message);
        this.field = field;
    }
}
