package com.m2.tur.infra.exception;

public class CategoryAlreadyExistsException extends BusinessException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
