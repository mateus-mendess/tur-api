package com.m2.tur.infra.exception;

public class CategoryAlreadyExistsException extends TurException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
