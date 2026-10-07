package com.m2.tur.service;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class OtpData {
    private final String code;
    private final AtomicInteger failedAttempts =  new AtomicInteger(0);

    public OtpData(String code) {
        this.code = code;
    }

    public boolean matches(String inputCode) {
        return code.equals(inputCode);
    }

    public int registerFailure() {
        return failedAttempts.incrementAndGet();
    }
}
